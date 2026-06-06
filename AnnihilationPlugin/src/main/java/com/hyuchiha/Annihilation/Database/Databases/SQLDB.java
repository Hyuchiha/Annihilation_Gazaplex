package com.hyuchiha.Annihilation.Database.Databases;

import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Database.Base.Database;
import com.hyuchiha.Annihilation.Database.StatType;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Output.Output;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL backend backed by a HikariCP connection pool.
 *
 * <p>Concrete subclasses (MySQLDB / SQLiteDB) provide a {@link HikariConfig} via
 * {@link #buildHikariConfig()} plus the dialect-specific DDL/DML query strings.
 *
 * <p>The pool replaces the previous single shared {@link Connection} model: each
 * DB op acquires a connection from the pool, uses it inside a try-with-resources,
 * and releases it back. HikariCP handles keep-alive, validation and concurrency,
 * so we no longer synchronize methods or run a manual ping task.
 */
public abstract class SQLDB extends Database {
  protected static final String ACCOUNTS_TABLE = "annihilation_accounts";
  protected static final String KITS_TABLE = "annihilation_kits";
  protected static final String KITS_UNLOCKED_TABLE = "annihilation_kits_unlocked";

  private HikariDataSource dataSource;

  public SQLDB(Main plugin) {
    super(plugin);
  }

  @Override
  public boolean init() {
    try {
      this.dataSource = new HikariDataSource(buildHikariConfig());
    } catch (RuntimeException e) {
      Output.logError("Failed to initialize Hikari pool: " + e.getMessage());
      return false;
    }

    try (Connection conn = dataSource.getConnection();
         Statement stmt = conn.createStatement()) {
      stmt.execute(getDatabaseQuery());
      stmt.execute(getDatabaseKitsQuery());
      stmt.execute(getDatabaseKitsUnlockedQuery());
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }

    insertMissingKits();
    return true;
  }

  /** Subclasses build a Hikari config tailored to their JDBC driver. */
  protected abstract HikariConfig buildHikariConfig();

  @Override
  public void close() {
    super.close(); // flushes cached accounts via saveAccount
    if (this.dataSource != null && !this.dataSource.isClosed()) {
      this.dataSource.close();
    }
  }

  private void insertMissingKits() {
    for (Kit kit : Kit.values()) {
      try (Connection conn = dataSource.getConnection()) {
        int id = getIdOfElement(conn, kit.name());

        if (id < 0) {
          try (Statement stmt = conn.createStatement()) {
            stmt.execute(getInsertKitQuery(kit));
          }
        }
      } catch (SQLException e) {
        Output.logError(e.getMessage());
      }
    }
  }

  @Override
  protected List<Account> loadTopAccountsByStatType(StatType type, int size) {
    String sql = "SELECT * FROM " + ACCOUNTS_TABLE + " ORDER BY " + type.name().toLowerCase() + " DESC LIMIT ?";

    List<Account> topAccounts = new ArrayList<>();

    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, size);
      try (ResultSet set = ps.executeQuery()) {
        while (set.next()) {
          topAccounts.add(new Account(
              set.getString("uuid"),
              set.getString("username"),
              set.getInt("kills"),
              set.getInt("deaths"),
              set.getInt("wins"),
              set.getInt("losses"),
              set.getInt("nexus_damage")
          ));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return topAccounts;
  }

  @Override
  protected void createAccountAndAddToDatabase(Account account) {
    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(getCreateAccountQuery())) {
      ps.setString(1, account.getUUID());
      ps.setString(2, account.getName());
      ps.execute();
      cachedAccounts.put(account.getUUID(), account);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected Account loadAccount(String uuid) {
    String query = "SELECT * FROM " + ACCOUNTS_TABLE + " WHERE UPPER(uuid) LIKE UPPER(?)";

    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, uuid);

      Account account = null;
      try (ResultSet set = ps.executeQuery()) {
        if (set.next()) {
          account = new Account(
              set.getString("uuid"),
              set.getString("username"),
              set.getInt("kills"),
              set.getInt("deaths"),
              set.getInt("wins"),
              set.getInt("losses"),
              set.getInt("nexus_damage")
          );
        }
      }

      if (account != null) {
        account.setKits(getKitsFromAccount(conn, uuid));
        cachedAccounts.put(uuid, account);
      }
      return account;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public void saveAccount(Account account) {
    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(getUpdateAccountQuery())) {
      ps.setString(1, account.getName());
      ps.setInt(2, account.getKills());
      ps.setInt(3, account.getDeaths());
      ps.setInt(4, account.getWins());
      ps.setInt(5, account.getLosses());
      ps.setInt(6, account.getNexus_damage());
      ps.setString(7, account.getUUID());
      ps.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void addUnlockedKit(String uuid, String kit) {
    try (Connection conn = dataSource.getConnection()) {
      int idKit = getIdOfElement(conn, kit);

      String query = "INSERT INTO `" + KITS_UNLOCKED_TABLE + "`(`clv_kit`,`player`) VALUES (?, ?);";
      try (PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setInt(1, idKit);
        ps.setString(2, uuid);
        ps.execute();
      }

      Account cached = cachedAccounts.get(uuid);
      if (cached != null) {
        cached.getKits().add(Kit.valueOf(kit.toUpperCase()));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Looks up the integer id of a kit by name. Reuses the caller's connection so the
   * insert/update can stay on the same physical connection if needed.
   */
  private int getIdOfElement(Connection conn, String name) throws SQLException {
    String query = "SELECT clv_kit FROM " + KITS_TABLE + " WHERE `name` = ?";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, name);
      try (ResultSet set = ps.executeQuery()) {
        if (set.next()) {
          return set.getInt("clv_kit");
        }
      }
    }
    return -1;
  }

  private List<Kit> getKitsFromAccount(Connection conn, String uuid) throws SQLException {
    List<Kit> kits = new ArrayList<>();

    String query = "SELECT " + KITS_TABLE + ".name FROM " + KITS_UNLOCKED_TABLE
        + " JOIN " + KITS_TABLE + " ON " + KITS_TABLE + ".clv_kit = " + KITS_UNLOCKED_TABLE + ".clv_kit "
        + "WHERE player = ?;";

    try (PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, uuid);
      try (ResultSet set = ps.executeQuery()) {
        while (set.next()) {
          kits.add(Kit.valueOf(set.getString("name")));
        }
      }
    }
    return kits;
  }

  protected abstract String getDatabaseQuery();

  protected abstract String getDatabaseKitsQuery();

  protected abstract String getDatabaseKitsUnlockedQuery();

  protected abstract String getInsertKitQuery(Kit kit);

  /**
   * Parameterized INSERT template for a new account. Placeholders, in order:
   * {@code 1 = uuid}, {@code 2 = username}. Stat columns default to 0.
   */
  protected abstract String getCreateAccountQuery();

  /**
   * Parameterized UPDATE template for an existing account. Placeholders, in order:
   * {@code 1 = username, 2 = kills, 3 = deaths, 4 = wins, 5 = losses,
   * 6 = nexus_damage, 7 = uuid (WHERE)}.
   */
  protected abstract String getUpdateAccountQuery();
}

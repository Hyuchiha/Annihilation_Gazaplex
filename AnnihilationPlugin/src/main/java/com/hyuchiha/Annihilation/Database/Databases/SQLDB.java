package com.hyuchiha.Annihilation.Database.Databases;

import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Database.Base.Database;
import com.hyuchiha.Annihilation.Database.StatType;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Output.Output;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class SQLDB extends Database {
  private static final String ACCOUNTS_TABLE = "annihilation_accounts";
  private static final String ACCOUNTS_QUERY = "CREATE TABLE IF NOT EXISTS `" + ACCOUNTS_TABLE + "` ("
                                                   + "  `uuid` varchar(36) NOT NULL,"
                                                   + "  `username` varchar(16) NOT NULL,"
                                                   + "  `kills` int(16) NOT NULL,"
                                                   + "  `deaths` int(16) NOT NULL,"
                                                   + "  `wins` int(16) NOT NULL,"
                                                   + "  `losses` int(16) NOT NULL, "
                                                   + "  `nexus_damage` int(16) NOT NULL,"
                                                   + "  PRIMARY KEY (`uuid`),"
                                                   + "  UNIQUE KEY `uuid` (`uuid`)"
                                                   + ") ENGINE=InnoDB;";
  private Main plugin;
  private Connection connection;


  public SQLDB(Main plugin) {
    super(plugin);

    this.plugin = plugin;

    plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
      try {
        if (SQLDB.this.connection != null && !SQLDB.this.connection.isClosed()) {
          SQLDB.this.connection.createStatement().execute("/* ping */ SELECT 1");
        }
      } catch (SQLException e) {
        SQLDB.this.connection = SQLDB.this.getNewConnection();
      }
    }, 60 * 20, 60 * 20);
  }

  public boolean init() {
    super.init();

    return checkConnection();
  }

  public boolean checkConnection() {
    try {
      if (this.connection == null || this.connection.isClosed()) {
        this.connection = getNewConnection();

        if (this.connection == null || this.connection.isClosed()) {
          return false;
        }

        query(ACCOUNTS_QUERY);
      }

    } catch (SQLException e) {
      e.printStackTrace();

      return false;
    }

    return true;
  }

  protected abstract Connection getNewConnection();

  public boolean query(String sql) throws SQLException {
    return this.connection.createStatement().execute(sql);
  }

  public void close() {
    super.close();

    try {
      if (this.connection != null)
        this.connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  protected List<Account> loadTopAccountsByStatType(StatType type, int size) {
    checkConnection();

    String sql = "SELECT * FROM " + ACCOUNTS_TABLE + " ORDER BY " + type.name().toLowerCase() + " DESC limit " + size;

    List<Account> topAccounts = new ArrayList<Account>();

    try {
      ResultSet set = this.connection.createStatement().executeQuery(sql);

      while (set.next()) {
        Account account = new Account(
            set.getString("uuid"),
            set.getString("username"),
            set.getInt("kills"),
            set.getInt("deaths"),
            set.getInt("wins"),
            set.getInt("losses"),
            set.getInt("nexus_damage")
        );

        topAccounts.add(account);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return topAccounts;
  }

  protected void createAccountAndAddToDatabase(Account account) {
    checkConnection();

    try {
      String query = "INSERT IGNORE INTO `" + ACCOUNTS_TABLE + "` (`uuid`, `username`, `kills`, "
                         + "`deaths`, `wins`, `losses`, `nexus_damage`) VALUES "
                         + "('"
                         + account.getUUID() + "', '"
                         + account.getName()
                         + "', '0', '0', '0', '0', '0');";

      PreparedStatement statement = this.connection.prepareStatement(query);

      if (statement.execute()) {
        statement.close();
      }

      this.cachedAccounts.put(account.getUUID(), account);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  protected Account loadAccount(String uuid) {
    checkConnection();

    Output.log("Loading account with uuid: " + uuid);

    try {
      String query = "SELECT * FROM " + ACCOUNTS_TABLE + " WHERE UPPER(uuid) LIKE UPPER(?)";

      PreparedStatement statement = this.connection.prepareStatement(query);

      statement.setString(1, uuid);

      ResultSet set = statement.executeQuery();

      Account account = null;

      while (set.next()) {
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

      set.close();

      this.cachedAccounts.put(uuid, account);

      return account;
    } catch (SQLException e) {
      e.printStackTrace();

      return null;
    }
  }

  public void saveAccount(Account account) {
    checkConnection();


    try {
      String query = "UPDATE `" + ACCOUNTS_TABLE + "` SET "
                         + "`username`= '" + account.getName() + "',"
                         + "`kills`= '" + account.getKills() + "',"
                         + "`deaths`='" + account.getDeaths() + "',"
                         + "`wins`='" + account.getWins() + "',"
                         + "`losses`='" + account.getLosses() + "',"
                         + "`nexus_damage`='" + account.getNexus_damage() + "' "
                         + "WHERE `uuid`='" + account.getUUID() + "';";

      PreparedStatement statement = this.connection.prepareStatement(query);

      if (statement.execute()) {
        statement.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}

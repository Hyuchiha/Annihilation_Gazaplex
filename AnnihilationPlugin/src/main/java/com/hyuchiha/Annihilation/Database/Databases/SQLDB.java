package com.hyuchiha.Annihilation.Database.Databases;

import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Database.Base.Database;
import com.hyuchiha.Annihilation.Database.StatType;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Output.Output;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public abstract class SQLDB extends Database {
  protected static final String ACCOUNTS_TABLE        = "annihilation_accounts";
  protected static final String KITS_TABLE            = "annihilation_kits";
  protected static final String KITS_UNLOCKED_TABLE   = "annihilation_kits_unlocked";

  private Main plugin;
  private Connection connection;


  public SQLDB(Main plugin) {
    super(plugin);

    this.plugin = plugin;

    plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
      try {
        if (connection != null && !connection.isClosed()) {
          connection.createStatement().execute("/* ping */ SELECT 1");
        }
      } catch (SQLException e) {
        connection = getNewConnection();
      }
    }, 60 * 20, 60 * 20);
  }

  public boolean init() {
    return checkConnection();
  }

  public boolean checkConnection() {
    try {
      if (connection == null || connection.isClosed()) {
        connection = getNewConnection();

        if (connection == null || connection.isClosed()) {
          return false;
        }

        String ACCOUNTS_QUERY = getDatabaseQuery();
        query(ACCOUNTS_QUERY);

        String KITS_QUERY = getDatabaseKitsQuery();
        query(KITS_QUERY);

        String KITS_UNLOCKED_QUERY = getDatabaseKitsUnlockedQuery();
        query(KITS_UNLOCKED_QUERY);


        for (Kit kit: Kit.values()) {
          String query = getInsertKitQuery(kit);

          query(query);
        }

      }

    } catch (SQLException e) {
      e.printStackTrace();

      return false;
    }

    return true;
  }

  protected abstract Connection getNewConnection();

  public boolean query(String sql) throws SQLException {
    return connection.createStatement().execute(sql);
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
      ResultSet set = connection.createStatement().executeQuery(sql);

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
      String query = getCreateAccountQuery(account);

      PreparedStatement statement = connection.prepareStatement(query);

      if (statement.execute()) {
        statement.close();
      }

      cachedAccounts.put(account.getUUID(), account);
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

      if (account != null){
        List<Kit> kits = getKitsFromAccount(uuid);
        account.setKits(kits);
      }

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
      String query = getUpdateAccountQuery(account);

      PreparedStatement statement = this.connection.prepareStatement(query);

      if (statement.execute()) {
        statement.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void addUnlockedKit(String uuid, String kit) {
    checkConnection();

    try {
      int idKit = getIdOfElement(kit);

      String query = "INSERT INTO `" + KITS_UNLOCKED_TABLE + "`(`clv_kit`,`player`)  VALUES "
              + "('" + idKit + "', '"+ uuid +"');";

      PreparedStatement statement = connection.prepareStatement(query);

      if(statement.execute()){
        statement.close();
      }

      Account account = null;

      for (Account cache: cachedAccounts.values()){
        if(cache.getUUID().equalsIgnoreCase(uuid)){
          account = cache;
        }
      }

      if(account != null){
        account.getKits().add(Kit.valueOf(kit.toUpperCase()));

        cachedAccounts.put(uuid, account);
      }

    }catch (SQLException e){
      e.printStackTrace();
    }
  }

  private int getIdOfElement(String name) {
    checkConnection();

    try {
      String query = "SELECT * FROM " + KITS_TABLE + " WHERE `name` = '" + name + "'";

      PreparedStatement statement = connection.prepareStatement(query);

      ResultSet set = statement.executeQuery();

      int id = -1;

      while (set.next()) {
        id = set.getInt("clv_kit");
      }

      return id;

    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return -1;
  }

  private List<Kit> getKitsFromAccount(String uuid){
    checkConnection();

    List<Kit> kits = new ArrayList<>();

    try {
      String query = "SELECT "+ KITS_TABLE + ".name from " + KITS_UNLOCKED_TABLE
              + " JOIN " + KITS_TABLE + " where "+ KITS_TABLE +".clv_kit = "+ KITS_UNLOCKED_TABLE +".clv_kit " +
              "AND player = '" + uuid + "';";

      ResultSet set = connection.createStatement().executeQuery(query);

      while (set.next()) {
        String kitName = set.getString("name");

        kits.add(Kit.valueOf(kitName));
      }

    }catch (SQLException ex){
      ex.printStackTrace();
    }

    return kits;
  }

  protected abstract String getDatabaseQuery();
  protected abstract String getDatabaseKitsQuery();
  protected abstract String getDatabaseKitsUnlockedQuery();

  protected abstract String getInsertKitQuery(Kit kit);

  protected abstract String getCreateAccountQuery(Account account);
  protected abstract String getUpdateAccountQuery(Account account);
}

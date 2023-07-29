package com.hyuchiha.Annihilation.Database.Databases;

import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class MySQLDB extends SQLDB {
  private final Plugin plugin;
  private boolean useSSL;
  private String keystorePassword;

  public MySQLDB(Main plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  protected Connection getNewConnection() {
    ConfigurationSection config = getConfigSection();

    try {
      Class.forName("com.mysql.cj.jdbc.Driver");

      String url = "jdbc:mysql://" + config.getString("host") + ":" + config.getString("port") + "/" + config.getString("name");

      Properties properties = new Properties();
      properties.setProperty("user", config.getString("user"));
      properties.setProperty("password", config.getString("pass"));

      boolean useSSL = config.getBoolean("useSSL", false);
      if (useSSL) {
        properties.setProperty("useSSL", "true");
        properties.setProperty("sslMode", "VERIFY_CA");

        String keystorePassword = config.getString("keystorePassword", "");
        if (!keystorePassword.isEmpty()) {
          properties.setProperty("trustCertificateKeyStorePassword", keystorePassword);
        }
      }

      return DriverManager.getConnection(url, properties);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  protected String getDatabaseQuery() {
    return "CREATE TABLE IF NOT EXISTS `" + ACCOUNTS_TABLE + "` (" +
            "`uuid` varchar(36) NOT NULL, " +
            "`username` varchar(16) NOT NULL, " +
            "`kills` int(16) NOT NULL, " +
            "`deaths` int(16) NOT NULL, " +
            "`wins` int(16) NOT NULL, " +
            "`losses` int(16) NOT NULL, " +
            "`nexus_damage` int(16) NOT NULL, " +
            "PRIMARY KEY (`uuid`), " +
            "UNIQUE KEY `uuid` (`uuid`)) " +
            "ENGINE=InnoDB;";
  }

  @Override
  protected String getDatabaseKitsQuery() {
    return "CREATE TABLE IF NOT EXISTS `" + KITS_TABLE + "` ( "
            + "`clv_kit` int(6) NOT NULL AUTO_INCREMENT,"
            + "`name` varchar(45) NOT NULL,"
            + "PRIMARY KEY (`clv_kit`), "
            + "UNIQUE KEY `clv_kit` (`clv_kit`) ) "
            + "ENGINE=InnoDB AUTO_INCREMENT=1";
  }

  @Override
  protected String getDatabaseKitsUnlockedQuery() {
    return "CREATE TABLE IF NOT EXISTS `" + KITS_UNLOCKED_TABLE + "` "
            + "(`clv_kit` int(6) NOT NULL,"
            + "`player` varchar(36) NOT NULL, "
            + "PRIMARY KEY (`clv_kit` , `player` ) ,"
            + "FOREIGN KEY (`clv_kit`) REFERENCES " + KITS_TABLE + "(`clv_kit`), "
            + "FOREIGN KEY (`player`) REFERENCES " + ACCOUNTS_TABLE + "(`uuid`) )  "
            + "ENGINE=InnoDB;";
  }

  @Override
  protected String getInsertKitQuery(Kit kit) {
    return "INSERT IGNORE INTO `" + KITS_TABLE + "`(`name`)  VALUES "
            + "('" + kit.name().toUpperCase() + "');";
  }

  @Override
  protected String getCreateAccountQuery(Account account) {
    return "INSERT IGNORE INTO `" + ACCOUNTS_TABLE + "` (`uuid`, `username`, `kills`, "
            + "`deaths`, `wins`, `losses`, `nexus_damage`) VALUES "
            + "('"
            + account.getUUID() + "', '"
            + account.getName()
            + "', '0', '0', '0', '0', '0');";
  }

  @Override
  protected String getUpdateAccountQuery(Account account) {
    return "UPDATE `" + ACCOUNTS_TABLE + "` SET "
            + "`username`= '" + account.getName() + "',"
            + "`kills`= '" + account.getKills() + "',"
            + "`deaths`='" + account.getDeaths() + "',"
            + "`wins`='" + account.getWins() + "',"
            + "`losses`='" + account.getLosses() + "',"
            + "`nexus_damage`='" + account.getNexus_damage() + "' "
            + "WHERE `uuid`='" + account.getUUID() + "';";
  }

  private ConfigurationSection getConfigSection() {
    return this.plugin.getConfig().getConfigurationSection("Database");
  }
}

package com.hyuchiha.Annihilation.Database.Databases;

import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLDB extends SQLDB {
  private final Plugin plugin;

  public MySQLDB(Main plugin) {
    super(plugin);
    this.plugin = plugin;
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

  protected Connection getNewConnection() {
    ConfigurationSection config = getConfigSection();

    try {
      Class.forName("com.mysql.jdbc.Driver");

      String url = "jdbc:mysql://" + config.getString("host") + ":" + config.getString("port") + "/" + config.getString("name");

      return DriverManager.getConnection(url, config.getString("user"), config.getString("pass"));
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
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
    return null;
  }

  private ConfigurationSection getConfigSection() {
    return this.plugin.getConfig().getConfigurationSection("Database");
  }
}

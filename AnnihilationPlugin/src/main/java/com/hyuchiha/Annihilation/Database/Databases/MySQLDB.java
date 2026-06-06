package com.hyuchiha.Annihilation.Database.Databases;

import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Main;
import com.zaxxer.hikari.HikariConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

public class MySQLDB extends SQLDB {
  private final Plugin plugin;

  public MySQLDB(Main plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  @Override
  protected HikariConfig buildHikariConfig() {
    ConfigurationSection config = getConfigSection();

    HikariConfig hc = new HikariConfig();
    String url = "jdbc:mysql://" + config.getString("host") + ":" + config.getString("port") + "/"
        + config.getString("name")
        + "?useSSL=false&autoReconnect=true&allowPublicKeyRetrieval=true";

    hc.setJdbcUrl(url);
    hc.setUsername(config.getString("user"));
    hc.setPassword(config.getString("pass"));
    hc.setDriverClassName("com.mysql.jdbc.Driver");
    hc.setPoolName("Annihilation-MySQL");
    // Bounded pool sized for a Spigot server: a handful of concurrent saves at game-end
    // is the realistic peak; tune via config if needed.
    hc.setMaximumPoolSize(config.getInt("pool-size", 10));
    hc.setMinimumIdle(2);
    hc.setConnectionTimeout(10_000);
    hc.setIdleTimeout(600_000);
    hc.setMaxLifetime(1_800_000);
    return hc;
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
  protected String getCreateAccountQuery() {
    return "INSERT IGNORE INTO `" + ACCOUNTS_TABLE + "` (`uuid`, `username`, `kills`, "
        + "`deaths`, `wins`, `losses`, `nexus_damage`) VALUES (?, ?, 0, 0, 0, 0, 0);";
  }

  @Override
  protected String getUpdateAccountQuery() {
    return "UPDATE `" + ACCOUNTS_TABLE + "` SET "
        + "`username`=?, "
        + "`kills`=?, "
        + "`deaths`=?, "
        + "`wins`=?, "
        + "`losses`=?, "
        + "`nexus_damage`=? "
        + "WHERE `uuid`=?;";
  }

  private ConfigurationSection getConfigSection() {
    return this.plugin.getConfig().getConfigurationSection("Database");
  }
}

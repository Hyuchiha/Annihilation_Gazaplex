package com.hyuchiha.Annihilation.Database.Databases;

import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Output.Output;
import com.zaxxer.hikari.HikariConfig;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class SQLiteDB extends SQLDB {

  private final Plugin plugin;

  public SQLiteDB(Main plugin) {
    super(plugin);
    this.plugin = plugin;
  }

  @Override
  protected HikariConfig buildHikariConfig() {
    File dataFolder = new File(plugin.getDataFolder(), "database.db");

    if (!dataFolder.exists()) {
      try {
        dataFolder.createNewFile();
      } catch (IOException e) {
        Output.logError("File write error: database.db");
      }
    }

    HikariConfig hc = new HikariConfig();
    hc.setJdbcUrl("jdbc:sqlite:" + dataFolder.getAbsolutePath());
    hc.setDriverClassName("org.sqlite.JDBC");
    hc.setPoolName("Annihilation-SQLite");
    // SQLite serializes writes at the file level. Pool size 1 keeps things simple
    // and avoids "database is locked" errors under contention.
    hc.setMaximumPoolSize(1);
    hc.setMinimumIdle(1);
    hc.setConnectionTimeout(10_000);
    return hc;
  }

  @Override
  protected String getDatabaseQuery() {
    return "CREATE TABLE IF NOT EXISTS `" + ACCOUNTS_TABLE + "` (" +
        "  `uuid` varchar(36) NOT NULL PRIMARY KEY," +
        "  `username` varchar(16) NOT NULL," +
        "  `kills` int(16) NOT NULL," +
        "  `deaths` int(16) NOT NULL," +
        "  `wins` int(16) NOT NULL," +
        "  `losses` int(16) NOT NULL," +
        "  `nexus_damage` int(16) NOT NULL" +
        ");";
  }

  @Override
  protected String getDatabaseKitsQuery() {
    return "CREATE TABLE IF NOT EXISTS `" + KITS_TABLE + "` (" +
        "  `clv_kit` INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," +
        "  `name` varchar(45) NOT NULL" +
        ")";
  }

  @Override
  protected String getDatabaseKitsUnlockedQuery() {
    return "CREATE TABLE IF NOT EXISTS `" + KITS_UNLOCKED_TABLE + "` "
        + "(`clv_kit` int(6) NOT NULL,"
        + "`player` varchar(36) NOT NULL, "
        + "PRIMARY KEY (`clv_kit` , `player` ) ,"
        + "FOREIGN KEY (`clv_kit`) REFERENCES " + KITS_TABLE + "(`clv_kit`), "
        + "FOREIGN KEY (`player`) REFERENCES " + ACCOUNTS_TABLE + "(`uuid`) );";
  }

  @Override
  protected String getInsertKitQuery(Kit kit) {
    return "INSERT OR IGNORE INTO `" + KITS_TABLE + "`(`name`)  VALUES "
        + "('" + kit.name().toUpperCase() + "');";
  }

  @Override
  protected String getCreateAccountQuery(Account account) {
    return "INSERT OR IGNORE INTO `" + ACCOUNTS_TABLE + "` (`uuid`, `username`, `kills`, "
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
}

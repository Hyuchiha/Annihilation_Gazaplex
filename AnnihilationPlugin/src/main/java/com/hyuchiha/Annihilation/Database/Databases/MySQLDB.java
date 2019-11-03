package com.hyuchiha.Annihilation.Database.Databases;

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


  private ConfigurationSection getConfigSection() {
    return this.plugin.getConfig().getConfigurationSection("Database");
  }
}

package com.hyuchiha.Annihilation.Serializers;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PlayerSerializer {
  private static final String pluginPath = "plugins/Annihilation/";
  private static final String usersPath = "plugins/Annihilation/users/";

  public static void SerializePlayer(Player player) {
    String playerName = player.getName();
    ItemStack[] items = player.getInventory().getContents().clone();
    double health = player.getHealth();
    ItemStack[] armor = player.getInventory().getArmorContents().clone();
    float saturation = player.getSaturation();
    int level = player.getLevel();
    int gm = player.getGameMode().getValue();
    int food = player.getFoodLevel();
    float exhaustion = player.getExhaustion();
    float exp = player.getExp();

    String wName = player.getWorld().getName();
    GameTeam target = PlayerManager.getGamePlayer(player).getTeam();

    PlayerToConfig(playerName, items, armor, health, saturation, level, gm, food, exhaustion, exp, target, wName);
  }

  public static void RestorePlayer(Player p) {
    String playerName = p.getName();
    File f = new File(usersPath + playerName + ".yml");
    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(f);
    ConfigToPlayer(p, yamlConfiguration);
  }

  public static void removeItems(String playerName) {
    try {
      File file = new File(usersPath + playerName + ".yml");
      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
      yamlConfiguration.set("killed", true);
      yamlConfiguration.save(file);
      Output.log(playerName + " removing player items");
    } catch (IOException ex) {
      Output.logError(ex.getLocalizedMessage());
    }
  }

  public static void delete(String playerName) {
    File file = new File(usersPath + playerName + ".yml");
    file.delete();
  }

  public static void save(String playerName) {
    File file = new File(usersPath + playerName + ".yml");
    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    try {
      yamlConfiguration.save(file);
    } catch (IOException ex) {
      Output.logError(ex.getLocalizedMessage());
    }
  }

  public static Collection<ItemStack> dropItem(String playerName) {
    ItemStack[] a = ConfigToItemStack(getConfig(playerName), "Armor");
    ItemStack[] i = ConfigToItemStack(getConfig(playerName), "Inventaire");
    Collection<ItemStack> items = new ArrayList<ItemStack>();
    items.addAll(Arrays.asList(a));
    items.addAll(Arrays.asList(i));

    return items;
  }

  public static boolean isKilled(String name) {
    return getConfig(name).getBoolean("killed");
  }

  public static boolean playerPlayed(Player p) {
    String playerName = p.getName();
    try {
      File playerDataFile = new File(usersPath + playerName + ".yml");
      if (playerDataFile.exists()) {
        return true;
      }
    } catch (NullPointerException nullPointerException) {

    }

    return false;
  }

  public static void restartDataOfPlayers() {
    try {
      File file = new File(usersPath);

      if (file.isDirectory()) {
        for (File f: file.listFiles()) {
          f.delete();
        }
      }

      file.delete();
    } catch (Exception e) {
      Output.logError(e.getLocalizedMessage());
    }
  }

  private static void PlayerToConfig(String playerName, ItemStack[] items, ItemStack[] armor, double health, float saturation, int level, int gm, int food, float exhaust, float exp, GameTeam team, String wName) {
    try {
      File file = new File(usersPath + playerName + ".yml");
      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
      yamlConfiguration.set("Name", playerName);
      yamlConfiguration.set("Health", health);
      yamlConfiguration.set("Food", food);
      yamlConfiguration.set("Saturation", saturation);
      yamlConfiguration.set("Exhaustion", exhaust);
      yamlConfiguration.set("XP-Level", level);
      yamlConfiguration.set("Exp", exp);
      yamlConfiguration.set("GameMode", gm);
      String teamName = team.name();
      yamlConfiguration.set("Team", teamName);
      ItemStackToConfig(yamlConfiguration, "Armor", armor);
      ItemStackToConfig(yamlConfiguration, "Inventory", items);
      yamlConfiguration.set("killed", false);
      yamlConfiguration.set("world", wName);
      yamlConfiguration.save(file);
    } catch (IOException ex) {
      Output.logError(ex.getLocalizedMessage());
    }
  }

  public static void ConfigToPlayer(Player p, FileConfiguration config) {
    try {
      if (!config.contains("Name") || !config.getString("Name").equals(p.getName())) {
        return;
      }

      p.updateInventory();
      GamePlayer meta = PlayerManager.getGamePlayer(p);
      GameTeam team = GameTeam.getTeam(config.getString("Team"));
      if (team == GameTeam.NONE &&
          GameManager.getCurrentGame().getPhase() > Main.getInstance()
              .getConfig("config.yml").getInt("lastJoinPhase")) {

        p.kickPlayer(Translator.getPrefix() + ChatColor.RED + "Your team is invalid.");
        return;
      }
      meta.setTeam(team);
      meta.setAlive(true);

      p.setHealth((int) config.getDouble("Health"));
      p.setFoodLevel(config.getInt("Food"));
      p.setSaturation((float) config.getDouble("Saturation"));
      p.setExhaustion((float) config.getDouble("Exhaustion"));
      p.setLevel(config.getInt("XP-Level"));
      p.setExp((float) config.getDouble("Exp"));
      p.setGameMode(GameMode.getByValue(config.getInt("GameMode")));
      p.getInventory().clear();
      p.getInventory().setArmorContents(ConfigToItemStack(config, "Armor"));
      p.getInventory().setContents(ConfigToItemStack(config, "Inventory"));
      p.updateInventory();
    } catch (IllegalArgumentException illegalArgumentException) {
      Output.logError(illegalArgumentException.getLocalizedMessage());
    }
  }

  private static ItemStack[] ConfigToItemStack(FileConfiguration config, String path) {
    int nb = config.getInt(path + ".Item-Nb");
    ItemStack[] item = new ItemStack[nb];
    for (int i = 0; i < nb; i++) {
      item[i] = config.getItemStack(path + ".Item" + i);
    }
    return item;
  }

  private static FileConfiguration ItemStackToConfig(FileConfiguration config, String path, ItemStack[] items) {
    if (config == null) {
      return null;
    }
    config.set(path + ".Item-Nb", items.length);
    int i = 0;
    for (ItemStack it : items) {
      config.set(path + ".Item" + i, it);

      i++;
    }
    return config;
  }

  private static FileConfiguration getConfig(String playerName) {
    File file = new File(usersPath + playerName + ".yml");
    return YamlConfiguration.loadConfiguration(file);
  }
}

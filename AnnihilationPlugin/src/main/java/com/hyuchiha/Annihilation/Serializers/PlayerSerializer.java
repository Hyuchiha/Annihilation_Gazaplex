package com.hyuchiha.Annihilation.Serializers;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Output.Output;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
    String wName = Bukkit.getPlayer(playerName).getWorld().getName();
    GameTeam target = PlayerManager.getGamePlayer(player).getTeam();

    PlayerToConfig(playerName, items, armor, health, saturation, level, gm, food, exhaustion, exp, target, wName);
  }

  private static void PlayerToConfig(String playerName, ItemStack[] items, ItemStack[] armor, double health, float saturation, int level, int gm, int food, float exhaut, float exp, GameTeam team, String wName) {
    try {
      File file = new File(usersPath + playerName + ".yml");
      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
      yamlConfiguration.set("Name", playerName);
      yamlConfiguration.set("Health", health);
      yamlConfiguration.set("Food", food);
      yamlConfiguration.set("Saturation", saturation);
      yamlConfiguration.set("Exhaustion", exhaut);
      yamlConfiguration.set("XP-Level", level);
      yamlConfiguration.set("Exp", exp);
      yamlConfiguration.set("GameMode", gm);
      String teamName = team.name();
      yamlConfiguration.set("Team", teamName);
      ItemStackToConfig(yamlConfiguration, "Armor", armor);
      ItemStackToConfig(yamlConfiguration, "Inventaire", items);
      yamlConfiguration.set("killed", false);
      yamlConfiguration.set("world", wName);
      yamlConfiguration.save(file);
    } catch (IOException ex) {
      Output.logError(ex.getLocalizedMessage());
    }
  }

  public static void restartDataOfPlayers() {
    try {
      File f = new File(usersPath);
      FileUtils.cleanDirectory(f);
    } catch (IOException e) {
      Output.logError(e.getLocalizedMessage());
    }
  }

  public static void RestorePlayer(Player p) {
    String playerName = p.getName();
    File f = new File(usersPath + playerName + ".yml");
    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(f);
    ConfigToPlayer(p, yamlConfiguration);
  }


  public static FileConfiguration getConfig(String playerName) {
    File file = new File(usersPath + playerName + ".yml");
    return YamlConfiguration.loadConfiguration(file);
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
      p.getInventory().setContents(ConfigToItemStack(config, "Inventaire"));
      p.updateInventory();
    } catch (IllegalArgumentException illegalArgumentException) {
      Output.logError(illegalArgumentException.getLocalizedMessage());
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

  public static ItemStack[] ConfigToItemStack(FileConfiguration config, String path) {
    int nb = config.getInt(path + ".Item-Nb");
    ItemStack[] item = new ItemStack[nb];
    for (int i = 0; i < nb; i++) {
      item[i] = config.getItemStack(path + ".Item" + i);
    }
    return item;
  }


  public static ItemStack[] BossLoot() {
    File file = new File(pluginPath + "config.yml");
    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    int nb = yamlConfiguration.getInt("Boss-loot.Item-Nb");
    ItemStack[] item = new ItemStack[nb];
    for (int i = 0; i < nb; i++) {
      item[i] = yamlConfiguration.getItemStack("Boss-loot.Item" + i);
    }
    return item;
  }

  public static FileConfiguration ItemStackToConfig(FileConfiguration config, String path, ItemStack[] items) {
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

  public static String InventoryToString(Inventory invInventory) {
    String serialization = invInventory.getSize() + ";";
    for (int i = 0; i < invInventory.getSize(); i++) {
      ItemStack is = invInventory.getItem(i);
      if (is != null) {
        String serializedItemStack = "";

        String isType = String.valueOf(is.getType().getId());
        serializedItemStack = serializedItemStack + "t@" + isType;
        if (is.getDurability() != 0) {
          String isDurability = String.valueOf(is.getDurability());
          serializedItemStack = serializedItemStack + ":d@" + isDurability;
        }
        if (is.getAmount() != 1) {
          String isAmount = String.valueOf(is.getAmount());
          serializedItemStack = serializedItemStack + ":a@" + isAmount;
        }
        Map isEnch = is.getEnchantments();

        if (isEnch.size() > 0) {
          for (Iterator it = isEnch.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry ench = (Map.Entry) it.next();
            serializedItemStack = serializedItemStack + ":e@" + ((Enchantment) ench.getKey()).getId() + "@" + ench.getValue();
          }
        }
        serialization = serialization + i + "#" + serializedItemStack + ";";
      }
    }
    return serialization;
  }

  public static Inventory StringToInventory(String invString) {
    String[] serializedBlocks = invString.split(";");
    String invInfo = serializedBlocks[0];
    Inventory deserializeInventory = Bukkit.getServer().createInventory(null, Integer.parseInt(invInfo));
    for (int i = 1; i < serializedBlocks.length; i++) {
      String[] serializedBlock = serializedBlocks[i].split("#");
      int stackPosition = Integer.parseInt(serializedBlock[0]);
      if (stackPosition < deserializeInventory.getSize()) {
        ItemStack is = null;
        boolean createdItemStack = false;

        String[] serializedItemStack = serializedBlock[1].split(":");
        for (String itemInfo : serializedItemStack) {
          String[] itemAttribute = itemInfo.split("@");
          if (itemAttribute[0].equals("t")) {
            is = new ItemStack(Material.getMaterial(itemAttribute[1]));
            createdItemStack = true;
          } else if ((itemAttribute[0].equals("d")) && createdItemStack) {
            is.setDurability(Short.parseShort(itemAttribute[1]));
          } else if ((itemAttribute[0].equals("a")) && createdItemStack) {
            is.setAmount(Integer.parseInt(itemAttribute[1]));
          } else if ((itemAttribute[0].equals("e")) && createdItemStack) {
            is.addEnchantment(Enchantment.getById(Integer.parseInt(itemAttribute[1])), Integer.parseInt(itemAttribute[2]));
          }
        }
        deserializeInventory.setItem(stackPosition, is);
      }
    }
    return deserializeInventory;
  }


  public static boolean isKilled(String name) {
    return getConfig(name).getBoolean("killed");
  }


  public static void clearUserData() {
    File invFile = new File(usersPath);
    if (invFile.isDirectory()) {
      for (File f : Objects.requireNonNull(invFile.listFiles())) {
        f.delete();
      }
    }
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
}

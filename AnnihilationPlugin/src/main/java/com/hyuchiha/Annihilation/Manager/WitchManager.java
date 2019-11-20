package com.hyuchiha.Annihilation.Manager;

import com.hyuchiha.Annihilation.Game.GameWitch;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Witch;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class WitchManager {
  public static HashMap<String, GameWitch> witches = new HashMap<>();
  private static List<BukkitTask> tasks = new ArrayList<>();

  public static void loadWitchConfig(ConfigurationSection config, World originalWorld) {
    Output.log("Loading witches configuration");

    for (String witch : config.getKeys(false)) {
      String name = config.getString(witch + ".name");
      Location spawnPoint = LocationUtils.parseLocation(originalWorld, config.getString(witch + ".spawn"));
      int hearts = config.getInt(witch + ".hearts") * 2;

      GameWitch gWitch = new GameWitch(witch, name, spawnPoint, hearts);
      witches.put(witch, gWitch);
    }

    Output.log("Witches loaded");
  }

  public static void spawnWitches() {
    for (GameWitch witch: witches.values()) {
      spawnWitch(witch);
    }
  }

  public static void spawnWitch(GameWitch gWitch) {
    Location spawn = gWitch.getSpawn();

    if (spawn != null && spawn.getWorld() != null) {
      Bukkit.getWorld(spawn.getWorld().getName()).loadChunk(spawn.getChunk());

      Witch witch = (Witch) spawn.getWorld().spawnEntity(spawn, EntityType.WITCH);

      witch.setMaxHealth(gWitch.getHealth());
      witch.setHealth(gWitch.getHealth());
      witch.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE,5));
      witch.setCanPickupItems(false);
      witch.setRemoveWhenFarAway(false);
      witch.setCustomNameVisible(true);

      String name = ChatColor.translateAlternateColorCodes('&',
          gWitch.getName() + " &8» &a" + gWitch.getHealth() + " HP"
      );
      witch.setCustomName(name);
    }
  }

  public static void update(Witch witch) {
    Output.log("Updating witch name");

    for (GameWitch gWitch: witches.values()) {
      if (witch.getCustomName().contains(gWitch.getName())) {
        witch.setCustomName(ChatColor.translateAlternateColorCodes('&', gWitch.getName() + " &8» &a" + witch.getHealth() + " HP"));

        Output.log("Witch name updated");
        break;
      }
    }
  }

  public static GameWitch findGameWitch(String name) {
    for (GameWitch witch: witches.values()) {
      if (name.contains(witch.getName())) {
        return witch;
      }
    }

    return null;
  }

  public static void beginWitchRespawn(GameWitch witch, int respawnTime) {
    BukkitTask task = Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), new Runnable() {
      @Override
      public void run() {
        spawnWitch(witch);
      }
    }, 20 * respawnTime * 60);
  }

  public static void cancelRespawnTask() {
    for (BukkitTask task: tasks) {
      task.cancel();
    }
  }

  public static void clearWitchData() {
    World world = MapManager.getCurrentMap().getWorld();

    for (Entity entity: world.getEntities()) {
      if(entity.getType() == EntityType.WITCH){
        Output.log("Removing witch");
        entity.remove();
      }
    }

    cancelRespawnTask();

    witches.clear();
    tasks.clear();
  }

}

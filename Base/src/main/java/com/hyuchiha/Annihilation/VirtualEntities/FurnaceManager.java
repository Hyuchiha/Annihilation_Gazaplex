package com.hyuchiha.Annihilation.VirtualEntities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public abstract class FurnaceManager {

  private Plugin plugin;
  private HashMap<UUID, VirtualFurnace> furnaces = new HashMap<>();
  private boolean isRunning = false;
  private BukkitTask task;

  public FurnaceManager(Plugin plugin) {
    this.plugin = plugin;

    init();
  }

  public void init() {
    task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
      for (VirtualFurnace furnace : furnaces.values()) {
        if (furnace.canCook()) {
          furnace.cook();
        }
      }

    }, 0, 5L);

    isRunning = true;
  }

  public boolean isRunning() {
    return isRunning;
  }

  public void clearFurnaces() {
    furnaces.clear();
  }

  public boolean hasFurnaceRegistered(UUID playerUUID) {
    return furnaces.containsKey(playerUUID);
  }

  public VirtualFurnace getFurnace(Player player) {
    UUID playerUUID = player.getUniqueId();

    if (!furnaces.containsKey(playerUUID)) {
      furnaces.put(playerUUID, createFurnace(player));
    }
    return furnaces.get(playerUUID);
  }

  public void disableFurnaces() {
    task.cancel();

    isRunning = false;
  }

  abstract VirtualFurnace createFurnace(Player player);

}

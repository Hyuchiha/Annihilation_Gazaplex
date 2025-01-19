package com.hyuchiha.Annihilation.VirtualEntities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public abstract class BrewingManager {

  private final Plugin plugin;
  private final HashMap<UUID, VirtualBrewingStand> brewingStands = new HashMap<>();
  private boolean isRunning = false;
  private BukkitTask task;

  public BrewingManager(Plugin plugin) {
    this.plugin = plugin;

    init();
  }

  public void init() {
    task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
      for (VirtualBrewingStand brewingStand : brewingStands.values()) {
        if (brewingStand.canMakePotions()) {
          brewingStand.makePotions();
        }
      }

    }, 0, 5L);

    isRunning = true;
  }

  public boolean isRunning() {
    return isRunning;
  }

  public void clearBrewingStands() {
    brewingStands.clear();
  }

  public boolean hasBrewingRegistered(UUID playerUUID) {
    return brewingStands.containsKey(playerUUID);
  }

  public VirtualBrewingStand getBrewingStand(Player player) {
    UUID playerUUID = player.getUniqueId();

    if (!brewingStands.containsKey(playerUUID)) {
      brewingStands.put(playerUUID, createBrewingStand(player));
    }
    return brewingStands.get(playerUUID);
  }

  public void disableBrewingStands() {
    if (task != null) {
      task.cancel();
    }

    isRunning = false;
  }

  protected abstract VirtualBrewingStand createBrewingStand(Player player);
}

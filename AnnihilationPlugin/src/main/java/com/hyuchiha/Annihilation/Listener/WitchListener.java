package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Game.GameWitch;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.WitchManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class WitchListener implements Listener {
  private final Main plugin;
  private Configuration config;

  private final int respawnTime;

  public WitchListener(Main instance) {
    this.plugin = instance;
    this.config = plugin.getConfig("config.yml");

    respawnTime = plugin.getConfig().getInt("witchRespawnDelay", 10);
  }

  @EventHandler
  public void onHit(EntityDamageEvent event) {
    if (event.getEntity() instanceof Witch) {
      final Witch g = (Witch) event.getEntity();
      if (g.getCustomName() == null) {
        return;
      }

      final GameWitch gWitch = WitchManager.findGameWitch(g.getName());
      if (gWitch == null) {
        return;
      }

      if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
        event.getEntity().remove();

        Bukkit.getScheduler().runTask(plugin, new Runnable() {
          @Override
          public void run() {
            WitchManager.spawnWitch(gWitch);
          }
        });
      }
    }
  }

  @EventHandler
  public void onHit(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof Witch) {
      if (!(event.getDamager() instanceof Player) && !(event.getDamager() instanceof Arrow)) {
        event.setCancelled(true);
      }

      final Witch g = (Witch) event.getEntity();
      if (g.getCustomName() == null) {
        return;
      }

      final GameWitch gWitch = WitchManager.findGameWitch(g.getCustomName());
      if (gWitch == null) {
        return;
      }

      Bukkit.getScheduler().runTask(plugin, new Runnable() {
        @Override
        public void run() {
          WitchManager.update(g);
        }
      });
    }
  }

  @EventHandler
  public void onDeath(EntityDeathEvent event) {
    if (event.getEntity() instanceof Witch) {
      Witch witch = (Witch) event.getEntity();
      if (witch.getCustomName() == null) {
        return;
      }

      GameWitch gWitch = WitchManager.findGameWitch(witch.getCustomName());
      if (gWitch == null) {
        return;
      }

      event.getDrops().clear();

      witch.getWorld().dropItemNaturally(witch.getLocation(), getLoot());

      if (witch.getKiller() != null) {
        WitchManager.beginWitchRespawn(gWitch, this.respawnTime);
      } else {
        witch.teleport(gWitch.getSpawn());
      }
    }
  }

  private ItemStack getLoot() {
    switch (new Random().nextInt(10)) {
      case 1:
        return new ItemStack(Material.GLOWSTONE_DUST, 2);
      case 2:
      case 9:
      case 10:
        return new ItemStack(Material.REDSTONE, 5);
      case 3:
        return new ItemStack(Material.SPIDER_EYE, 10);
      case 4:
      case 5:
      case 6:
      case 7:
        return new ItemStack(Material.BLAZE_POWDER, 3);
      case 8:
        return new ItemStack(Material.DIAMOND, 5);
    }

    return new ItemStack(Material.COOKIE, 64);
  }
}

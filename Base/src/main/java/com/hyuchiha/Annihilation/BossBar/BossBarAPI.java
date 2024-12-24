package com.hyuchiha.Annihilation.BossBar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BossBarAPI {
  private static final Map<UUID, BossBar> barMap = new ConcurrentHashMap<>();
  private static Plugin pluginInstance;

  public static void init(Plugin plugin) {

    pluginInstance = plugin;

    for (Player player : Bukkit.getOnlinePlayers()) {
      removeBar(player);
    }

    PluginManager pm = plugin.getServer().getPluginManager();
    pm.registerEvents(new BossBarListener(), plugin);

  }

  public static void setMessage(Player player, String message, float percentage) {

    if (!barMap.containsKey(player.getUniqueId())) {
      BossBar bossBar = Bukkit.createBossBar(message, BarColor.PURPLE, BarStyle.SOLID, BarFlag.DARKEN_SKY);
      bossBar.addPlayer(player);
      bossBar.setProgress(percentage);

      barMap.put(player.getUniqueId(), bossBar);
    }

    BossBar bar = barMap.get(player.getUniqueId());
    if (!bar.getTitle().equals(message)) {
      bar.setTitle(message);
    }

    if (bar.getProgress() != percentage) {
      bar.setProgress(percentage);
    }

    if (!bar.isVisible()) {
      bar.setVisible(true);
    }

  }

  protected static void removeBarForPlayer(Player player, BossBar bossBar) {
    bossBar.removePlayer(player);
    BossBar bar = barMap.get(player.getUniqueId());
    if (bar != null) {
      barMap.remove(player.getUniqueId());
    }
  }

  public static void removeBar(@Nonnull Player player) {
    BossBar bar = getBossBar(player);
    if (bar != null) {
      bar.setVisible(false);

      removeBarForPlayer(player, bar);
    }
  }

  public static boolean hasBar(@Nonnull Player player) {
    return barMap.containsKey(player.getUniqueId());
  }

  public static BossBar getBossBar(@Nonnull Player player) {
    return barMap.get(player.getUniqueId());
  }

  public static void handlePlayerTeleport(Player player) {
    if (hasBar(player)) {
      final BossBar bar = getBossBar(player);

      if (bar != null) {
        bar.setVisible(false);
        (new BukkitRunnable() {
          public void run() {
            bar.setVisible(true);
          }
        }).runTaskLater(pluginInstance, 2L);
      }
    }
  }

}

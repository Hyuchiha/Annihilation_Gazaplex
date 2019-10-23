package com.hyuchiha.Annihilation.BossBar;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BossBarListener implements Listener {

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onQuit(PlayerQuitEvent e) {
        BossBarAPI.removeBar(e.getPlayer());
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onKick(PlayerKickEvent e) {
        BossBarAPI.removeBar(e.getPlayer());
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onTeleport(PlayerTeleportEvent e) {
        BossBarAPI.handlePlayerTeleport(e.getPlayer(), e.getFrom(), e.getTo());
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void onRespawn(PlayerRespawnEvent e) {
        BossBarAPI.handlePlayerTeleport(e.getPlayer(), e.getPlayer().getLocation(), e.getRespawnLocation());
    }
}

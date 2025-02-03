package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Manager.EnderFurnaceManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EnderFurnaceListener implements Listener {

  @EventHandler(ignoreCancelled = true)
  public void onFurnaceOpen(PlayerInteractEvent e) {
    if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    Block b = e.getClickedBlock();
    if (b.getType() != Material.FURNACE) {
      return;
    }

    Location loc = b.getLocation();
    Player player = e.getPlayer();
    GameTeam team = PlayerManager.getGamePlayer(player).getTeam();
    if (team == null || !EnderFurnaceManager.teamHasFurnaceRegistered(team)) {
      return;
    }

    if (EnderFurnaceManager.isTeamFurnace(team, loc)) {
      e.setCancelled(true);
      EnderFurnaceManager.openFurnaceForUser(player);
      player.sendMessage(Translator.getPrefix() + ChatColor.GRAY + Translator.getColoredString("INFO.FURNACE"));
    }
  }

  @EventHandler
  public void onFurnaceBreak(BlockBreakEvent e) {
    if (EnderFurnaceManager.isFurnaceLocation(e.getBlock().getLocation())) {
      e.setCancelled(true);
    }
  }
}

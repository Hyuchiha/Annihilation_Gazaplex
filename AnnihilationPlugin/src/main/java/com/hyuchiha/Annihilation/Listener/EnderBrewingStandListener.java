package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Manager.EnderBrewingManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class EnderBrewingStandListener implements Listener {

  @EventHandler(ignoreCancelled = true)
  public void onBrewingOpen(PlayerInteractEvent e) {
    if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    Block b = e.getClickedBlock();
    if (b.getType() != Material.BREWING_STAND) {
      return;
    }

    Location loc = b.getLocation();
    Player player = e.getPlayer();
    GameTeam team = PlayerManager.getGamePlayer(player).getTeam();
    if (team == null || !EnderBrewingManager.teamHasBrewingRegistered(team)) {
      return;
    }

    if (EnderBrewingManager.isTeamBrewing(team, loc)) {
      e.setCancelled(true);
      EnderBrewingManager.openBrewingForUser(player);
      player.sendMessage(Translator.getPrefix() + ChatColor.GRAY + Translator.getColoredString("INFO_BREWING"));
    }
  }

  @EventHandler
  public void onBrewingInventoryClick(InventoryClickEvent e) {
    Inventory inventory = e.getClickedInventory();

    if (inventory != null && inventory.getType() == InventoryType.BREWING && e.getSlot() == 4) {
      e.setCancelled(true);
    }
  }


  @EventHandler
  public void onBrewingBreak(BlockBreakEvent e) {
    if (EnderBrewingManager.isBrewingLocation(e.getBlock().getLocation()))
      e.setCancelled(true);
  }
}

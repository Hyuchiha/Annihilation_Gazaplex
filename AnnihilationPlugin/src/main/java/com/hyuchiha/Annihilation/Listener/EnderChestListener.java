package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Manager.EnderChestManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EnderChestListener implements Listener {

  @EventHandler(ignoreCancelled = true)
  public void onChestOpen(PlayerInteractEvent e) {
    if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (e.getClickedBlock().getType() != Material.ENDER_CHEST) {
      return;
    }

    Block clicked = e.getClickedBlock();
    Player player = e.getPlayer();
    GameTeam team = PlayerManager.getGamePlayer(player).getTeam();
    if (team == GameTeam.NONE || !EnderChestManager.teamHasChest(team)) {
      return;
    }

    e.setCancelled(true);

    if (EnderChestManager.isTeamChest(team, clicked.getLocation())) {
      EnderChestManager.openEnderChest(player);
    }
  }

  @EventHandler
  public void ItemMoveEvent(InventoryMoveItemEvent event) {
    Inventory inv = event.getInitiator();

    if (inv != null && inv.getTitle() != null && !inv.getTitle().contains("Endechest")) {
      return;
    }

    ItemStack material = event.getItem();
    if (material == null) {
      event.setCancelled(true);
      return;
    }

    if (event.getItem().getType() == Material.STAINED_GLASS_PANE) {
      event.setCancelled(true);

      return;
    }
    ItemMeta meta = material.getItemMeta();

    if (meta != null) {

      String displayName = meta.getDisplayName();


      if (displayName.equals(ChatColor.GOLD + "Slot disponible para el rango Gold") || displayName
                                                                                           .equals(ChatColor.GRAY + "Slot disponible para el rango Silver") || displayName
                                                                                                                                                                   .equals(ChatColor.RED + "Slot disponible para el rango Bronze")) {
        event.setCancelled(true);
      }
    }
  }


  @EventHandler
  public void onEnderChestBreak(BlockBreakEvent e) {
    if (EnderChestManager.isEnderChestBlock(e.getBlock().getLocation())) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void enderInteract(InventoryClickEvent e) {
    Inventory inv = e.getInventory();
    Player player = (Player) e.getWhoClicked();

    if (e.getCurrentItem().getType() == Material.STAINED_GLASS_PANE || e.getCurrentItem().getType() == null) {
      e.setCancelled(true);

      return;
    }
    if (inv == null || inv.getTitle() == null) {
      return;
    }

    if (inv.getTitle().contains("Enderchest")) {
      ItemMeta name = e.getCurrentItem().getItemMeta();

      if (name != null &&
              name.hasDisplayName()) {
        if (name.getDisplayName().equals(ChatColor.GOLD + "Slot disponible para el rango Gold") || name
                                                                                                       .getDisplayName().equals(ChatColor.GRAY + "Slot disponible para el rango Silver") || name
                                                                                                                                                                                                .getDisplayName().equals(ChatColor.RED + "Slot disponible para el rango Bronze"))
          e.setCancelled(true);
      }
    }
  }
}

package com.hyuchiha.Annihilation.Listener;

import com.cryptomorin.xseries.XMaterial;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Manager.EnderChestManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
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
    Output.log("Ender chest click");

    Block clicked = e.getClickedBlock();
    Player player = e.getPlayer();
    GameTeam team = PlayerManager.getGamePlayer(player).getTeam();
    if (team == GameTeam.NONE || !EnderChestManager.teamHasChest(team)) {
      Output.log("No ender chest for team");
      return;
    }

    e.setCancelled(true);

    if (EnderChestManager.isTeamChest(team, clicked.getLocation())) {
      EnderChestManager.openEnderChest(player);
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
    Player player = (Player) e.getWhoClicked();
    InventoryView view = e.getView();
    Inventory inv = e.getClickedInventory();

    if (view.getTitle().contains("Enderchest")) {
      ItemStack clickedItem = e.getCurrentItem();

      if (clickedItem == null || clickedItem.getType() == Material.AIR) {
        return;
      }

      if (clickedItem.getType() == XMaterial.BLACK_STAINED_GLASS_PANE.get()) {
        e.setCancelled(true);
        return;
      }

      ItemMeta name = clickedItem.getItemMeta();
      //TODO translate
      if (name != null && name.hasDisplayName()) {
        if (name.getDisplayName().equals(ChatColor.GOLD + "Slot disponible para el rango Gold")
            || name.getDisplayName().equals(ChatColor.GRAY + "Slot disponible para el rango Silver")
            || name.getDisplayName().equals(ChatColor.RED + "Slot disponible para el rango Bronze"))
          e.setCancelled(true);
      }
    }

  }
}

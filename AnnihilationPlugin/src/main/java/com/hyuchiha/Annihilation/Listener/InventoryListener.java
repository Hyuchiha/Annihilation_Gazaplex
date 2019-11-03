package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameState;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Manager.VotingManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

  private Main plugin;

  public InventoryListener(Main plugin) {
    plugin = plugin;
  }


  @EventHandler
  public void onInvClose(InventoryCloseEvent event) {
    HumanEntity player = event.getPlayer();
    Player p = (Player) player;
    GameManager.getCurrentGame().getCrafting().remove(p.getName());
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    Inventory inv = e.getInventory();
    Player player = (Player) e.getWhoClicked();

    ItemStack clickedItem = e.getCurrentItem();

    if (inv.getTitle().startsWith(Translator.getColoredString("CLICK_TO_VOTE_MAP"))) {
      if (e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == null) {
        return;
      }

      player.closeInventory();
      e.setCancelled(true);

      if (GameManager.getCurrentGame().getTimer().getGameState() != GameState.STARTING) {
        player.sendMessage(Translator.getPrefix() + ChatColor.RED + Translator.getColoredString("NOT_VOTE_PHASE"));

        return;
      }
      if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
        String name = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

        VotingManager.vote(player, name);
      }

      return;
    }

    if (inv.getTitle().startsWith(Translator.getColoredString("CLICK_TO_CHOOSE_TEAM"))) {
      if (e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == null) {
        return;
      }

      player.closeInventory();
      e.setCancelled(true);

      if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
        String name = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

        if (GameManager.getCurrentGame() != null && GameManager.getCurrentGame().isInGame()) {
          player.sendMessage(Translator.getPrefix() + ChatColor.RED + Translator.getString("CANNOT_JOIN_TEAM"));

          return;
        }
        GamePlayer gamePlayer = PlayerManager.getGamePlayer(player);
        GameTeam team = GameTeam.getTeamByTranslatedName(name);

        if (team != null) {
          if (gamePlayer.getTeam() == GameTeam.NONE) {
            GameManager.getCurrentGame().joinTeam(player, team.name());
          }
        } else {
          player.sendMessage(Translator.getPrefix() + ChatColor.RED + Translator.getString("CANNOT_JOIN_TEAM"));
        }
      }
    }
  }
}

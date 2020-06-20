package com.hyuchiha.Annihilation.Listener;

import com.google.common.base.Enums;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameState;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Manager.VotingManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Utils.KitUtils;
import com.hyuchiha.Annihilation.Utils.MenuUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

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

    if (inv.getTitle().startsWith(Translator.getColoredString("GAME.CLICK_TO_VOTE_MAP"))) {
      if (e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == null) {
        return;
      }

      player.closeInventory();
      e.setCancelled(true);

      if (GameManager.getCurrentGame().getTimer().getGameState() != GameState.STARTING) {
        player.sendMessage(Translator.getPrefix() + ChatColor.RED + Translator.getColoredString("ERRORS.NOT_VOTE_PHASE"));

        return;
      }
      if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
        String name = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

        VotingManager.vote(player, name);
      }

      return;
    }

    if (inv.getTitle().startsWith(Translator.getColoredString("GAME.CLICK_TO_CHOOSE_TEAM"))) {
      if (e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == null) {
        return;
      }

      player.closeInventory();
      e.setCancelled(true);

      if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
        String name = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

        if (GameManager.getCurrentGame() != null && GameManager.getCurrentGame().isInGame()) {
          player.sendMessage(Translator.getPrefix() + ChatColor.RED + Translator.getString("ERRORS.CANNOT_JOIN_TEAM"));

          return;
        }
        GamePlayer gamePlayer = PlayerManager.getGamePlayer(player);
        GameTeam team = GameTeam.getTeamByTranslatedName(name);

        if (team != null) {
          if (gamePlayer.getTeam() == GameTeam.NONE) {
            GameManager.getCurrentGame().joinTeam(player, team.name());
          }
        } else {
          player.sendMessage(Translator.getPrefix() + ChatColor.RED + Translator.getString("ERRORS.CANNOT_JOIN_TEAM"));
        }
      }
    }

    if (inv.getTitle().startsWith(Translator.getColoredString("BOSS_SHOP"))) {
      if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == null) {
        return;
      }

      e.setCancelled(true);

      if (e.getClickedInventory().getTitle().contains(Translator.getColoredString("GAME.BOSS_SHOP"))) {
        player.getInventory().addItem(clickedItem);
        player.closeInventory();
      }

      return;
    }

    if (inv.getTitle().startsWith(Translator.getColoredString("GAME.CLASS_SELECT_INV_TITLE"))) {
      if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == null) {
        return;
      }

      player.closeInventory();
      e.setCancelled(true);

      if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
        String name = clickedItem.getItemMeta().getDisplayName();

        KitUtils.selectKit(name, player);
      }

      return;
    }

    if (inv.getTitle().startsWith(Translator.getColoredString("GAME.CLASS_UNLOCK_INV_TITLE"))) {
      if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == null) {
        return;
      }

      String name = e.getCurrentItem().getItemMeta().getDisplayName();

      Kit toChoose = Enums.getIfPresent(Kit.class, ChatColor.stripColor(name).toUpperCase()).orNull();

      player.closeInventory();

      e.setCancelled(true);

      if (toChoose != null && !toChoose.isOwnedBy(player)) {
        MenuUtils.showConfirmUnlockClass(player, toChoose);

      } else {
        player.sendMessage(Translator.getPrefix() + " " + Translator.getColoredString("GAME.PLAYER_ALREADY_HAVE_CLASS"));
      }

      return;
    }

    if (inv.getTitle().startsWith(Translator.getColoredString("GAME.CONFIRM_UNLOCK"))) {
      if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == null) {
        return;
      }
      e.setCancelled(true);

      if (e.getCurrentItem().getType() == Material.EMERALD_BLOCK || e.getCurrentItem().getType() == Material.REDSTONE_BLOCK) {

        player.closeInventory();

        String name = e.getClickedInventory().getItem(4).getItemMeta().getDisplayName();

        if (e.getCurrentItem().getType() == Material.REDSTONE_BLOCK) {
          return;
        }

        double money = Main.getInstance().getConfig("kits.yml").getInt("Kits." + name.toUpperCase() + ".price");
        double userMoney = PlayerManager.getMoney(player);

        if (userMoney >= money) {
          Main.getInstance().getMainDatabase().addUnlockedKit(player.getUniqueId().toString(), ChatColor.stripColor(name).toUpperCase());

          PlayerManager.withdrawMoney(player, money);

          String classUnlocked = Translator.getColoredString("GAME.PLAYER_UNLOCK_CLASS");
          player.sendMessage(Translator.getPrefix() + " " + classUnlocked.replace("%CLASS%", name));
        } else {
          player.sendMessage(Translator.getPrefix() + " " + Translator.getColoredString("GAME.PLAYER_DONT_HAVE_REQUIRED_MONEY"));
        }

      }
    }

  }

  @EventHandler
  public void ItemMoveEvent(InventoryMoveItemEvent event) {
    if (event.getInitiator().getName().startsWith(Translator.getColoredString("GAME.CONFIRM_UNLOCK"))) {
      event.setCancelled(true);
    }
  }
}

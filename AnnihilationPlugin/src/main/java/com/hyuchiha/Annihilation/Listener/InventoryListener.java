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
import com.hyuchiha.Annihilation.Utils.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryListener implements Listener {

  @EventHandler
  public void onInvClose(InventoryCloseEvent event) {
    HumanEntity player = event.getPlayer();
    Player p = (Player) player;
    GameManager.getCurrentGame().getCrafting().remove(p.getName());
  }

  @EventHandler()
  public void onVoteMap(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    InventoryView view = e.getView();

    if (view.getTitle().equals(Translator.getColoredString("GAME.CLICK_TO_VOTE_MAP"))) {
      ItemStack clickedItem = e.getCurrentItem();

      if (clickedItem == null || clickedItem.getType() == Material.AIR) {
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
    }
  }

  @EventHandler()
  public void onChooseTeam(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    InventoryView view = e.getView();

    if (view.getTitle().equals(Translator.getColoredString("GAME.CLICK_TO_CHOOSE_TEAM"))) {
      ItemStack clickedItem = e.getCurrentItem();

      if (clickedItem == null || clickedItem.getType() == Material.AIR) {
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
  }

  @EventHandler()
  public void onBossShop(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    InventoryView view = e.getView();

    if (view.getTitle().equals(Translator.getColoredString("GAME.BOSS_SHOP"))) {
      ItemStack clickedItem = e.getCurrentItem();

      if (clickedItem == null || clickedItem.getType() == Material.AIR) {
        return;
      }

      e.setCancelled(true);
      player.closeInventory();

      player.getInventory().addItem(clickedItem);
    }
  }

  @EventHandler()
  public void onSelectClass(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    InventoryView view = e.getView();

    if (view.getTitle().equals(Translator.getColoredString("GAME.CLASS_SELECT_INV_TITLE"))) {
      ItemStack clickedItem = e.getCurrentItem();

      if (clickedItem == null || clickedItem.getType() == Material.AIR) {
        return;
      }

      e.setCancelled(true);

      if (clickedItem.getType() == XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()) {
        return;
      }

      player.closeInventory();

      if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
        String name = clickedItem.getItemMeta().getDisplayName();

        KitUtils.selectKit(name, player);
      }
    }
  }

  @EventHandler()
  public void onUnlockClass(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    InventoryView view = e.getView();

    if (view.getTitle().equals(Translator.getColoredString("GAME.CLASS_UNLOCK_INV_TITLE"))) {
      ItemStack clickedItem = e.getCurrentItem();

      if (clickedItem == null || clickedItem.getType() == Material.AIR) {
        return;
      }

      e.setCancelled(true);

      if (clickedItem.getType() == XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()) {
        return;
      }

      player.closeInventory();

      ItemMeta itemMeta = clickedItem.getItemMeta();
      String itemName = itemMeta.getDisplayName();

      Kit kitSelected = Enums.getIfPresent(Kit.class, ChatColor.stripColor(itemName).toUpperCase()).orNull();

      if (kitSelected != null && !kitSelected.isOwnedBy(player)) {
        MenuUtils.showConfirmUnlockClass(player, kitSelected);
      } else {
        player.sendMessage(Translator.getPrefix() + Translator.getColoredString("GAME.PLAYER_ALREADY_HAVE_CLASS"));
      }

    }
  }

  @EventHandler
  public void onConfirmUnlock(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    InventoryView view = e.getView();
    Inventory inventory = e.getClickedInventory();

    if (view.getTitle().equals(Translator.getColoredString("GAME.CONFIRM_UNLOCK"))) {
      ItemStack clickedItem = e.getCurrentItem();

      if (clickedItem == null || clickedItem.getType() == Material.AIR) {
        return;
      }

      e.setCancelled(true);

      if (e.getCurrentItem().getType() == Material.EMERALD_BLOCK || e.getCurrentItem().getType() == Material.REDSTONE_BLOCK) {

        player.closeInventory();

        String name = inventory.getItem(4).getItemMeta().getDisplayName();

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
          player.sendMessage(Translator.getPrefix() + " " + Translator.getColoredString("INFO.PLAYER_DONT_HAVE_REQUIRED_MONEY"));
        }

      }
    }
  }
}

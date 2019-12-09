package com.hyuchiha.Annihilation.Utils;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class ItemSelectorUtils {
  public static void giveMapSelector(Player player) {
    ItemStack mapSelector = new ItemStack(Material.PAPER);
    ItemMeta itemMeta = mapSelector.getItemMeta();
    itemMeta.setDisplayName(Translator.getColoredString("CLICK_TO_VOTE_MAP"));
    mapSelector.setItemMeta(itemMeta);
    player.getInventory().setItem(1, mapSelector);
  }

  public static void giveTeamSelector(Player player) {
    ItemStack teamSelector = new ItemStack(Material.WOOL, 1, (short) 0, DyeColor.WHITE.getWoolData());
    ItemMeta itemMeta = teamSelector.getItemMeta();
    itemMeta.setDisplayName(Translator.getColoredString("CLICK_TO_CHOOSE_TEAM"));
    teamSelector.setItemMeta(itemMeta);
    player.getInventory().setItem(0, teamSelector);
  }

  public static void giveLobbyReturnItem(Player player) {
    ItemStack lobbySelector = new ItemStack(Material.BED);
    ItemMeta itemMeta = lobbySelector.getItemMeta();
    itemMeta.setDisplayName(Translator.getColoredString("CLICK_TO_RETURN_LOBBY"));
    lobbySelector.setItemMeta(itemMeta);
    player.getInventory().setItem(8, lobbySelector);
  }

  public static void giveKitSelectorItem(Player player) {
    ItemStack lobbySelector = new ItemStack(Material.ENCHANTED_BOOK);
    ItemMeta itemMeta = lobbySelector.getItemMeta();
    itemMeta.setDisplayName(Translator.getColoredString("CLICK_TO_CHOOSE_KIT"));
    lobbySelector.setItemMeta(itemMeta);
    player.getInventory().setItem(2, lobbySelector);
  }

  public static void getBossStarSelector(String player) {
    Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
      GamePlayer gPlayer = PlayerManager.getGamePlayer(Bukkit.getPlayer(player));
      for (Player p : Bukkit.getOnlinePlayers()) {
        GamePlayer EquipPlayer = PlayerManager.getGamePlayer(p);

        if (gPlayer.getTeam() == EquipPlayer.getTeam()) {
          ItemStack star = new ItemStack(Material.NETHER_STAR);
          ItemMeta meta = star.getItemMeta();
          meta.setDisplayName(Translator.getColoredString("BOSS_STAR"));
          star.setItemMeta(meta);
          p.getInventory().addItem(star);
          p.updateInventory();
        }
      }
    });
  }
}

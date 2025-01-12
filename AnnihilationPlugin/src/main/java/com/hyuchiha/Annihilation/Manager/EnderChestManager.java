package com.hyuchiha.Annihilation.Manager;

import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import com.hyuchiha.Annihilation.Utils.PermissionUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class EnderChestManager {
  private static final HashMap<GameTeam, Location> chests = new HashMap<>();
  private static final HashMap<UUID, Inventory> inventories = new HashMap<>();

  public static void loadTeamEnderChest(GameTeam team, Location location) {
    chests.put(team, location);

    if (location.getBlock() != null) {
      location.getBlock().setType(Material.ENDER_CHEST);
    }
  }

  public static void clearTeamsEnderChests() {
    chests.clear();
  }


  public static boolean teamHasChest(GameTeam team) {
    return chests.containsKey(team);
  }


  public static boolean isTeamChest(GameTeam team, Location location) {
    return chests.get(team).equals(location);
  }


  public static boolean isEnderChestBlock(Location location) {
    return chests.containsValue(location);
  }


  public static void openEnderChest(Player player) {
    UUID uuid = player.getUniqueId();
    String enderChestName = ChatColor.LIGHT_PURPLE + "Enderchest: " + player.getName();

    if (enderChestName.length() > 32) {
      enderChestName = enderChestName.substring(0, 30);
    }

    if (!inventories.containsKey(uuid)) {
      Inventory inv = Bukkit.createInventory(player, 45, enderChestName);
      Inventory newInv = formatInventory(inv, player);
      inventories.put(uuid, newInv);
    }
    player.openInventory(inventories.get(uuid));
  }

  private static Inventory formatInventory(Inventory inventory, Player owner) {
    ItemStack glass = GameUtils.getDyeGlassPane(DyeColor.BLACK);
    ItemMeta metaGlass = glass.getItemMeta();
    metaGlass.setDisplayName(ChatColor.BLACK + "");
    glass.setItemMeta(metaGlass);

    for (int i = 0; i < 45; i++) {
      inventory.setItem(i, glass);
    }

    inventory.clear(10);

    if (PermissionUtils.hasPermission(owner, "annihilation.vip.diamond")) {

      inventory.clear(12);

      inventory.clear(14);
      inventory.clear(23);

      inventory.clear(16);
      inventory.clear(25);
      inventory.clear(34);
    } else {

      ItemStack diamondMeta = new ItemStack(Material.DIAMOND);
      ItemMeta metadia = diamondMeta.getItemMeta();
      metadia.setDisplayName(ChatColor.GOLD + "Slot disponible para el rango Gold");
      diamondMeta.setItemMeta(metadia);


      inventory.setItem(16, diamondMeta);
      inventory.setItem(25, diamondMeta);
      inventory.setItem(34, diamondMeta);

      if (PermissionUtils.hasPermission(owner, "annihilation.vip.gold")) {

        inventory.clear(12);

        inventory.clear(14);
        inventory.clear(23);
      } else {

        ItemStack material = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = material.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Slot disponible para el rango Silver");
        material.setItemMeta(meta);

        inventory.setItem(14, material);
        inventory.setItem(23, material);

        if (PermissionUtils.hasPermission(owner, "annihilation.vip.iron")) {

          inventory.clear(12);
        } else {
          ItemStack iron = new ItemStack(Material.IRON_INGOT);
          ItemMeta metairon = iron.getItemMeta();
          metairon.setDisplayName(ChatColor.RED + "Slot disponible para el rango Bronze");
          iron.setItemMeta(metairon);

          inventory.setItem(12, iron);
        }
      }
    }

    return inventory;
  }
}

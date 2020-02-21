package com.hyuchiha.Annihilation.Utils;

import com.hyuchiha.Annihilation.Game.BossStarItem;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Manager.BossManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Manager.VotingManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.List;


public class MenuUtils {
  public static void showTeamSelector(Player p) {
    int size = (GameTeam.teams().length + 8) / 9 * 9;
    Inventory inv = createInventory(p, size, Translator.getColoredString("GAME.CLICK_TO_CHOOSE_TEAM"));
    for (GameTeam team : GameTeam.teams()) {
      Wool wool = new Wool(team.getDyeColor());
      ItemStack teamWoolColored = wool.toItemStack(1);

      ItemMeta im = teamWoolColored.getItemMeta();

      List<String> players = new ArrayList<>();

      for (Player player : team.getPlayers()) {
        players.add("- " + team.getChatColor() + player.getName());

        if (players.size() > 8) {
          break;
        }
      }

      im.setLore(players);
      im.setDisplayName(team.coloredName());
      teamWoolColored.setItemMeta(im);

      inv.addItem(teamWoolColored);
    }

    p.openInventory(inv);
  }

  public static void showMapSelector(Player p) {
    Output.log("Creating Selector");

    int size = (VotingManager.getMaps().size() + 8) / 9 * 9;

    Inventory inv = createInventory(p, size, Translator.getColoredString("GAME.CLICK_TO_VOTE_MAP"));
    for (String map : VotingManager.getMaps().values()) {
      ItemStack i = new ItemStack(Material.MAP);

      ItemMeta im = i.getItemMeta();

      im.setDisplayName(ChatColor.GOLD + map);
      i.setItemMeta(im);

      Output.log("Loading map selector: " + map);

      inv.addItem(i);
    }

    p.openInventory(inv);
  }

  public static void showKitSelector(Player p) {
    int size = ((46 + 8) / 9) * 9;
    Inventory inv = Bukkit.createInventory(p, size, Translator.getColoredString("GAME.CLASS_SELECT_INV_TITLE"));

    ArrayList<Kit> notUnlocked = new ArrayList<>();

    GamePlayer gPlayer = PlayerManager.getGamePlayer(p);

    int kitsCount = 0;
    Kit selectedKit = null;
    if(gPlayer.getKit() != Kit.CIVILIAN){
      selectedKit = gPlayer.getKit();
    }

    //Build the inventory with the kits unlocked
    for (Kit kit : Kit.values()) {
      if (kit.isOwnedBy(p)) {
        ItemStack kitItem = buildItemForSelector(true, kit,selectedKit == kit);
        inv.addItem(kitItem);

        kitsCount++;
      } else {
        //Catch the kits not unlocked from this user
        notUnlocked.add(kit);
      }
    }

    int startPoint = kitsCount;
    kitsCount = (Math.round((kitsCount + 8) / 9) + 1) * 9;

    //Make the remaining Slots from glass
    fillInventoryWithGlass(startPoint, kitsCount, inv);

    //Fill the remaining kits
    for (Kit kit : notUnlocked) {
      ItemStack restantKits = buildItemForSelector(false,kit,false);
      inv.addItem(restantKits);
      kitsCount++;
    }

    //Make the restant Slots from glass
    fillInventoryWithGlass(kitsCount, inv.getSize(), inv);

    p.openInventory(inv);
  }

  public static void showUnlockerSelector(Player p) {
    int size = ((46 + 8) / 9) * 9;
    Inventory inv = Bukkit.createInventory(p, size, Translator.getColoredString("GAME.CLASS_UNLOCK_INV_TITLE"));

    int kitsCount = 0;

    //Build the inventory with the kits unlocked
    for (Kit kit : Kit.values()) {
      if (!kit.isOwnedBy(p)) {
        ItemStack restantKits = buildItemForSelector(false,kit,false);
        inv.addItem(restantKits);
        kitsCount++;
      }
    }

    //Make the restant Slots from glass
    fillInventoryWithGlass(kitsCount, inv.getSize(), inv);

    p.openInventory(inv);
  }

  public static void showConfirmUnlockClass(Player p, Kit kit) {
    Inventory inv = createInventory(p, 9, Translator.getColoredString("GAME.CONFIRM_UNLOCK"));

    ItemStack item = kit.getKit().getIcon().clone();
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(ChatColor.GOLD + kit.getName());
    item.setItemMeta(meta);

    ItemStack yes = new ItemStack(Material.EMERALD_BLOCK);
    ItemMeta yesMeta = yes.getItemMeta();
    yesMeta.setDisplayName(ChatColor.GREEN + Translator.getString("COMMONS.TOTALLY"));
    yes.setItemMeta(yesMeta);

    ItemStack no = new ItemStack(Material.REDSTONE_BLOCK);
    ItemMeta noMeta = no.getItemMeta();
    noMeta.setDisplayName(ChatColor.RED + Translator.getString("COMMONS.NOPE"));
    no.setItemMeta(noMeta);

    inv.setItem(2, yes);
    inv.setItem(4, item);
    inv.setItem(6, no);

    p.openInventory(inv);
  }

  public static void openBossStarMenu(Player player) {
    int size = ((46 + 8) / 9) * 9;
    Inventory inv = createInventory(player, size, Translator.getColoredString("GAME.BOSS_SHOP"));

    for (BossStarItem item: BossManager.getBossStarItems()) {
      inv.setItem(item.getPosition(), item.getItem());
    }

    player.openInventory(inv);

  }

  private static Inventory createInventory(Player player, int slots, String name) {
    return Bukkit.createInventory(player, slots, name);
  }

  private static ItemStack buildItemForSelector(boolean isUnlocked, Kit kit, boolean hasKitSelected){
    ItemStack kitLogo = kit.getKit().getIcon().clone();
    ItemMeta meta = kitLogo.getItemMeta();
    List<String> lore = meta.getLore();
    if (isUnlocked){
      if (!hasKitSelected){
        lore.add(ChatColor.GRAY + "---------------");
        lore.add(ChatColor.GREEN + Translator.getColoredString("GAME.KIT_UNLOCKED"));
        lore.add(ChatColor.GRAY + "---------------");
      }
    } else {
      lore.add(ChatColor.GRAY + "---------------");
      lore.add(ChatColor.RED + Translator.getColoredString("GAME.KIT_LOCKED"));
      lore.add(ChatColor.GRAY + "---------------");
    }

    meta.setLore(lore);
    kitLogo.setItemMeta(meta);

    return  kitLogo;
  }

  private static void fillInventoryWithGlass(int start, int end, Inventory inv){
    ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getDyeData());
    ItemMeta metaglass = glass.getItemMeta();
    metaglass.setDisplayName(ChatColor.BLACK + "");
    glass.setItemMeta(metaglass);

    for (int i = start; i < end; i++) {
      inv.setItem(i, glass);
    }
  }
}

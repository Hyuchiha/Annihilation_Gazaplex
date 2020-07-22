package com.hyuchiha.Annihilation.Game;

import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class Shop implements Listener {
  private String name;
  private ArrayList<ShopItem> items;

  private Main plugin;

  public Shop(Main plugin, String name, Configuration config) {
    this.plugin = plugin;
    this.name = name;

    plugin.getServer().getPluginManager().registerEvents(this, plugin);

    loadConfig(config);
  }

  @EventHandler
  public void onSignClick(PlayerInteractEvent e) {
    if (e.getClickedBlock() != null) {
      Block block = e.getClickedBlock();
      if (GameUtils.isWallSign(block)) {
        Sign sign = (Sign) e.getClickedBlock().getState();
        String line0 = sign.getLine(0);
        String line1 = sign.getLine(1);
        if (line0.contains("Shop") && line1
            .equalsIgnoreCase(this.name)) {
          openShop(e.getPlayer());
        }
      }
    }
  }


  @EventHandler
  public void onShopInventoryClick(InventoryClickEvent e) {
    Player buyer = (Player) e.getWhoClicked();
    if (e.getInventory().getName().equals(this.name + " Shop")) {
      int slot = e.getRawSlot();
      if (slot < e.getInventory().getSize() && slot >= 0) {
        if (slot < this.items.size() && this.items.get(slot) != null) {
          if (this.name.equals("Brewing") && GameManager.getCurrentGame().getPhase() >= 3) {
            sellItem(buyer, this.items.get(slot));
          } else if (this.name.equals("Weapon")) {
            sellItem(buyer, this.items.get(slot));
          }
        }

        e.setCancelled(true);
      }
      buyer.updateInventory();
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void ItemMoveEvent(InventoryMoveItemEvent event) {
    Inventory inv = event.getSource();
    if (inv.getName().equals(this.name + " Shop")) {
      event.setCancelled(true);
    }
  }

  private void openShop(Player player) {
    int size = 9 * (int) Math.ceil(this.items.size() / 9.0D);
    Inventory shopInv = Bukkit.getServer().createInventory(player, size, this.name + " Shop");

    for (int i = 0; i < this.items.size(); i++) {
      ShopItem item = this.items.get(i);
      if (item != null) {
        shopInv.setItem(i, item.getShopStack());
      } else {
        shopInv.setItem(i, null);
      }
    }
    player.openInventory(shopInv);
  }

  private void sellItem(Player buyer, ShopItem item) {
    PlayerInventory playerInventory = buyer.getInventory();
    ItemStack stackToGive = item.getItemStack();
    int price = item.getPrice();

    String stackName = ChatColor.WHITE + item.getName();

    if (playerInventory.contains(Material.GOLD_INGOT, price)) {
      playerInventory.removeItem(new ItemStack(Material.GOLD_INGOT, price));
      playerInventory.addItem(stackToGive);
      buyer.sendMessage(Translator.getPrefix() + Translator.getColoredString("GAME.PURCHASED") + stackName);
    } else {

      buyer.sendMessage(Translator.getPrefix() + Translator.getColoredString("GAME.NOT_ENOUGH_GOLD_TO_PURCHASE") + stackName);
    }
  }

  private void loadConfig(Configuration config) {
    this.items = new ArrayList<>();

    List<String> list = config.getStringList(this.name.toLowerCase());
    for (String entry : list) {
      if (entry.equalsIgnoreCase("nextline")) {
        int end = 9 * (int) Math.ceil(this.items.size() / 9.0D);
        for (int i = this.items.size(); i < end; i++)
          this.items.add(null);
        continue;
      }
      String[] params = entry.split(",");
      if (params.length >= 3) {
        Material type = Material.getMaterial(params[0]);
        int qty = Integer.parseInt(params[1]);
        int price = Integer.parseInt(params[2]);
        ShopItem item = new ShopItem(type, qty, price);
        if (params.length >= 4) {
          String itemName = params[3].replace("\"", "");
          item.setName(ChatColor.translateAlternateColorCodes('&', itemName));
        }

        this.items.add(item);
      }
    }
  }


  public String getName() {
    return this.name;
  }
}

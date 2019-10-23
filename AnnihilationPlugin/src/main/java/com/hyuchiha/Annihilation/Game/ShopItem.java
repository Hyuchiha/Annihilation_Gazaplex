package com.hyuchiha.Annihilation.Game;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class ShopItem {
    private final ItemStack item;
    private final int price;

    public ShopItem(Material type, int qty, int price) {
        this.item = new ItemStack(type);
        this.price = price;
        this.item.setAmount(qty);
    }

    public ShopItem(ItemStack type, int price) {
        this.item = type;
        this.price = price;
    }

    public void addEnchant(Enchantment enchant, int level) {
        this.item.addUnsafeEnchantment(enchant, level);
    }

    public ItemStack getShopStack() {
        ItemStack stack = this.item.clone();
        String priceStr = ChatColor.GOLD.toString() + this.price + " Gems";
        ItemMeta meta = stack.getItemMeta();
        if (meta.hasLore()) {
            meta.getLore().add(priceStr);
        } else {
            meta.setLore(Collections.singletonList(priceStr));
        }
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack getItemStack() {
        return this.item;
    }

    public int getPrice() {
        return this.price;
    }

    public String getName() {
        String name;
        ItemMeta meta = this.item.getItemMeta();
        if (meta.hasDisplayName()) {
            name = meta.getDisplayName();
        } else {
            name = this.item.getType().name();
            name = name.replace("_", " ").toLowerCase();
            name = WordUtils.capitalize(name);
            name = name + ChatColor.WHITE;
        }
        if (this.item.getAmount() > 1) {
            name = this.item.getAmount() + " " + name;
        }

        return name;
    }

    public ShopItem setName(String name) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + name);
        this.item.setItemMeta(meta);
        return this;
    }
}

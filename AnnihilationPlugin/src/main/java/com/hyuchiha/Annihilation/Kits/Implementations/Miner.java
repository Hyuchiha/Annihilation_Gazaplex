package com.hyuchiha.Annihilation.Kits.Implementations;

import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Miner extends BaseKit {
    // https://wiki.shotbow.net/Miner
    public Miner(String name, ItemStack icon, ConfigurationSection section) {
        super(name, icon, section);
    }

    @Override
    protected void setupSpawnItems() {
        spawnItems.add(new ItemStack(Material.WOOD_SWORD));

        ItemStack pickaxe = new ItemStack(Material.STONE_PICKAXE);
        pickaxe.addEnchantment(Enchantment.DIG_SPEED, 1);
        spawnItems.add(pickaxe);

        spawnItems.add(new ItemStack(Material.WOOD_AXE));
        spawnItems.add(new ItemStack(Material.WOOD_SPADE));
        spawnItems.add(new ItemStack(Material.FURNACE));
        spawnItems.add(new ItemStack(Material.COAL, 5));
    }

    @Override
    protected void giveSpecialPotions(Player recipient) {
        // Not needed
    }

    @Override
    protected void giveExtraHearts(Player recipient) {
        // Not needed
    }

    @Override
    protected void extraConfiguration(Player recipient) {
        // Not needed
    }

    @Override
    public void removePlayer(Player recipient) {
        // Nope
    }

    @Override
    public void resetData() {
        // Nope
    }

    @Override
    public int getMaterialDropMultiplier() {
        return 2;
    }
}

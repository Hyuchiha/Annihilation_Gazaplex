package com.hyuchiha.Annihilation.Kits.Implementations;

import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Civilian extends BaseKit {

  public Civilian(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);
  }

  @Override
  protected void setupSpawnItems() {
    spawnItems.add(new ItemStack(Material.WOOD_SWORD));
    spawnItems.add(new ItemStack(Material.WOOD_PICKAXE));
    spawnItems.add(new ItemStack(Material.WOOD_AXE));
    spawnItems.add(new ItemStack(Material.WOOD_SPADE));
    spawnItems.add(new ItemStack(Material.WORKBENCH));
  }

  @Override
  protected void giveSpecialPotions(Player recipient) {
    // No special potions for you
  }

  @Override
  protected void giveExtraHearts(Player recipient) {
    // And no extra hearts cause your a noob
  }

  @Override
  protected void extraConfiguration(Player recipient) {

  }

  @Override
  public void removePlayer(Player recipient) {
    /// Nothing to remove from the noobs
  }

  @Override
  public void resetData() {
    // We dont needed, the noobs dont have additional items to remove
  }
}

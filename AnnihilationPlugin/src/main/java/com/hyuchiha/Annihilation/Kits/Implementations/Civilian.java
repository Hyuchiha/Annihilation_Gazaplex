package com.hyuchiha.Annihilation.Kits.Implementations;

import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Utils.XMaterial;
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
    spawnItems.add(XMaterial.WOODEN_SWORD.parseItem());
    spawnItems.add(XMaterial.WOODEN_PICKAXE.parseItem());
    spawnItems.add(XMaterial.WOODEN_AXE.parseItem());
    spawnItems.add(XMaterial.WOODEN_SHOVEL.parseItem());
    spawnItems.add(XMaterial.CRAFTING_TABLE.parseItem());
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

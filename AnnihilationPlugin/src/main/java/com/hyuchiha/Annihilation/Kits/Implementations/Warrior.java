package com.hyuchiha.Annihilation.Kits.Implementations;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Utils.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class Warrior extends BaseKit {
  public Warrior(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);
  }

  @Override
  protected void setupSpawnItems() {
    spawnItems.add(XMaterial.WOODEN_SWORD.parseItem());
    spawnItems.add(XMaterial.WOODEN_PICKAXE.parseItem());
    spawnItems.add(XMaterial.WOODEN_AXE.parseItem());

    ItemStack potion = new ItemStack(Material.POTION, 1);
    PotionMeta meta = (PotionMeta) potion.getItemMeta();
    meta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL, false, false));
    potion.setItemMeta(meta);
    spawnItems.add(potion);
  }

  @Override
  protected void giveSpecialPotions(Player recipient) {
    // Not needed
  }

  @Override
  protected void giveExtraHearts(Player recipient) {
    // Noup
  }

  @Override
  protected void extraConfiguration(Player recipient) {
    // Noup
  }

  @Override
  public void removePlayer(Player recipient) {
    // Ok, but no
  }

  @Override
  public void resetData() {
    // Nothing to store, nothing to reset
  }

  //Adds the +1 melee damage. May need to be changed to work just for melee WEAPONS and not every melee attack. idk.
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void damageListener(final EntityDamageByEntityEvent event) {
    Entity one = event.getDamager();
    if (one.getType() == EntityType.PLAYER) {
      Player damager = (Player) one;
      GamePlayer gPlayer = PlayerManager.getGamePlayer(damager);
      if (gPlayer.getKit() == Kit.WARRIOR) {
        event.setDamage(event.getDamage() + 1);
      }
    }
  }
}

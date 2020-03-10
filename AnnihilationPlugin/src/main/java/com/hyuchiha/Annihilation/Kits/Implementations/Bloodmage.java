package com.hyuchiha.Annihilation.Kits.Implementations;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class Bloodmage extends BaseKit {
  private final Random random = new Random();

  // https://shotbow.net/forum/wiki/anni-bloodmage/
  public Bloodmage(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);
  }

  @Override
  protected void setupSpawnItems() {
    spawnItems.add(new ItemStack(Material.STONE_SWORD));
    spawnItems.add(new ItemStack(Material.WOOD_PICKAXE));
    spawnItems.add(new ItemStack(Material.WOOD_AXE));

    ItemStack abilityItem = new ItemStack(Material.FERMENTED_SPIDER_EYE, 1);
    ItemMeta meta = abilityItem.getItemMeta();
    meta.setDisplayName(Translator.getColoredString("KITS.BLOODMAGE_ITEM"));
    abilityItem.setItemMeta(meta);
    spawnItems.add(abilityItem);
  }

  @Override
  protected void giveSpecialPotions(Player recipient) {
    // This kit dont need pots
  }

  @Override
  protected void giveExtraHearts(Player recipient) {
    // Meh, this is not needed
  }

  @Override
  protected void extraConfiguration(Player recipient) {
    // Noup
  }

  @Override
  public void removePlayer(Player recipient) {
    // mmmmmmm.... posible but for now is not needed :)
  }

  @Override
  public void resetData() {
    // There are not items that require reset data
  }

  @EventHandler()
  public void onBloodMageDamage(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
      Player entity = (Player) event.getEntity();
      Player damager = (Player) event.getDamager();

      GamePlayer gpDamager = PlayerManager.getGamePlayer(damager);

      if (gpDamager.getKit() == Kit.BLOODMAGE && random.nextInt(3) == 0) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 1));
      }
    }
  }

}

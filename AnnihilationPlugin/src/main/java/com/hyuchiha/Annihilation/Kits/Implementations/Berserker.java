package com.hyuchiha.Annihilation.Kits.Implementations;

import com.cryptomorin.xseries.XAttribute;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Utils.PotionUtils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class Berserker extends BaseKit {
  // https://shotbow.net/forum/wiki/anni-berserker/
  public Berserker(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);
  }

  @Override
  protected void setupSpawnItems() {
    spawnItems.add(XMaterial.STONE_SWORD.parseItem());
    spawnItems.add(XMaterial.WOODEN_PICKAXE.parseItem());
    spawnItems.add(XMaterial.WOODEN_AXE.parseItem());

    ItemStack potion = PotionUtils.getBasePotionType(XPotion.INSTANT_HEALTH.getPotionType(), 1);
    spawnItems.add(potion);
  }

  @Override
  protected void giveSpecialPotions(Player recipient) {
    // Not needed for this kit
  }

  @Override
  protected void giveExtraHearts(Player recipient) {
    // Finally, this is needed

    AttributeInstance attribute = recipient.getAttribute(XAttribute.MAX_HEALTH.get());
    attribute.setBaseValue(14.0D);
    recipient.setHealth(14.0D);
  }

  @Override
  protected void extraConfiguration(Player recipient) {
    // Not needed
  }

  @Override
  public void removePlayer(Player recipient) {
    // I think this is not needed
  }

  @Override
  public void resetData() {
    // Noooooooot nedeeed
  }

  @EventHandler
  public void onPlayerKilled(PlayerDeathEvent event) {
    Player playerKilled = event.getEntity();

    if (GameManager.getCurrentGame().isInGame() && playerKilled.getKiller() != null && !playerKilled.getKiller().equals(playerKilled)) {
      Player killer = playerKilled.getKiller();
      GamePlayer gpKiller = PlayerManager.getGamePlayer(killer);

      if (gpKiller.getKit() == Kit.BERSERKER) {
        double currentMaxHealth = killer.getMaxHealth();

        if (currentMaxHealth < 40.0D) {
          AttributeInstance attribute = killer.getAttribute(XAttribute.MAX_HEALTH.get());
          attribute.setBaseValue(currentMaxHealth + 2.0D);
          killer.setHealth(currentMaxHealth + 2.0D);
        }
      }

    }
  }

  @EventHandler
  public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
      Player entity = (Player) event.getEntity();
      Player damager = (Player) event.getDamager();

      GamePlayer gpDamager = PlayerManager.getGamePlayer(damager);

      if (gpDamager.getKit() == Kit.BERSERKER) {
        double armorEntity = entity.getAttribute(XAttribute.ARMOR.get()).getValue();
        double armorDamager = damager.getAttribute(XAttribute.ARMOR.get()).getValue();

        if (armorEntity > armorDamager) {
          double extraDamage = (armorEntity - armorDamager) / 2.5;
          event.setDamage(event.getDamage() + extraDamage);
        }

      }
    }
  }
}

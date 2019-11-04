package com.hyuchiha.Annihilation.Mobs.CustomMob;

import com.hyuchiha.Annihilation.Mobs.EnemyIA;
import com.hyuchiha.Annihilation.Mobs.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WitherMob extends EnemyIA {

  public WitherMob(EntityType type, int health, String name) {
    super(type, health, name);
  }

  @Override
  public LivingEntity spawnEntity(Location spawnLocation, Plugin plugin) {
    Bukkit.getWorld(spawnLocation.getWorld().getName()).loadChunk(spawnLocation.getChunk());
    Entity e = getEntityType().spawnEntity(spawnLocation);

    if (!(e instanceof LivingEntity)) {
      e.remove();
      return null;
    }

    LivingEntity entity = (LivingEntity) e;
    org.bukkit.entity.Wither wither = (org.bukkit.entity.Wither) entity;

    wither.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
    wither.setHealth(getHealth());
    wither.setCanPickupItems(false);
    wither.setRemoveWhenFarAway(false);

    String customName = ChatColor.translateAlternateColorCodes('&', getCustomName() + " &8» &a" + getHealth() + " HP");
    wither.setCustomNameVisible(true);
    wither.setCustomName(customName);

    return wither;
  }
}

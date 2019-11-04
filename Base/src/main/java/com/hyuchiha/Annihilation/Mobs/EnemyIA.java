package com.hyuchiha.Annihilation.Mobs;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

public abstract class EnemyIA {

  private EntityType entityType;
  private int health = 20;
  private String customName = "";

  public EnemyIA(EntityType entityType) {
    this.entityType = entityType;
  }

  public EnemyIA(EntityType entityType, int health) {
    this.entityType = entityType;
    this.health = health;
  }

  public EnemyIA(EntityType entityType, int health, String name) {
    this.entityType = entityType;
    this.health = health;
    this.customName = name;
  }

  public EntityType getEntityType() {
    return entityType;
  }

  public int getHealth() {
    return health;
  }

  public String getCustomName() {
    return customName;
  }

  public void setCustomName(String customName) {
    this.customName = customName;
  }

  public void setHealth(int health) {
    this.health = health;
  }

  public abstract LivingEntity spawnEntity(Location spawnLocation, Plugin plugin);
}

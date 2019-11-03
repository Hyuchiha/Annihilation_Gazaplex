package com.hyuchiha.Annihilation.Game;

import org.bukkit.Location;

public class GameBoss {
  private int health;
  private String bossName;
  private Location bossSpawn;
  private Location chest;

  public GameBoss(int health, String bossName, Location bossSpawn, Location chest) {
    this.health = health;
    this.bossName = bossName;
    this.bossSpawn = bossSpawn;
    this.chest = chest;
  }


  public int getHealth() {
    return this.health;
  }


  public void setHealth(int health) {
    this.health = health;
  }


  public String getBossName() {
    return this.bossName;
  }


  public Location getChest() {
    return this.chest;
  }


  public Location getBossSpawn() {
    return this.bossSpawn;
  }
}

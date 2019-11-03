package com.hyuchiha.Annihilation.Arena;

import com.hyuchiha.Annihilation.Game.GameTeam;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class Nexus {
  private final GameTeam team;
  private final Location location;
  private int health;

  public Nexus(GameTeam team, Location location, int health) {
    this.team = team;
    this.location = location;
    this.health = health;

    location.getBlock().setType(Material.ENDER_STONE);
  }

  public GameTeam getTeam() {
    return this.team;
  }


  public Location getLocation() {
    return this.location;
  }


  public int getHealth() {
    return this.health;
  }


  public void damage(int amount) {
    this.health -= amount;
    if (this.health <= 0) {
      this.health = 0;
      this.location.getBlock().setType(Material.BEDROCK);
    }
  }


  public boolean isAlive() {
    return this.health > 0;
  }


  public Block getBlock() {
    return this.location.getBlock();
  }
}

package com.hyuchiha.Annihilation.Object;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Loc {

  private double x;
  private double y;
  private double z;
  private double pitch;
  private double yaw;
  private String world;

  public Loc(Location loc) {
    this.x = loc.getX();
    this.y = loc.getY();
    this.z = loc.getZ();
    this.pitch = loc.getPitch();
    this.yaw = loc.getY();
    this.world = loc.getWorld().getName();
  }

  public Location toLocation() {
    return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z, (float) this.yaw, (float) this.pitch);
  }

  public boolean isEqual(Loc loc) {
    return (loc.x == this.x) && (loc.y == this.y) && (loc.z == this.z) && (loc.world.equalsIgnoreCase(this.world));
  }

  public static Location getMiddle(Location loc) {
    Location k = loc.clone();
    k.setX(k.getBlockX() + 0.5D);

    k.setZ(k.getBlockZ() + 0.5D);
    return k;
  }
}

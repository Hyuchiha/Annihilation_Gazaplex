package com.hyuchiha.Annihilation.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


public class LocationUtils {
  public static Location parseLocation(World w, String in) {
    String[] params = in.split(",");
    for (String s : params) {
      s.replace("-0", "0");
    }
    if (params.length == 3 || params.length == 5) {
      double x = Double.parseDouble(params[0]);
      double y = Double.parseDouble(params[1]);
      double z = Double.parseDouble(params[2]);
      Location loc = new Location(w, x, y, z);
      if (params.length == 5) {
        loc.setYaw(Float.parseFloat(params[4]));
        loc.setPitch(Float.parseFloat(params[5]));
      }
      return loc;
    }
    return null;
  }

  public static boolean isEmptyColumn(Location loc) {
    Location test = loc.clone();
    for (int y = 0; y < loc.getWorld().getMaxHeight(); y++) {
      test.setY(y);
      if (test.getBlock().getType() != Material.AIR) {
        return false;
      }
    }

    return true;
  }


  public static boolean isSameBlock(Block b, Block bo) {
    Location loc = b.getLocation();
    Location lo = bo.getLocation();
    if (loc.getBlockX() != lo.getBlockX()) {
      return false;
    }
    if (loc.getBlockY() != lo.getBlockY()) {
      return false;
    }
    if (loc.getBlockZ() != lo.getBlockZ()) {
      return false;
    }
    if (loc.getWorld() != lo.getWorld()) {
      return false;
    }
    return (b.getType() == bo.getType());
  }

  public static boolean isFallingToVoid(Player p) {
    Location loc = p.getLocation();
    int a = p.getLocation().getBlockY();
    for (int i = a; i >= 0; i--) {
      loc.add(0, -1, 0);
      if (Bukkit.getWorld(loc.getWorld().getName()).getBlockAt(loc).getType() != Material.AIR) {
        return false;
      }
    }
    return true;
  }

  public static double pointToLineDistance(Location A, Location B, Location P) {
    double normalLength = Math.sqrt((B.getX() - A.getX()) * (B.getX() - A.getX()) + (B
                                                                                         .getZ() - A.getZ()) * (B.getZ() - A.getZ()));
    return Math.abs((P.getX() - A.getX()) * (B.getZ() - A.getZ()) - (P.getZ() - A.getZ()) * (B.getX() - A.getX())) / normalLength;
  }
}

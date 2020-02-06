package com.hyuchiha.Annihilation.Manager;

import com.hyuchiha.Annihilation.Game.Resource;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResourceManager {
  private static final HashMap<Material, Resource> resources = new HashMap<>();
  private static final List<Location> diamonds = new ArrayList<>();

  public static void initializeResources() {
    addResource(Material.COAL_ORE, 4, 10);
    addResource(Material.IRON_ORE, 5, 20);
    addResource(Material.GOLD_ORE, 5, 20);
    addResource(Material.DIAMOND_ORE, 8, 30);
    addResource(Material.EMERALD_ORE, 8, 40);
    addResource(Material.REDSTONE_ORE, 5, 20);
    addResource(Material.GLOWING_REDSTONE_ORE, 5, 20);
    addResource(Material.LOG, 2, 10);
    addResource(Material.LOG_2, 2, 10);
    addResource(Material.GRAVEL, 2, 20);
    addResource(Material.MELON_BLOCK, 0, 10);
    addResource(Material.LAPIS_ORE, 3, 10);
    addResource(Material.PUMPKIN, 1, 10);
  }

  public static boolean containsResource(Material type) {
    return resources.containsKey(type);
  }


  public static Resource getResource(Material type) {
    return resources.get(type);
  }


  public static void loadDiamonds(List<Location> locations) {
    for (Location loc : locations) {
      if (loc.getBlock().getType() == Material.DIAMOND_ORE) {
        loc.getBlock().setType(Material.AIR);
      }
      diamonds.add(loc);
    }
  }

  public static void spawnDiamonds() {
    for (Location loc : diamonds) {
      loc.getBlock().setType(Material.DIAMOND_ORE);
    }
  }


  public static void clearDiamonds() {
    diamonds.clear();
  }


  private static void addResource(Material type, int xp, int delay) {
    resources.put(type, new Resource(getDropMaterial(type), xp, delay));
  }


  private static Material getDropMaterial(Material type) {
    switch (type) {
      case COAL_ORE:
        return Material.COAL;
      case DIAMOND_ORE:
        return Material.DIAMOND;
      case EMERALD_ORE:
        return Material.EMERALD;
      case GLOWING_REDSTONE_ORE:
      case REDSTONE_ORE:
        return Material.REDSTONE;
      case MELON_BLOCK:
        return Material.MELON;
      case GRAVEL:
        return null;
    }
    return type;
  }
}

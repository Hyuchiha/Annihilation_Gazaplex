package com.hyuchiha.Annihilation.Manager;

import com.hyuchiha.Annihilation.Game.Resource;
import com.hyuchiha.Annihilation.Utils.XMaterial;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResourceManager {
  private static final HashMap<Material, Resource> resources = new HashMap<>();
  private static final List<Location> diamonds = new ArrayList<>();

  public static void initializeResources() {
    addResource(Material.COAL_ORE, 2, 10);
    addResource(Material.IRON_ORE, 3, 20);
    addResource(Material.GOLD_ORE, 4, 20);
    addResource(Material.DIAMOND_ORE, 5, 30);
    addResource(Material.EMERALD_ORE, 6, 40);
    addResource(Material.REDSTONE_ORE, 4, 20);
    // addResource(Material.GLOWING_REDSTONE_ORE, 5, 20);

    addResource(XMaterial.ACACIA_LOG.parseMaterial(), 1, 10);
    addResource(XMaterial.BIRCH_LOG.parseMaterial(), 1, 10);
    addResource(XMaterial.DARK_OAK_LOG.parseMaterial(), 1, 10);
    addResource(XMaterial.JUNGLE_LOG.parseMaterial(), 1, 10);
    addResource(XMaterial.OAK_LOG.parseMaterial(), 1, 10);
    addResource(XMaterial.SPRUCE_LOG.parseMaterial(), 1, 10);
    addResource(XMaterial.STRIPPED_ACACIA_LOG.parseMaterial(), 1, 10);
    addResource(XMaterial.STRIPPED_BIRCH_LOG.parseMaterial(), 1, 10);
    addResource(XMaterial.STRIPPED_DARK_OAK_LOG.parseMaterial(), 1, 10);
    addResource(XMaterial.STRIPPED_JUNGLE_LOG.parseMaterial(), 1, 10);
    addResource(XMaterial.STRIPPED_OAK_LOG.parseMaterial(), 1, 10);
    addResource(XMaterial.STRIPPED_SPRUCE_LOG.parseMaterial(), 1, 10);

    addResource(Material.GRAVEL, 1, 20);
    addResource(XMaterial.MELON.parseMaterial(), 1, 10);
    addResource(Material.LAPIS_ORE, 2, 10);
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
    XMaterial parsedMaterialType = XMaterial.matchXMaterial(type);
    switch (parsedMaterialType) {
      case COAL_ORE:
        return Material.COAL;
      case DIAMOND_ORE:
        return Material.DIAMOND;
      case EMERALD_ORE:
        return Material.EMERALD;
      case REDSTONE_ORE:
        return Material.REDSTONE;
      case MELON:
        return XMaterial.MELON_SLICE.parseMaterial();
      case GRAVEL:
        return null;
    }
    return type;
  }
}

package com.hyuchiha.Annihilation.Manager;

import com.cryptomorin.xseries.XMaterial;
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
    addResource(XMaterial.COAL_ORE.get(), 2, 10);
    addResource(XMaterial.IRON_ORE.get(), 3, 20);
    addResource(XMaterial.GOLD_ORE.get(), 4, 20);
    addResource(XMaterial.DIAMOND_ORE.get(), 5, 30);
    addResource(XMaterial.EMERALD_ORE.get(), 6, 40);
    addResource(XMaterial.REDSTONE_ORE.get(), 4, 20);
    // addResource(Material.GLOWING_REDSTONE_ORE, 5, 20);

    addResource(XMaterial.ACACIA_LOG.get(), 1, 10);
    addResource(XMaterial.BIRCH_LOG.get(), 1, 10);
    addResource(XMaterial.DARK_OAK_LOG.get(), 1, 10);
    addResource(XMaterial.JUNGLE_LOG.get(), 1, 10);
    addResource(XMaterial.OAK_LOG.get(), 1, 10);
    addResource(XMaterial.SPRUCE_LOG.get(), 1, 10);
    addResource(XMaterial.STRIPPED_ACACIA_LOG.get(), 1, 10);
    addResource(XMaterial.STRIPPED_BIRCH_LOG.get(), 1, 10);
    addResource(XMaterial.STRIPPED_DARK_OAK_LOG.get(), 1, 10);
    addResource(XMaterial.STRIPPED_JUNGLE_LOG.get(), 1, 10);
    addResource(XMaterial.STRIPPED_OAK_LOG.get(), 1, 10);
    addResource(XMaterial.STRIPPED_SPRUCE_LOG.get(), 1, 10);

    addResource(XMaterial.GRAVEL.get(), 1, 20);
    addResource(XMaterial.MELON.get(), 1, 10);
    addResource(XMaterial.LAPIS_ORE.get(), 2, 10);
    addResource(XMaterial.PUMPKIN.get(), 1, 10);
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
        return XMaterial.COAL.get();
      case DIAMOND_ORE:
        return XMaterial.DIAMOND.get();
      case EMERALD_ORE:
        return XMaterial.EMERALD.get();
      case REDSTONE_ORE:
        return XMaterial.REDSTONE.get();
      case MELON:
        return XMaterial.MELON_SLICE.get();
      case GRAVEL:
        return null;
    }
    return type;
  }
}

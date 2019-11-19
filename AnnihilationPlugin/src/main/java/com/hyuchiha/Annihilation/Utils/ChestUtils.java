package com.hyuchiha.Annihilation.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ChestUtils {
  public static ItemStack getRandomItem(HashMap<ItemStack, Float> weighting) {
    List<ItemStack> items = new ArrayList<ItemStack>(weighting.keySet());

    float totalWeight = 0F;
    for (Float f : weighting.values()) {
      totalWeight += f;
    }

    float rand = new Random().nextFloat() * totalWeight;
    for (int i = 0; i < weighting.size(); i++) {
      ItemStack item = items.get(i);
      rand -= weighting.get(item);
      if (rand <= 0F) {
        return item;
      }
    }
    return null;
  }

  public static boolean isEmpty(Inventory inv, int slot) {
    ItemStack stack = inv.getItem(slot);
    if (stack == null) {
      return true;
    }
    return stack.getType() == Material.AIR;
  }
}

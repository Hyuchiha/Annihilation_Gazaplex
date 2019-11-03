package com.hyuchiha.Annihilation.VirtualEntities;

import org.bukkit.inventory.InventoryHolder;

public interface VirtualBrewingStand {

  boolean canMakePotions();

  void makePotions();

  InventoryHolder getOwner();

  void openBrewingStand();

}

package com.hyuchiha.Annihilation.VirtualEntities;

import org.bukkit.inventory.InventoryHolder;

public interface VirtualFurnace {
    boolean canCook();

    void cook();

    InventoryHolder getOwner();

    void openFurnace();
}

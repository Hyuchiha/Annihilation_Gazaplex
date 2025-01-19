package com.hyuchiha.Annihilation.VirtualEntities;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BrewingManager_v1_20_R3 extends BrewingManager {

    public BrewingManager_v1_20_R3(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected VirtualBrewingStand createBrewingStand(Player player) {
        return new VirtualBrewingStand_v1_20_R3(player);
    }
}

package com.hyuchiha.Annihilation.VirtualEntities;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BrewingManager_v1_16_R2 extends BrewingManager{
  public BrewingManager_v1_16_R2(Plugin plugin) {
    super(plugin);
  }

  @Override
  VirtualBrewingStand createBrewingStand(Player player) {
    return new VirtualBrewingStand_v1_16_R2(player);
  }
}

package com.hyuchiha.Annihilation.VirtualEntities;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FurnaceManager_v1_13_R2 extends FurnaceManager {
  public FurnaceManager_v1_13_R2(Plugin plugin) {
    super(plugin);
  }

  @Override
  VirtualFurnace createFurnace(Player player) {
    return new VirtualFurnace_v1_13_R2(player);
  }
}

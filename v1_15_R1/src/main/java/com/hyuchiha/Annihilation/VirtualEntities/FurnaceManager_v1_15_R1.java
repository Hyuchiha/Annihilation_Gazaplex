package com.hyuchiha.Annihilation.VirtualEntities;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FurnaceManager_v1_15_R1 extends FurnaceManager{
  public FurnaceManager_v1_15_R1(Plugin plugin) {
    super(plugin);
  }

  @Override
  protected VirtualFurnace createFurnace(Player player) {
    return new VirtualFurnace_v1_15_R1(player);
  }
}

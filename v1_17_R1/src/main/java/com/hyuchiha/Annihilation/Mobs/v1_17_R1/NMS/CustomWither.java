package com.hyuchiha.Annihilation.Mobs.v1_17_R1.NMS;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.level.World;

public class CustomWither extends EntityWither {
  public CustomWither(World world) {
    super(EntityTypes.aZ, world);
  }
}

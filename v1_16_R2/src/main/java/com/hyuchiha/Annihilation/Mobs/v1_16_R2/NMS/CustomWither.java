package com.hyuchiha.Annihilation.Mobs.v1_16_R2.NMS;

import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.EntityWither;
import net.minecraft.server.v1_16_R2.World;

public class CustomWither extends EntityWither {
  public CustomWither(World world) {
    super(EntityTypes.WITHER, world);
  }
}

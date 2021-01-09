package com.hyuchiha.Annihilation.Mobs.v1_16_R3.NMS;

import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EntityWither;
import net.minecraft.server.v1_16_R3.World;

public class CustomWither extends EntityWither {
  public CustomWither(World world) {
    super(EntityTypes.WITHER, world);
  }
}

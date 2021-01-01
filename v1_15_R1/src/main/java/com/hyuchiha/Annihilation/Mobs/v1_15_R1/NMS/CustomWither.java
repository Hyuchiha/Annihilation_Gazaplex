package com.hyuchiha.Annihilation.Mobs.v1_15_R1.NMS;

import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.EntityWither;
import net.minecraft.server.v1_15_R1.World;

public class CustomWither extends EntityWither {
  public CustomWither(World world) {
    super(EntityTypes.WITHER, world);
  }
}

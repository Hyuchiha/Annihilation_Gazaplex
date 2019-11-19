package com.hyuchiha.Annihilation.Mobs.v1_11_R1;

import com.hyuchiha.Annihilation.Mobs.EntityType;
import com.hyuchiha.Annihilation.Mobs.MobCreator;

public class MobCreator_v1_11_R1 implements MobCreator {
  @Override
  public EntityType getMob(String type) {
    return CustomEntityType.valueOf(type);
  }
}

package com.hyuchiha.Annihilation.Mobs.v1_16_R3;

import com.hyuchiha.Annihilation.Mobs.EntityType;
import com.hyuchiha.Annihilation.Mobs.MobCreator;

public class MobCreator_v1_16_R3 implements MobCreator {
  @Override
  public EntityType getMob(String type) {
    return CustomEntityType.valueOf(type);
  }
}

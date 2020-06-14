package com.hyuchiha.Annihilation.Mobs.v1_13_R1.NMS;

import com.hyuchiha.Annihilation.Mobs.MobUtils;
import net.minecraft.server.v1_13_R1.*;

import java.util.Set;

public class CustomZombie extends EntityZombie {
  public CustomZombie(World world) {
    super(world);
  }

  @Override
  protected void n() {
    Set goalB = (Set) MobUtils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
    goalB.clear();
    Set goalC = (Set) MobUtils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
    goalC.clear();
    Set targetB = (Set) MobUtils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
    targetB.clear();
    Set targetC = (Set) MobUtils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
    targetC.clear();

    this.goalSelector.a(0, new PathfinderGoalFloat(this));
    this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
    this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
    this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
    this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
    this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));

    this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true));
    this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityVillager.class, true, true));
    this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true, false));
  }

}

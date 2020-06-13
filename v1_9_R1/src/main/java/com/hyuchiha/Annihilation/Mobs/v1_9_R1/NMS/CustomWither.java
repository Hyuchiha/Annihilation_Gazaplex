package com.hyuchiha.Annihilation.Mobs.v1_9_R1.NMS;

import com.hyuchiha.Annihilation.Mobs.MobUtils;
import com.hyuchiha.Annihilation.Mobs.v1_9_R1.Pathfinders.PathfinderGoalWalkToLocation;
import net.minecraft.server.v1_9_R1.*;

import java.util.List;

public class CustomWither extends EntityWither {

  public CustomWither(World world) {
    super(world);
  }

  @Override
  protected void r() {
    List goalB = (List) MobUtils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
    goalB.clear();
    List goalC = (List) MobUtils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
    goalC.clear();
    List targetB = (List) MobUtils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
    targetB.clear();
    List targetC = (List) MobUtils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
    targetC.clear();


    this.goalSelector.a(0, new PathfinderGoalFloat(this));
    this.goalSelector.a(1, new PathfinderGoalWalkToLocation(this, this.getBukkitEntity().getLocation(), 2.0));
    this.goalSelector.a(2, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
    this.goalSelector.a(3, new PathfinderGoalArrowAttack(this, 1.0D, 40, 20.0F));
    this.goalSelector.a(4, new PathfinderGoalRandomLookaround(this));

    this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<EntityHuman>(this, EntityHuman.class, true));
    this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
  }
}

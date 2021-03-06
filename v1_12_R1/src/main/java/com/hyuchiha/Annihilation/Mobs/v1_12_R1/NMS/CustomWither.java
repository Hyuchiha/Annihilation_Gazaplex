package com.hyuchiha.Annihilation.Mobs.v1_12_R1.NMS;

import com.hyuchiha.Annihilation.Mobs.MobUtils;
import com.hyuchiha.Annihilation.Mobs.v1_12_R1.Pathfinders.PathfinderGoalWalkToLocation;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;

import java.util.Set;

public class CustomWither extends EntityWither {

  public CustomWither(World world) {
    super(world);

    this.persistent = true;
  }

  @Override
  protected void r() {
    Set goalB = (Set) MobUtils.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
    goalB.clear();
    Set goalC = (Set) MobUtils.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
    goalC.clear();
    Set targetB = (Set) MobUtils.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
    targetB.clear();
    Set targetC = (Set) MobUtils.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
    targetC.clear();

    Location location = this.getBukkitEntity().getLocation();

    this.goalSelector.a(0, new PathfinderGoalFloat(this));
    this.goalSelector.a(1, new PathfinderGoalWalkToLocation(this, 0.28d, location.getX(), location.getY(), location.getZ()));
    this.goalSelector.a(2, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
    this.goalSelector.a(3, new PathfinderGoalArrowAttack(this, 1.0D, 40, 20.0F));
    this.goalSelector.a(4, new PathfinderGoalRandomLookaround(this));

    this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<EntityHuman>(this, EntityHuman.class, true));
    this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
  }

}

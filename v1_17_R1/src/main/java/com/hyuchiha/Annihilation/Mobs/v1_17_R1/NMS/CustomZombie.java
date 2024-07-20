package com.hyuchiha.Annihilation.Mobs.v1_17_R1.NMS;

import com.hyuchiha.Annihilation.Mobs.MobUtils;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.EntityPigZombie;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;

import java.util.Set;

public class CustomZombie extends EntityZombie {
  public CustomZombie(World world) {
    super(world);
    Set goalB = (Set) MobUtils.getPrivateField("d", PathfinderGoalSelector.class, bO);
    goalB.clear();

    Set targetB = (Set) MobUtils.getPrivateField("d", PathfinderGoalSelector.class, bP);
    targetB.clear();

    this.bO.a(0, new PathfinderGoalFloat(this));
    this.bO.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
    this.bO.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, true));
    this.bO.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
    this.bO.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, true, 4, this::ca));
    this.bO.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
    this.bO.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
    this.bO.a(8, new PathfinderGoalRandomLookaround(this));
    this.bP.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a(new Class[]{EntityPigZombie.class}));
    this.bP.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillager.class, true, true));
    this.bP.a(5, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true, false));
  }
}

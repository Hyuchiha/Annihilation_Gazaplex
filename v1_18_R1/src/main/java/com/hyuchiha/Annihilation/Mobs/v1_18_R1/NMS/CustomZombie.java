package com.hyuchiha.Annihilation.Mobs.v1_18_R1.NMS;

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
        Set goalB = (Set) MobUtils.getPrivateField("d", PathfinderGoalSelector.class, bR);
        goalB.clear();

        Set targetB = (Set) MobUtils.getPrivateField("d", PathfinderGoalSelector.class, bS);
        targetB.clear();

        this.bR.a(0, new PathfinderGoalFloat(this));
        this.bR.a(2, new PathfinderGoalZombieAttack(this, 1.0D, false));
        this.bR.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.bR.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.bR.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, true, 4, this::cf));
        this.bR.a(7, new PathfinderGoalRandomStroll(this, 1.0D));
        this.bR.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.bR.a(8, new PathfinderGoalRandomLookaround(this));
        this.bS.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a(new Class[]{EntityPigZombie.class}));
        this.bS.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillager.class, true, true));
        this.bS.a(5, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true, false));
    }

//    @Override
//    protected boolean x() {
//        return false;
//    }
}

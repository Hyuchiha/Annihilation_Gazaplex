package com.hyuchiha.Annihilation.Mobs.v1_9_R2.Pathfinders;

import net.minecraft.server.v1_9_R2.EntityInsentient;
import net.minecraft.server.v1_9_R2.Navigation;
import net.minecraft.server.v1_9_R2.PathEntity;
import net.minecraft.server.v1_9_R2.PathfinderGoal;
import org.bukkit.Location;

public class PathfinderGoalWalkToLocation extends PathfinderGoal {
  private double speed;
  private EntityInsentient entity;
  private Location loc;

  private Navigation navigation;

  public PathfinderGoalWalkToLocation(EntityInsentient entity, Location loc, double speed) {
    this.entity = entity;
    this.loc = loc;
    this.navigation = (Navigation) this.entity.getNavigation();
    this.speed = speed;
  }

  @Override
  public boolean a() {
    return true;
  }

  @Override
  public void c() {
    PathEntity pathEntity = this.navigation.a(loc.getX(), loc.getY(), loc.getZ());
    this.navigation.a(pathEntity, speed);
  }
}

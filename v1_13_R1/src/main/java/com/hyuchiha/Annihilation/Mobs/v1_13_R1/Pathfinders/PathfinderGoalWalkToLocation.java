package com.hyuchiha.Annihilation.Mobs.v1_13_R1.Pathfinders;

import net.minecraft.server.v1_13_R1.EntityInsentient;
import net.minecraft.server.v1_13_R1.Navigation;
import net.minecraft.server.v1_13_R1.PathEntity;
import net.minecraft.server.v1_13_R1.PathfinderGoal;
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

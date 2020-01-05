package com.hyuchiha.Annihilation.Mobs.v1_12_R1.Pathfinders;

import net.minecraft.server.v1_12_R1.*;

public class PathfinderGoalWalkToLocation extends PathfinderGoal {

  // NMS Entity
  private EntityCreature b;

  // speed
  protected double a;

  // random PosX
  private double c;

  // random PosY
  private double d;

  // random PosZ
  private double e;

  public PathfinderGoalWalkToLocation(EntityCreature entitycreature, double d0, double x, double y, double z) {
    this.b = entitycreature;
    this.a = d0;
    this.d = y;
    this.c = x;
    this.e = z;
  }

  @Override
  public boolean a() {
    Vec3D vec3d = RandomPositionGenerator.a(this.b, 5, 4);
    if (vec3d == null) return false;
    return true;
  }

  @Override
  public void c() {
    Vec3D vec3d = RandomPositionGenerator.a(this.b, 5, 4);
    if (vec3d == null) return; // IN AIR
    this.b.getNavigation().a(c, d, e, 2);
  }

  @Override
  public boolean b() {
    if ((this.b.ticksLived - this.b.hurtTimestamp) > 100) {
      this.b.b((EntityLiving) null);
      return false;
    }
    return !this.b.getNavigation().o();
  }

}
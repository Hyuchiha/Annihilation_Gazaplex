package com.hyuchiha.Annihilation.Mobs.Abilities;

import com.hyuchiha.Annihilation.Mobs.CustomMob;
import com.hyuchiha.Annihilation.Mobs.MobAbility;
import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.util.Vector;

/**
 * Launches a small fireball projectile aimed at the target.
 *
 * <p>The fireball uses the vanilla {@link SmallFireball} entity so all collision/explosion
 * behavior is handled by the server. Caller controls the cooldown and minimum range to
 * keep the mob from spamming projectiles at melee distance.
 */
public class FireballAbility extends MobAbility {
  private final double minRange;
  private final double maxRange;
  private final float yield;
  private final boolean incendiary;

  /**
   * @param cooldownTicks ticks between shots
   * @param minRange      do not fire if target is closer than this (melee-cleanup)
   * @param maxRange      do not fire if target is farther than this
   * @param yield         explosion radius (0 = no explosion, 1.0 = ghast small fireball)
   * @param incendiary    whether the impact ignites blocks
   */
  public FireballAbility(long cooldownTicks, double minRange, double maxRange,
                          float yield, boolean incendiary) {
    super(cooldownTicks);
    this.minRange = minRange;
    this.maxRange = maxRange;
    this.yield = yield;
    this.incendiary = incendiary;
  }

  @Override
  protected boolean canFire(CustomMob mob, Player target) {
    double dSq = target.getLocation().distanceSquared(mob.getEntity().getLocation());
    return dSq >= minRange * minRange && dSq <= maxRange * maxRange;
  }

  @Override
  protected void execute(CustomMob mob, Player target) {
    LivingEntity self = mob.getEntity();
    Location origin = self.getEyeLocation();
    Vector direction = target.getEyeLocation().toVector().subtract(origin.toVector()).normalize();

    Fireball fb = self.getWorld().spawn(origin.add(direction), SmallFireball.class);
    fb.setShooter(self);
    fb.setDirection(direction);
    fb.setYield(yield);
    fb.setIsIncendiary(incendiary);
  }
}

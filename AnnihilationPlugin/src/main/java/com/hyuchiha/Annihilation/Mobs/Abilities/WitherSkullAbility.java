package com.hyuchiha.Annihilation.Mobs.Abilities;

import com.hyuchiha.Annihilation.Mobs.CustomMob;
import com.hyuchiha.Annihilation.Mobs.MobAbility;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.util.Vector;

/**
 * Single Wither skull projectile aimed at the target — the Wither's signature attack.
 *
 * <p>Uses the vanilla {@link WitherSkull} entity so explosion + wither effect are handled
 * by the server. Set {@code charged} to true for the blue ("charged") variant that
 * shatters obstacles.
 */
public class WitherSkullAbility extends MobAbility {
  private final double minRange;
  private final double maxRange;
  private final boolean charged;

  /**
   * @param cooldownTicks ticks between shots
   * @param minRange      do not fire if target is closer than this (melee cleanup)
   * @param maxRange      do not fire if target is farther than this
   * @param charged       blue charged skull (true) vs normal black skull (false)
   */
  public WitherSkullAbility(long cooldownTicks, double minRange, double maxRange, boolean charged) {
    super(cooldownTicks);
    this.minRange = minRange;
    this.maxRange = maxRange;
    this.charged = charged;
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

    WitherSkull skull = self.getWorld().spawn(origin.add(direction), WitherSkull.class);
    skull.setShooter(self);
    skull.setDirection(direction);
    skull.setCharged(charged);
  }
}

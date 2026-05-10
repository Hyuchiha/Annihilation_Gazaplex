package com.hyuchiha.Annihilation.Mobs.Abilities;

import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Mobs.CustomMob;
import com.hyuchiha.Annihilation.Mobs.MobAbility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Multi-shot sonic-boom beam — the Warden's signature attack, generalized so any mob can
 * use it. Each ability "fire" produces {@code beamCount} sequential beam casts, each
 * re-targeting the nearest valid player at cast time (so the Warden chases moving targets
 * within the burst).
 *
 * <p><b>Version note:</b> The {@link Particle#valueOf} static initializer probes for the
 * 1.19+ {@code SONIC_BOOM} particle and falls back to {@code EXPLOSION_LARGE} on older
 * servers. The ability itself works on any 1.18+ server (the wider CustomMob gate).
 *
 * <p>Each beam: raycasts from the mob's eye toward the target, damages every player it
 * intersects within {@code range}, draws particles along the line, plays a warden roar.
 */
public class SonicBeamAbility extends MobAbility {
  private static final Particle BEAM_PARTICLE;
  static {
    Particle resolved = null;
    for (String name : new String[]{"SONIC_BOOM", "EXPLOSION_LARGE", "EXPLOSION"}) {
      try { resolved = Particle.valueOf(name); break; }
      catch (IllegalArgumentException ignored) { /* try next */ }
    }
    BEAM_PARTICLE = resolved;
  }

  private final int beamCount;
  private final long delayBetweenBeams;
  private final double range;
  private final double damage;

  /**
   * @param cooldownTicks      ticks between barrages
   * @param beamCount          number of beam casts per fire
   * @param delayBetweenBeams  tick stagger between sequential casts within one burst
   * @param range              max distance the beam can travel and re-pick a target
   * @param damage             damage per player hit
   */
  public SonicBeamAbility(long cooldownTicks, int beamCount, long delayBetweenBeams,
                           double range, double damage) {
    super(cooldownTicks);
    this.beamCount = Math.max(1, beamCount);
    this.delayBetweenBeams = Math.max(1L, delayBetweenBeams);
    this.range = range;
    this.damage = damage;
  }

  @Override
  protected void execute(CustomMob mob, Player initialTarget) {
    for (int i = 0; i < beamCount; i++) {
      long delay = (long) i * delayBetweenBeams;
      Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> castBeam(mob), delay);
    }
  }

  private void castBeam(CustomMob mob) {
    if (!mob.isAlive()) return;
    LivingEntity self = mob.getEntity();
    Player target = pickNearestPlayer(self);
    if (target == null) return;

    Location origin = self.getEyeLocation();
    Vector direction = target.getLocation().add(0, 1.0, 0).toVector().subtract(origin.toVector());
    double distance = direction.length();
    if (distance > range) return;
    direction.normalize();

    self.getWorld().playSound(origin, Sound.ENTITY_WARDEN_SONIC_BOOM, 1.4f, 1.0f);

    // Particle trail along the beam.
    if (BEAM_PARTICLE != null) {
      double step = 0.5;
      for (double d = 0.0; d < distance; d += step) {
        Location p = origin.clone().add(direction.clone().multiply(d));
        self.getWorld().spawnParticle(BEAM_PARTICLE, p, 1, 0.0, 0.0, 0.0, 0.0);
      }
    }

    // Damage anything in a 1-block radius cylinder around the line.
    double rSq = 1.0 * 1.0;
    Location origin2 = origin.clone();
    for (Player nearby : self.getWorld().getPlayers()) {
      if (nearby.isDead() || !nearby.isValid()) continue;
      if (perpendicularDistanceSq(nearby.getLocation(), origin2, direction) <= rSq
          && nearby.getLocation().distance(origin2) <= distance + 1.0) {
        nearby.damage(damage, self);
      }
    }
  }

  private Player pickNearestPlayer(LivingEntity self) {
    Player best = null;
    double bestSq = range * range;
    for (Player p : self.getWorld().getPlayers()) {
      if (p.isDead() || !p.isValid()) continue;
      double dSq = p.getLocation().distanceSquared(self.getLocation());
      if (dSq < bestSq) {
        bestSq = dSq;
        best = p;
      }
    }
    return best;
  }

  /** Squared perpendicular distance from {@code point} to the ray (origin, dir-unit). */
  private static double perpendicularDistanceSq(Location point, Location origin, Vector dirUnit) {
    Vector toPoint = point.toVector().subtract(origin.toVector());
    double projection = toPoint.dot(dirUnit);
    Vector projected = dirUnit.clone().multiply(projection);
    Vector perp = toPoint.subtract(projected);
    return perp.lengthSquared();
  }
}

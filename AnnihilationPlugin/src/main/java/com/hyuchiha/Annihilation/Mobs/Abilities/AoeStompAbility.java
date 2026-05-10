package com.hyuchiha.Annihilation.Mobs.Abilities;

import com.hyuchiha.Annihilation.Mobs.CustomMob;
import com.hyuchiha.Annihilation.Mobs.MobAbility;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Area-of-effect stomp at the mob's location. Damages and knocks back every player inside
 * a horizontal radius, ignoring the mob itself.
 *
 * <p>Only fires when the primary target is at melee range — keeps it tactical instead of
 * spammy.
 */
public class AoeStompAbility extends MobAbility {
  private final double radius;
  private final double damage;
  private final double knockbackStrength;
  private final double meleeRange;

  /**
   * @param cooldownTicks      ticks between stomps
   * @param radius             horizontal blast radius
   * @param damage             damage per affected player
   * @param knockbackStrength  vertical+horizontal knockback impulse
   * @param meleeRange         only fire if the primary target is within this many blocks
   */
  public AoeStompAbility(long cooldownTicks, double radius, double damage,
                          double knockbackStrength, double meleeRange) {
    super(cooldownTicks);
    this.radius = radius;
    this.damage = damage;
    this.knockbackStrength = knockbackStrength;
    this.meleeRange = meleeRange;
  }

  @Override
  protected boolean canFire(CustomMob mob, Player target) {
    return target.getLocation().distanceSquared(mob.getEntity().getLocation())
        <= meleeRange * meleeRange;
  }

  @Override
  protected void execute(CustomMob mob, Player target) {
    LivingEntity self = mob.getEntity();
    Location center = self.getLocation();

    center.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, center, 1);
    center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.2f, 1.4f);

    double rSq = radius * radius;
    for (Entity nearby : self.getNearbyEntities(radius, radius, radius)) {
      if (!(nearby instanceof Player)) continue;
      Player p = (Player) nearby;
      if (p.isDead() || !p.isValid()) continue;
      if (p.getLocation().distanceSquared(center) > rSq) continue;

      p.damage(damage, self);
      Vector away = p.getLocation().toVector().subtract(center.toVector()).normalize();
      away.setY(0.6).multiply(knockbackStrength);
      p.setVelocity(p.getVelocity().add(away));
    }
  }
}

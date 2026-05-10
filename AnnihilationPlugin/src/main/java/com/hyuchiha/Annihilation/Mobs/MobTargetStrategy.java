package com.hyuchiha.Annihilation.Mobs;

import org.bukkit.entity.Player;

/**
 * Pluggable strategy for choosing which player a {@link CustomMob} should pursue.
 *
 * <p>The default strategy ({@link #NEAREST_PLAYER}) picks the closest online player within
 * a 32-block radius. Anni-specific mobs typically want a "nearest enemy of opposing team"
 * strategy — subclasses can return their own implementation from
 * {@link CustomMob#createTargetStrategy()}.
 */
@FunctionalInterface
public interface MobTargetStrategy {
  Player pickTarget(CustomMob mob);

  /** Closest player within 32 blocks, ignoring spectators / dead players. */
  MobTargetStrategy NEAREST_PLAYER = mob -> {
    Player nearest = null;
    double nearestSq = 32.0 * 32.0;
    for (Player p : mob.getEntity().getWorld().getPlayers()) {
      if (p.isDead() || !p.isValid()) continue;
      double dSq = p.getLocation().distanceSquared(mob.getEntity().getLocation());
      if (dSq < nearestSq) {
        nearestSq = dSq;
        nearest = p;
      }
    }
    return nearest;
  };
}

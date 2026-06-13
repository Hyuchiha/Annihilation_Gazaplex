package com.hyuchiha.Annihilation.Particles;

import com.cryptomorin.xseries.particles.ParticleDisplay;
import com.cryptomorin.xseries.particles.XParticle;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Low-level, reusable particle primitives built on top of XSeries
 * {@link ParticleDisplay}. This class is intentionally game-agnostic: it only
 * knows about geometry (circles, helixes, spheres, lines) and audience
 * resolution. Higher-level, named gameplay effects live in
 * {@link ParticleEffects}.
 *
 * <p><b>Performance note:</b> every shape method accepts a pre-built
 * {@link ParticleDisplay}. Configure {@link ParticleDisplay#onlyVisibleTo} on
 * it (e.g. with {@link #nearby(Location, double)}) so packets are sent only to
 * players who can actually see them. A world-wide broadcast to 80-100 players
 * for an effect nobody is standing near is the main avoidable cost.
 *
 * <p>Shapes use {@code withCount(0)} semantics: pass a display whose count is
 * 0 so each spawned point is a single, precisely-placed particle rather than a
 * random cloud.
 */
public final class ParticleLib {

  private ParticleLib() {
  }

  /**
   * Players within {@code radius} blocks of {@code center} in the same world.
   * Use the result with {@link ParticleDisplay#onlyVisibleTo} to cull packets
   * for distant players. O(online-players); cheap enough for per-second calls.
   */
  public static List<Player> nearby(Location center, double radius) {
    List<Player> out = new ArrayList<>();
    if (center == null || center.getWorld() == null) {
      return out;
    }
    double r2 = radius * radius;
    for (Player p : center.getWorld().getPlayers()) {
      if (p.getLocation().distanceSquared(center) <= r2) {
        out.add(p);
      }
    }
    return out;
  }

  /**
   * A flat (XZ-plane) ring of {@code points} particles around {@code center}.
   */
  public static void circle(ParticleDisplay display, Location center, double radius, int points) {
    if (display == null || center == null || points <= 0) {
      return;
    }
    double inc = (2 * Math.PI) / points;
    for (int i = 0; i < points; i++) {
      double angle = i * inc;
      double x = radius * Math.cos(angle);
      double z = radius * Math.sin(angle);
      display.spawn(center.clone().add(x, 0, z));
    }
  }

  /**
   * Like {@link #circle} but only draws the first {@code drawn} of {@code points}
   * segments — used to animate a circle "forming" over time. {@code drawn} is
   * clamped to {@code [0, points]}.
   */
  public static void arc(ParticleDisplay display, Location center, double radius, int points, int drawn) {
    if (display == null || center == null || points <= 0) {
      return;
    }
    if (drawn > points) {
      drawn = points;
    }
    double inc = (2 * Math.PI) / points;
    for (int i = 0; i < drawn; i++) {
      double angle = i * inc;
      double x = radius * Math.cos(angle);
      double z = radius * Math.sin(angle);
      display.spawn(center.clone().add(x, 0, z));
    }
  }

  /**
   * A vertical helix rising from {@code base}. {@code turns} full revolutions
   * over {@code height} blocks, sampled at {@code pointsPerTurn}.
   */
  public static void helix(ParticleDisplay display, Location base, double radius,
                           double height, int pointsPerTurn, double turns) {
    if (display == null || base == null || pointsPerTurn <= 0 || turns <= 0) {
      return;
    }
    int total = (int) Math.round(pointsPerTurn * turns);
    for (int i = 0; i < total; i++) {
      double t = (double) i / total;
      double angle = turns * 2 * Math.PI * t;
      double x = radius * Math.cos(angle);
      double z = radius * Math.sin(angle);
      double y = height * t;
      display.spawn(base.clone().add(x, y, z));
    }
  }

  /**
   * A hollow sphere of approximately {@code points} particles around
   * {@code center}, distributed with a Fibonacci spiral for even spacing.
   */
  public static void sphere(ParticleDisplay display, Location center, double radius, int points) {
    if (display == null || center == null || points <= 0) {
      return;
    }
    double goldenAngle = Math.PI * (3 - Math.sqrt(5));
    for (int i = 0; i < points; i++) {
      double y = 1 - (i / (double) (points - 1)) * 2; // 1 .. -1
      double r = Math.sqrt(1 - y * y);
      double theta = goldenAngle * i;
      double x = Math.cos(theta) * r;
      double z = Math.sin(theta) * r;
      display.spawn(center.clone().add(x * radius, y * radius, z * radius));
    }
  }

  /**
   * A straight line of {@code points} particles from {@code from} to {@code to}.
   * Both locations must share a world.
   */
  public static void line(ParticleDisplay display, Location from, Location to, int points) {
    if (display == null || from == null || to == null || points <= 0
        || from.getWorld() != to.getWorld()) {
      return;
    }
    double dx = (to.getX() - from.getX()) / points;
    double dy = (to.getY() - from.getY()) / points;
    double dz = (to.getZ() - from.getZ()) / points;
    for (int i = 0; i <= points; i++) {
      display.spawn(from.clone().add(dx * i, dy * i, dz * i));
    }
  }

  /**
   * Convenience: a single-particle display of {@code particle} that respects
   * client particle settings, visible only to {@code audience}. Count 0 so it
   * lands exactly where spawned.
   */
  public static ParticleDisplay point(XParticle particle, List<Player> audience) {
    ParticleDisplay display = ParticleDisplay.of(particle).withCount(0).offset(0, 0, 0);
    if (audience != null) {
      display.onlyVisibleTo(audience);
    }
    return display;
  }
}

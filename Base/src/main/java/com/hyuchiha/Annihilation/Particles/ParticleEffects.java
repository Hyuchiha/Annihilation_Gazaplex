package com.hyuchiha.Annihilation.Particles;

import com.cryptomorin.xseries.particles.ParticleDisplay;
import com.cryptomorin.xseries.particles.XParticle;
import com.cryptomorin.xseries.reflection.XReflection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Named, reusable gameplay particle effects built on {@link ParticleLib}.
 * Every effect culls packets to nearby players so it scales to a full
 * 80-100 player server. Game-specific orchestration (e.g. which nexus, at what
 * health threshold) stays in the plugin; this class only knows how to draw a
 * given effect at a given location.
 */
public final class ParticleEffects {

  /** Default radius (blocks) within which a location-based effect is shown. */
  public static final double DEFAULT_VIEW_RADIUS = 28.0;

  private ParticleEffects() {
  }

  // ---------------------------------------------------------------------------
  // Player-attached effects
  // ---------------------------------------------------------------------------

  /**
   * A small aura puff just behind the player's back (e.g. a kit cosmetic or a
   * "charging" tell). Computed from the player's facing direction, raised to
   * torso height. Visible to players near the target, including the target.
   */
  public static void playerBackAura(Player player, XParticle particle, int count) {
    if (player == null) {
      return;
    }
    Location loc = player.getLocation();
    Vector back = loc.getDirection().setY(0).normalize().multiply(-0.45);
    Location behind = loc.add(0, 1.1, 0).add(back);
    List<Player> audience = ParticleLib.nearby(behind, DEFAULT_VIEW_RADIUS);
    ParticleDisplay.of(particle)
        .withLocation(behind)
        .withCount(count)
        .offset(0.18, 0.28, 0.18)
        .withExtra(0)
        .onlyVisibleTo(audience)
        .spawn();
  }

  // ---------------------------------------------------------------------------
  // Block-attached effects
  // ---------------------------------------------------------------------------

  /**
   * A gentle ring of particles hovering above a block (a marker / objective
   * highlight). Drawn once; call on a timer for a persistent halo.
   */
  public static void blockHalo(Location blockLoc, XParticle particle, double radius, int points) {
    if (blockLoc == null) {
      return;
    }
    Location center = blockLoc.clone().add(0.5, 1.1, 0.5);
    List<Player> audience = ParticleLib.nearby(center, DEFAULT_VIEW_RADIUS);
    ParticleLib.circle(ParticleLib.point(particle, audience), center, radius, points);
  }

  /**
   * A burst of block-break particles (the cracked-texture shards you see when
   * mining), tinted to {@code material}. Use to make a block look like it is
   * crumbling or taking a hit.
   *
   * <p>Cross-version: {@link Material#createBlockData()} only exists on 1.13+,
   * so pre-1.13 servers fall back to the legacy {@link MaterialData} overload.
   * The 1.13+ branch is never linked on older servers because the version
   * guard short-circuits first.
   */
  public static void blockBreakBurst(Location blockLoc, Material material, int count) {
    if (blockLoc == null || material == null) {
      return;
    }
    Location center = blockLoc.clone().add(0.5, 0.5, 0.5);
    List<Player> audience = ParticleLib.nearby(center, DEFAULT_VIEW_RADIUS);
    ParticleDisplay display = ParticleDisplay.of(XParticle.BLOCK)
        .withLocation(center)
        .withCount(count)
        .offset(0.4, 0.4, 0.4)
        .withExtra(0)
        .onlyVisibleTo(audience);
    applyBlockData(display, material);
    display.spawn();
  }

  private static void applyBlockData(ParticleDisplay display, Material material) {
    if (XReflection.supports(13)) {
      display.withBlock(material.createBlockData());
    } else {
      display.withBlock(new MaterialData(material));
    }
  }

  // ---------------------------------------------------------------------------
  // Teleport effect
  // ---------------------------------------------------------------------------

  /**
   * Animated teleport channel: a circle visibly "forms" around the player over
   * {@code durationTicks}, a column rises with it, then the player is teleported
   * to {@code dest} and a portal burst fires at the destination.
   *
   * <p>Self-cancelling: aborts cleanly if the player logs off mid-channel.
   * {@code onComplete} (nullable) runs immediately after the teleport.
   *
   * @param plugin        owning plugin, for the scheduler
   * @param durationTicks how long the circle takes to fully form (20 = 1s)
   */
  public static void teleportChannel(Plugin plugin, Player player, Location dest,
                                     int durationTicks, Runnable onComplete) {
    if (plugin == null || player == null || dest == null || durationTicks <= 0) {
      return;
    }
    final int segments = 36;
    final double radius = 1.2;

    new BukkitRunnable() {
      int tick = 0;

      @Override
      public void run() {
        if (!player.isOnline()) {
          cancel();
          return;
        }
        double progress = Math.min(1.0, (double) tick / durationTicks);
        Location base = player.getLocation();
        List<Player> audience = ParticleLib.nearby(base, ParticleEffects.DEFAULT_VIEW_RADIUS);

        // Circle forms progressively from 0 to a full ring.
        int drawn = (int) Math.ceil(segments * progress);
        ParticleLib.arc(ParticleLib.point(XParticle.PORTAL, audience),
            base.clone().add(0, 0.1, 0), radius, segments, drawn);

        // A single end-rod particle rises with the channel for a "beam" tell.
        ParticleDisplay.of(XParticle.END_ROD)
            .withLocation(base.clone().add(0, 0.2 + progress * 2.0, 0))
            .withCount(1)
            .offset(0.08, 0.1, 0.08)
            .withExtra(0)
            .onlyVisibleTo(audience)
            .spawn();

        if (tick >= durationTicks) {
          cancel();
          player.teleport(dest);

          List<Player> destAudience = ParticleLib.nearby(dest, ParticleEffects.DEFAULT_VIEW_RADIUS);
          ParticleLib.circle(ParticleLib.point(XParticle.END_ROD, destAudience),
              dest.clone().add(0, 0.2, 0), radius, segments);
          ParticleDisplay.of(XParticle.PORTAL)
              .withLocation(dest.clone().add(0, 1.0, 0))
              .withCount(40)
              .offset(0.4, 0.8, 0.4)
              .withExtra(0)
              .onlyVisibleTo(destAudience)
              .spawn();

          if (onComplete != null) {
            onComplete.run();
          }
          return;
        }
        tick++;
      }
    }.runTaskTimer(plugin, 0L, 1L);
  }
}

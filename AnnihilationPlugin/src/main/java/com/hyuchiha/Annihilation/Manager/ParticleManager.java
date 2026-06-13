package com.hyuchiha.Annihilation.Manager;

import com.cryptomorin.xseries.particles.ParticleDisplay;
import com.cryptomorin.xseries.particles.XParticle;
import com.hyuchiha.Annihilation.Arena.Nexus;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Particles.ParticleEffects;
import com.hyuchiha.Annihilation.Particles.ParticleLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates the nexus particle visuals on top of the shared
 * {@link ParticleEffects} / {@link ParticleLib} library (in the Base module).
 *
 * <p><b>Performance:</b> every effect here is culled to players within
 * {@link ParticleEffects#DEFAULT_VIEW_RADIUS} of the nexus via
 * {@code onlyVisibleTo}, instead of broadcasting to the whole world. Counts are
 * kept modest so that many players hammering nexuses at once on an 80-100
 * player server does not flood clients with particle packets.
 */
public class ParticleManager {
  private static Main plugin = Main.getInstance();
  private static List<BukkitTask> particles = new ArrayList<>();

  // Ambient counts per nexus, per tick (every second). Kept low on purpose.
  private static final int AMBIENT_COUNT = 8;
  // Per-hit burst counts. A nexus hit fires once per break; keep it cheap.
  private static final int HIT_LAVA_COUNT = 6;
  private static final int HIT_SMOKE_COUNT = 6;
  private static final int HIT_CRACK_COUNT = 8;
  // Crumble shards shown each second once a nexus drops below half health.
  private static final int CRUMBLE_COUNT = 12;

  public static void initGameParticles() {
    BukkitTask nexusParticles = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
      for (GameTeam team : GameTeam.teams()) {
        if (!team.isTeamAlive()) {
          continue;
        }
        Nexus nexus = team.getNexus();
        Location center = nexus.getLocation().clone().add(0.5D, 0.0D, 0.5D);
        List<Player> audience = ParticleLib.nearby(center, ParticleEffects.DEFAULT_VIEW_RADIUS);
        if (audience.isEmpty()) {
          continue; // Nobody nearby: skip the packets entirely.
        }

        ParticleDisplay.of(XParticle.MYCELIUM)
            .withLocation(center)
            .offset(0.9F, 0.9F, 0.9F)
            .withCount(AMBIENT_COUNT)
            .withExtra(0)
            .onlyVisibleTo(audience)
            .spawn();

        ParticleDisplay.of(XParticle.ENCHANT)
            .withLocation(center)
            .offset(0.9F, 0.9F, 0.9F)
            .withCount(AMBIENT_COUNT)
            .withExtra(0)
            .onlyVisibleTo(audience)
            .spawn();

        // Crumbling block shards once the nexus is below half health — it
        // visibly starts breaking apart as it nears destruction.
        if (nexus.isBelowHalf()) {
          ParticleEffects.blockBreakBurst(nexus.getLocation(), nexusMaterial(nexus), CRUMBLE_COUNT);
        }
      }
    }, 100L, 20L);

    particles.add(nexusParticles);
  }

  /**
   * Fired once per nexus hit. A punchy lava + smoke pop plus block-break shards
   * so a hit reads as "chipping the block", culled to nearby players.
   */
  public static void createNexusBreakParticle(Location nexus) {
    List<Player> audience = ParticleLib.nearby(nexus, ParticleEffects.DEFAULT_VIEW_RADIUS);
    if (audience.isEmpty()) {
      return;
    }

    ParticleDisplay.of(XParticle.LAVA)
        .withLocation(nexus)
        .offset(0.6F, 0.6F, 0.6F)
        .withCount(HIT_LAVA_COUNT)
        .withExtra(0)
        .onlyVisibleTo(audience)
        .spawn();

    ParticleDisplay.of(XParticle.LARGE_SMOKE)
        .withLocation(nexus)
        .offset(0.6F, 0.6F, 0.6F)
        .withCount(HIT_SMOKE_COUNT)
        .withExtra(0)
        .onlyVisibleTo(audience)
        .spawn();

    // Block-break shards reinforce the "breaking a block" feel on every hit.
    ParticleEffects.blockBreakBurst(nexus, Material.END_STONE, HIT_CRACK_COUNT);
  }

  public static void createParticleNexusDestroy(Location nexus) {
    // One-shot, dramatic. forceSpawn so it always renders; culled to a slightly
    // wider radius since a nexus death is a server-wide moment of interest.
    List<Player> audience = ParticleLib.nearby(nexus, ParticleEffects.DEFAULT_VIEW_RADIUS * 1.5);
    ParticleDisplay.of(XParticle.EXPLOSION)
        .withLocation(nexus)
        .offset(1F, 1F, 1F)
        .withCount(50)
        .withExtra(0)
        .forceSpawn(true)
        .onlyVisibleTo(audience)
        .spawn();
  }

  public static void endGameParticles() {
    for (BukkitTask task : particles) {
      task.cancel();
    }

    particles.clear();
  }

  /** The nexus block's current material, falling back to END_STONE. */
  private static Material nexusMaterial(Nexus nexus) {
    Material type = nexus.getBlock().getType();
    return (type == Material.AIR) ? Material.END_STONE : type;
  }
}

package com.hyuchiha.Annihilation.Manager;

import com.cryptomorin.xseries.particles.ParticleDisplay;
import com.cryptomorin.xseries.particles.XParticle;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager {
  private static Main plugin = Main.getInstance();
  private static List<BukkitTask> particles = new ArrayList<>();

  public static void initGameParticles() {
    BukkitTask nexusParticles = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
      for (GameTeam team : GameTeam.teams()) {
        if (team.isTeamAlive()) {
          Location teamNexus = team.getNexus().getLocation().clone();
          teamNexus.add(0.5D, 0.0D, 0.5D);
          ParticleDisplay.of(XParticle.MYCELIUM)
                  .withLocation(teamNexus)
                  .offset(1F, 1F, 1F)
                  .withCount(20)
                  .withExtra(1)
                  .forceSpawn(true)
                  .spawn();

          ParticleDisplay.of(XParticle.ENCHANT)
                  .withLocation(teamNexus)
                  .offset(1F, 1F, 1F)
                  .withCount(20)
                  .withExtra(1)
                  .spawn();

          //ParticleEffect.TOWN_AURA.display(teamNexus, 1F, 1F, 1F, 0, 20, null, Bukkit.getOnlinePlayers());
          //ParticleEffect.ENCHANTMENT_TABLE.display(teamNexus, 1F, 1F, 1F, 0, 20, null, Bukkit.getOnlinePlayers());
        }
      }
    }, 100L, 20L);

    particles.add(nexusParticles);
  }

  public static void createNexusBreakParticle(Location nexus) {
    ParticleDisplay.of(XParticle.LAVA)
            .withLocation(nexus)
            .offset(1F, 1F, 1F)
            .withCount(20)
            .withExtra(0)
            .spawn();

    ParticleDisplay.of(XParticle.LARGE_SMOKE)
            .withLocation(nexus)
            .offset(1F, 1F, 1F)
            .withCount(20)
            .withExtra(0)
            .spawn();

    //ParticleEffect.LAVA.display(nexus, 1F, 1F, 1F, 1F, 20, null, Bukkit.getOnlinePlayers());
    //ParticleEffect.SMOKE_LARGE.display(nexus, 1F, 1F, 1F, 1F, 20, null, Bukkit.getOnlinePlayers());
  }

  public static void createParticleNexusDestroy(Location nexus) {
    ParticleDisplay.of(XParticle.EXPLOSION)
            .withLocation(nexus)
            .offset(1F, 1F, 1F)
            .withCount(50)
            .withExtra(0)
            .forceSpawn(true);

    //ParticleEffect.EXPLOSION_LARGE.display(nexus, 1F, 1F, 1F, 1F, 20, null, Bukkit.getOnlinePlayers());
  }

  public static void endGameParticles() {
    for (BukkitTask task : particles) {
      task.cancel();
    }

    particles.clear();
  }
}

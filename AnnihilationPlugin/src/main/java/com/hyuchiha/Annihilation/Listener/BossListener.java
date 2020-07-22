package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Chat.ChatUtil;
import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.BossManager;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Utils.FireworkUtils;
import com.hyuchiha.Annihilation.Utils.ItemSelectorUtils;
import com.hyuchiha.Annihilation.Utils.Sound;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.reflection.minecraft.Minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BossListener implements Listener {

  private final Main plugin;
  private Configuration config;

  private final int respawnCount;
  private List<String> playersToTeleport = new ArrayList<>();

  public BossListener(Main instance) {
    this.plugin = instance;
    this.config = plugin.getConfig("config.yml");

    respawnCount = plugin.getConfig().getInt("bossRespawnDelay", 10);
  }

  @EventHandler
  public void onHit(EntityDamageEvent event) {
    if (event.getEntity() instanceof Wither) {
      final Wither g = (Wither) event.getEntity();
      if (g.getCustomName() == null) {
        return;
      }

      if (!BossManager.hasBossConfig()) {
        return;
      }

      if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
        event.getEntity().remove();

        Bukkit.getScheduler().runTask(plugin, () -> BossManager.spawnBoss());
      }
    }
  }

  @EventHandler
  public void onBossPortal(EntityPortalEvent event) {
    if (event.getEntity() instanceof Wither) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onBreakBossMap(BlockBreakEvent event) {
    if (BossManager.hasBossConfig()) {
      World bossWorld = BossManager.getBossSpawnWorld();

      if (bossWorld != null && Objects.equals(event.getBlock().getWorld().getName(), bossWorld.getName())) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
          event.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onBlockPlaceBossMap(BlockPlaceEvent event) {
    World bossWorld = BossManager.getBossSpawnWorld();
    if (bossWorld != null && Objects.equals(event.getBlock().getWorld().getName(), bossWorld.getName())) {
      if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onBucketUse(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    World bossWorld = BossManager.getBossSpawnWorld();

    if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      ItemStack material = player.getInventory().getItemInMainHand();
      if (material.getType() == Material.WATER_BUCKET) {

        if (BossManager.hasBossConfig() && bossWorld != null && bossWorld == player.getWorld()) {
          event.setCancelled(true);
          return;
        }
      }

      if (material.getType() == Material.LAVA_BUCKET) {
        if (BossManager.hasBossConfig() && bossWorld != null && bossWorld == player.getWorld()) {
          event.setCancelled(true);
          return;
        }
      }
    }
  }

  @EventHandler
  public void onHit(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof Wither) {
      if (!(event.getDamager() instanceof Player) && !(event.getDamager() instanceof Arrow)) {
        event.setCancelled(true);
      }

      final Wither g = (Wither) event.getEntity();
      if (g.getCustomName() == null) {
        return;
      }

      if (!BossManager.hasBossConfig()) {
        return;
      }

      Bukkit.getScheduler().runTask(plugin, new Runnable() {
        @Override
        public void run() {
          BossManager.update(g);
        }
      });
    }
  }

  @EventHandler
  public void onPlayerMoveBossSpawn(final PlayerMoveEvent event) {

    if (GameManager.hasCurrentGame() && GameManager.getCurrentGame().getPhase() < 3) {
      return;
    }

    if (playersToTeleport.contains(event.getPlayer().getName())) {
      return;
    }

    final Location playerLoc = event.getPlayer().getLocation();
    List<Location> locations = BossManager.getTeleportLocations();

    for (final Location location : locations) {
      if (closeToTeleport(location, playerLoc)) {
        event.getPlayer().sendMessage(Translator.getColoredString("BOSS_BEGIN_TELEPORT"));
        playersToTeleport.add(event.getPlayer().getName());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
          if (GameManager.hasCurrentGame() && GameManager.getCurrentGame().getTimer().isRunning()) {
            if (closeToTeleport(location, playerLoc)) {
              GamePlayer meta = PlayerManager.getGamePlayer(event.getPlayer());
              Location spawnLocation = BossManager.getTeamSpawn(meta.getTeam());
              event.getPlayer().teleport(spawnLocation);
            } else {
              event.getPlayer().sendMessage(Translator.getColoredString("BOSS_TELEPORT_ERROR"));
            }
          }
          playersToTeleport.remove(event.getPlayer().getName());
        }, 20L * 5);
        break;
      }
    }
  }

  private boolean closeToTeleport(Location loc, Location playerLocation) {
    double x = playerLocation.getX();
    double y = playerLocation.getY();
    double z = playerLocation.getZ();

    double nX = loc.getX();
    double nZ = loc.getZ();
    double nY = loc.getY();
    return Math.abs(nX - x) <= 3
        && Math.abs(nZ - z) <= 3
        && Math.abs(nY - y) <= 3;

  }

  @EventHandler
  public void onDeath(EntityDeathEvent event) {
    if (event.getEntity() instanceof Wither) {
      Wither wither = (Wither) event.getEntity();
      if (wither.getCustomName() == null) {
        return;
      }

      if (!BossManager.hasBossConfig()) {
        return;
      }

      // One thing that can be good is add some potion effect to the team who kills the boss

      event.getDrops().clear();

      // TODO add particles
      // ParticleManager.createParticlesBossKill(event.getEntity().getLocation());

      BossManager.spawnLootChest();

      if (wither.getKiller() != null) {
        Player killer = wither.getKiller();
        GamePlayer gPlayer = PlayerManager.getGamePlayer(killer);
        ChatUtil.bossDeath(BossManager.getBoss(), killer, gPlayer.getTeam());

        BossManager.beginRespawnTime(respawnCount);

        Account data = plugin.getMainDatabase().getAccount(killer.getUniqueId().toString(), killer.getName());
        // data.increaseKills(); TODO Add stats for boss kill

        data.addMoney(config.getDouble("Money-boss-kill"));
        gPlayer.addXp(config.getInt("Exp-boss-kill"));

        Location bossLocation = event.getEntity().getLocation();
        bossLocation.getWorld().playSound(bossLocation, Sound.ENDERDRAGON_DEATH.bukkitSound(), 1.0F, 0.1F);

        FireworkUtils.spawnFirework(
            bossLocation,
            gPlayer.getTeam().getColor(),
            gPlayer.getTeam().getColor()
        );

        ItemSelectorUtils.getBossStarSelector(killer.getName());
      }
    }
  }

  @EventHandler
  public void onExplosionWither(EntityChangeBlockEvent event) {
    EntityType type = event.getEntity().getType();
    if (type == EntityType.WITHER) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void WitherProjectile(EntityExplodeEvent event) {
    Entity entity = event.getEntity();
    if ((entity instanceof WitherSkull)) {
      event.setCancelled(true);
    }
  }

  @EventHandler()
  private void onChunkUnload(ChunkUnloadEvent event) {
    World chunkWorld = event.getWorld();

    if (BossManager.hasBossConfig()) {
      World bossWorld = BossManager.getBoss().getBossSpawn().getWorld();

      if (bossWorld.getName().equals(chunkWorld.getName()) && Minecraft.Version.getVersion().olderThan(Minecraft.Version.v1_13_R1)) {
        // TODO uncomment in future
        // event.setCancelled(true);
      }
    }

  }


}

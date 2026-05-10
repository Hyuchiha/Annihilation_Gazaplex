package com.hyuchiha.Annihilation.Mobs;

import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.reflection.minecraft.Minecraft;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Registry and global ticker for {@link CustomMob} instances.
 *
 * <p><b>Version gate:</b> disabled silently on MC &lt; 1.18. Older NMS coverage in this
 * plugin is partial (see PENDING FEATURE-6) and the modern attribute / PDC APIs used by
 * the framework are stable from 1.18 onwards. All public methods become no-ops on older
 * servers; callers do not need to version-check themselves.
 *
 * <p><b>Performance:</b> a single global {@link BukkitRunnable} fires every
 * {@link #TICK_INTERVAL} ticks (default: 10 ticks = 0.5s) and iterates the registry.
 * For 80–100 players with a handful of custom mobs at most, this is well below 1ms/tick.
 *
 * <p><b>Lifecycle:</b> entries are removed automatically when the backing entity dies or
 * becomes invalid. {@link #removeAll()} is the canonical way to wipe all mobs on game end.
 */
public class CustomMobManager {
  /** Tick frequency for the global ticker. 10 = twice per second. */
  public static final long TICK_INTERVAL = 10L;

  private static final ConcurrentHashMap<UUID, CustomMob> mobs = new ConcurrentHashMap<>();
  private static NamespacedKey typeKey;
  private static boolean enabled = false;
  private static BukkitRunnable tickTask;

  public static void init(Main plugin) {
    if (Minecraft.Version.getVersion().olderThan(Minecraft.Version.v1_18_R1)) {
      Output.log("CustomMobManager: disabled (requires MC >= 1.18).");
      return;
    }
    typeKey = new NamespacedKey(plugin, "mob_type");
    enabled = true;
    startTicker(plugin);
    Output.log("CustomMobManager: enabled.");
  }

  public static void shutdown() {
    if (tickTask != null) {
      tickTask.cancel();
      tickTask = null;
    }
    removeAll();
    enabled = false;
  }

  public static boolean isEnabled() {
    return enabled;
  }

  public static NamespacedKey typeKey() {
    return typeKey;
  }

  /**
   * Wrap a freshly spawned entity into a {@link CustomMob} and register it.
   *
   * @param baseEntity entity already spawned in the world via Bukkit
   * @param factory    constructor reference of the concrete CustomMob subclass,
   *                   e.g. {@code NexusGuardian::new}
   * @return the registered {@link CustomMob}, or {@code null} if the manager is disabled
   *         or the factory returned null
   */
  public static CustomMob spawn(LivingEntity baseEntity, Function<LivingEntity, CustomMob> factory) {
    if (!enabled || baseEntity == null) {
      return null;
    }
    CustomMob mob = factory.apply(baseEntity);
    if (mob != null) {
      mobs.put(baseEntity.getUniqueId(), mob);
    }
    return mob;
  }

  /** Returns the wrapper for {@code uuid}, or {@code null} if the entity is not a custom mob. */
  public static CustomMob get(UUID uuid) {
    return mobs.get(uuid);
  }

  /**
   * Returns the PDC type tag of an entity, or {@code null} if it isn't tagged.
   * Useful for identifying our mobs across server restarts (the PDC persists with the entity).
   */
  public static String getStoredTypeId(LivingEntity entity) {
    if (!enabled || entity == null) {
      return null;
    }
    PersistentDataContainer pdc = entity.getPersistentDataContainer();
    return pdc.has(typeKey, PersistentDataType.STRING)
        ? pdc.get(typeKey, PersistentDataType.STRING)
        : null;
  }

  /** Remove a mob from the registry. Does NOT kill the underlying entity. */
  public static void unregister(UUID uuid) {
    CustomMob mob = mobs.remove(uuid);
    if (mob != null) {
      mob.markRemoved();
    }
  }

  /** Kill and unregister every active mob. Call on game end / plugin disable. */
  public static void removeAll() {
    for (CustomMob mob : mobs.values()) {
      LivingEntity e = mob.getEntity();
      if (e != null && !e.isDead()) {
        e.remove();
      }
      mob.markRemoved();
    }
    mobs.clear();
  }

  public static int activeCount() {
    return mobs.size();
  }

  private static void startTicker(Main plugin) {
    tickTask = new BukkitRunnable() {
      @Override
      public void run() {
        if (mobs.isEmpty()) {
          return;
        }
        mobs.values().removeIf(mob -> {
          if (!mob.isAlive()) {
            return true;
          }
          try {
            mob.tick();
          } catch (Throwable t) {
            Output.logError("CustomMob tick failed for " + mob.getTypeId() + ": " + t.getMessage());
          }
          return false;
        });
      }
    };
    tickTask.runTaskTimer(plugin, TICK_INTERVAL, TICK_INTERVAL);
  }
}

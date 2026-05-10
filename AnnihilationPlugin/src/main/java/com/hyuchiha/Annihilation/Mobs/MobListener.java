package com.hyuchiha.Annihilation.Mobs;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Bridges Bukkit entity events to the matching {@link CustomMob} wrapper.
 *
 * <p>Routing is by entity UUID (cheap O(1) HashMap lookup) — non-custom entities are
 * ignored with zero overhead beyond the map miss.
 */
public class MobListener implements Listener {

  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onDamage(EntityDamageEvent event) {
    CustomMob mob = CustomMobManager.get(event.getEntity().getUniqueId());
    if (mob == null) {
      return;
    }
    if (mob.onDamage(event)) {
      event.setCancelled(true);
    }
  }

  /** Routes both directions: mob damaged AND mob damaging another entity. */
  @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
  public void onDamageByEntity(EntityDamageByEntityEvent event) {
    CustomMob attackerMob = CustomMobManager.get(event.getDamager().getUniqueId());
    if (attackerMob != null) {
      attackerMob.onAttack(event);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onDeath(EntityDeathEvent event) {
    CustomMob mob = CustomMobManager.get(event.getEntity().getUniqueId());
    if (mob == null) {
      return;
    }
    mob.onDeath(event);
    CustomMobManager.unregister(event.getEntity().getUniqueId());
  }
}

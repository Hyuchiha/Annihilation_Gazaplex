package com.hyuchiha.Annihilation.Mobs;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * Base class for custom-behavior mobs.
 *
 * <p>Design philosophy: pure Bukkit API, no NMS. Cross-version compatible from 1.18
 * onwards (the {@link CustomMobManager} version-gates this — older servers see no-ops).
 *
 * <p>Lifecycle:
 * <ol>
 *   <li>Caller spawns a base entity in the world via Bukkit
 *       ({@code world.spawnEntity(loc, EntityType.ZOMBIE)}).</li>
 *   <li>Caller wraps it via {@code CustomMobManager.spawn(entity, EntityType::new)}.</li>
 *   <li>{@link #configure()} runs once: attributes, equipment, name, PDC tag.</li>
 *   <li>{@link CustomMobManager} ticks every {@link CustomMobManager#TICK_INTERVAL} ticks
 *       and invokes {@link #tick()} on each live mob.</li>
 *   <li>{@link #onDamage(EntityDamageEvent)} and {@link #onDeath(EntityDeathEvent)} are
 *       routed from {@link MobListener} based on the PDC tag.</li>
 * </ol>
 *
 * <p>Subclasses define behavior by overriding:
 * <ul>
 *   <li>{@link #getTypeId()} — stable PDC identifier.</li>
 *   <li>{@link #configure()} — attributes / equipment / display name.</li>
 *   <li>{@link #createAbilities()} — list of special abilities.</li>
 *   <li>{@link #createTargetStrategy()} — optional, defaults to nearest player.</li>
 * </ul>
 */
public abstract class CustomMob {
  protected final LivingEntity entity;
  protected final List<MobAbility> abilities;
  protected final MobTargetStrategy targetStrategy;
  private boolean removed = false;

  protected CustomMob(LivingEntity entity) {
    this.entity = entity;
    this.targetStrategy = createTargetStrategy();
    this.abilities = createAbilities();

    entity.getPersistentDataContainer().set(
        CustomMobManager.typeKey(), PersistentDataType.STRING, getTypeId());
    entity.setRemoveWhenFarAway(false);

    configure();
  }

  /** Stable identifier persisted to the entity's PDC. Must be unique per mob type. */
  public abstract String getTypeId();

  /** One-time setup: attributes, equipment, display name, etc. */
  protected abstract void configure();

  /**
   * Build the ability list. Called once at construction.
   *
   * <p>Return a {@code new ArrayList<>(Arrays.asList(...))} since each mob instance owns
   * its own ability instances (cooldowns are mob-instance-scoped).
   */
  protected abstract List<MobAbility> createAbilities();

  /** Optional override; defaults to {@link MobTargetStrategy#NEAREST_PLAYER}. */
  protected MobTargetStrategy createTargetStrategy() {
    return MobTargetStrategy.NEAREST_PLAYER;
  }

  /**
   * Per-tick logic. Picks a target, forces the entity to focus on it, and walks the
   * abilities list. Manager removes the mob if {@link #isAlive()} returns false.
   */
  public void tick() {
    if (!isAlive()) {
      return;
    }
    Player target = targetStrategy.pickTarget(this);
    if (target == null) {
      return;
    }
    if (entity instanceof Mob) {
      ((Mob) entity).setTarget(target);
    }
    for (MobAbility ability : abilities) {
      ability.tryFire(this, target);
    }
  }

  /**
   * Called from {@link MobListener} when this mob takes damage.
   * Return {@code true} to cancel the damage event. Default = let damage apply.
   */
  public boolean onDamage(EntityDamageEvent event) {
    return false;
  }

  /**
   * Called from {@link MobListener} when this mob attacks another entity.
   * Useful for adding on-hit effects (poison, knockback, etc).
   */
  public void onAttack(EntityDamageByEntityEvent event) {}

  /** Called from {@link MobListener} on death. Use for custom drops, broadcast, etc. */
  public void onDeath(EntityDeathEvent event) {}

  public LivingEntity getEntity() {
    return entity;
  }

  public List<MobAbility> getAbilities() {
    return abilities;
  }

  public boolean isAlive() {
    return !removed && entity != null && !entity.isDead() && entity.isValid();
  }

  void markRemoved() {
    this.removed = true;
  }
}

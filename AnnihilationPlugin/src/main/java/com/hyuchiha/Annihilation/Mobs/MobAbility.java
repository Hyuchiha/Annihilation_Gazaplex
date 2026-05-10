package com.hyuchiha.Annihilation.Mobs;

import org.bukkit.entity.Player;

/**
 * A reusable special ability that a {@link CustomMob} can trigger against a target.
 *
 * <p>Each {@code MobAbility} is mob-instance-scoped: every {@link CustomMob} owns its own
 * instances, so cooldowns are tracked per mob. Concrete abilities live in
 * {@code com.hyuchiha.Annihilation.Mobs.Abilities}.
 *
 * <p>Contract: subclasses implement {@link #execute(CustomMob, Player)} with the actual
 * effect. The framework calls {@link #tryFire(CustomMob, Player)} on every tick — that
 * checks the cooldown and any extra preconditions ({@link #canFire(CustomMob, Player)}).
 */
public abstract class MobAbility {
  private final long cooldownTicks;
  private long lastFiredTick = Long.MIN_VALUE;

  protected MobAbility(long cooldownTicks) {
    this.cooldownTicks = Math.max(0L, cooldownTicks);
  }

  /**
   * Tick-driven entry point. Returns {@code true} if the ability fired this tick.
   * Cooldown is automatically refreshed on a successful fire.
   */
  public final boolean tryFire(CustomMob mob, Player target) {
    long now = mob.getEntity().getTicksLived();
    if (now - lastFiredTick < cooldownTicks) {
      return false;
    }
    if (!canFire(mob, target)) {
      return false;
    }
    execute(mob, target);
    lastFiredTick = now;
    return true;
  }

  /** Override to add range/health/state preconditions. Default = always allowed. */
  protected boolean canFire(CustomMob mob, Player target) {
    return true;
  }

  /** The actual effect. Called when cooldown + {@link #canFire} both pass. */
  protected abstract void execute(CustomMob mob, Player target);

  public long getCooldownTicks() {
    return cooldownTicks;
  }
}

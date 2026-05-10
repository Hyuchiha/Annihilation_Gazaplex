package com.hyuchiha.Annihilation.Mobs.Abilities;

import com.hyuchiha.Annihilation.Mobs.CustomMob;
import com.hyuchiha.Annihilation.Mobs.MobAbility;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Time-limited burst of speed when the target is within range.
 *
 * <p>Implemented via vanilla potion effects (no NMS): applies SPEED to the mob for
 * {@code durationTicks} every cooldown. Visual: speed-particle trail. Movement
 * speed-up is handled by Mojang's pathfinding without any extra code.
 */
public class ChargeAbility extends MobAbility {
  private final int durationTicks;
  private final int amplifier;
  private final double triggerRange;

  /**
   * @param cooldownTicks ticks between charges
   * @param durationTicks how long each charge lasts (ticks)
   * @param amplifier     potion amplifier (0 = Speed I, 1 = Speed II, …)
   * @param triggerRange  only fire if the target is within this many blocks
   */
  public ChargeAbility(long cooldownTicks, int durationTicks, int amplifier, double triggerRange) {
    super(cooldownTicks);
    this.durationTicks = durationTicks;
    this.amplifier = amplifier;
    this.triggerRange = triggerRange;
  }

  @Override
  protected boolean canFire(CustomMob mob, Player target) {
    return target.getLocation().distanceSquared(mob.getEntity().getLocation())
        <= triggerRange * triggerRange;
  }

  @Override
  protected void execute(CustomMob mob, Player target) {
    mob.getEntity().addPotionEffect(
        new PotionEffect(PotionEffectType.SPEED, durationTicks, amplifier, true, true));
  }
}

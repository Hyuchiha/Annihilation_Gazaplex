package com.hyuchiha.Annihilation.Mobs.Abilities;

import com.hyuchiha.Annihilation.Mobs.CustomMob;
import com.hyuchiha.Annihilation.Mobs.MobAbility;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.util.Vector;

/**
 * Massive radial barrage of Wither skulls — a boss "panic" / "enrage" ability.
 *
 * <p>Spawns {@code skullCount} skulls in evenly-spaced horizontal directions around the
 * mob (a circle on the XZ plane). Tuning notes:
 * <ul>
 *   <li>{@code skullCount=8} → 45° between skulls, good "wall of skulls" density.</li>
 *   <li>{@code skullCount=16} → 22.5° between skulls, tighter wall but very chaotic.</li>
 *   <li>{@code charged=true} blue skulls shatter obstacles — terrain damage warning.</li>
 * </ul>
 *
 * <p>Long cooldowns recommended (10-20 seconds) to keep this as a panic mechanic, not
 * a default attack.
 */
public class WitherSkullBarrageAbility extends MobAbility {
  private final int skullCount;
  private final double speed;
  private final boolean charged;
  private final double triggerRange;

  /**
   * @param cooldownTicks ticks between barrages
   * @param skullCount    number of skulls to launch radially
   * @param speed         initial velocity magnitude per skull
   * @param charged       blue charged skulls (true) vs normal (false)
   * @param triggerRange  only fire if any player is within this radius
   */
  public WitherSkullBarrageAbility(long cooldownTicks, int skullCount, double speed,
                                    boolean charged, double triggerRange) {
    super(cooldownTicks);
    this.skullCount = Math.max(1, skullCount);
    this.speed = speed;
    this.charged = charged;
    this.triggerRange = triggerRange;
  }

  @Override
  protected boolean canFire(CustomMob mob, Player target) {
    return target.getLocation().distanceSquared(mob.getEntity().getLocation())
        <= triggerRange * triggerRange;
  }

  @Override
  protected void execute(CustomMob mob, Player target) {
    LivingEntity self = mob.getEntity();
    Location origin = self.getEyeLocation();
    origin.getWorld().playSound(origin, Sound.ENTITY_WITHER_SHOOT, 1.5f, 0.6f);

    double step = (Math.PI * 2.0) / skullCount;
    for (int i = 0; i < skullCount; i++) {
      double angle = step * i;
      Vector direction = new Vector(Math.cos(angle), 0.0, Math.sin(angle)).normalize();
      WitherSkull skull = self.getWorld().spawn(origin.clone().add(direction), WitherSkull.class);
      skull.setShooter(self);
      skull.setDirection(direction);
      skull.setVelocity(direction.clone().multiply(speed));
      skull.setCharged(charged);
    }
  }
}

package com.hyuchiha.Annihilation.Mobs.Abilities;

import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Mobs.CustomMob;
import com.hyuchiha.Annihilation.Mobs.MobAbility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Splash potion barrage — throws {@code count} splash potions in sequence with a small
 * stagger, each picked randomly from a configured effect pool.
 *
 * <p>The mob's typical Witch use case: high {@code count} + short {@code cooldownTicks}
 * for relentless potion pressure. Effects are picked uniformly from {@code effects} so
 * any mix works (offensive: HARM/POISON/WITHER, debuff: SLOW/WEAKNESS/BLINDNESS).
 */
public class PotionThrowAbility extends MobAbility {
  private static final long THROW_STAGGER_TICKS = 6L;

  private final int count;
  private final double range;
  private final int effectDurationTicks;
  private final int effectAmplifier;
  private final List<PotionEffectType> effects;

  /**
   * @param cooldownTicks       ticks between barrages
   * @param count               number of potions per barrage
   * @param range               only fire if target is within this radius
   * @param effectDurationTicks duration applied by each potion's effect
   * @param effectAmplifier     potion amplifier (0 = I, 1 = II, …)
   * @param effects             pool of effect types to pick from per throw
   */
  public PotionThrowAbility(long cooldownTicks, int count, double range,
                             int effectDurationTicks, int effectAmplifier,
                             List<PotionEffectType> effects) {
    super(cooldownTicks);
    this.count = Math.max(1, count);
    this.range = range;
    this.effectDurationTicks = effectDurationTicks;
    this.effectAmplifier = effectAmplifier;
    this.effects = effects;
  }

  @Override
  protected boolean canFire(CustomMob mob, Player target) {
    if (effects == null || effects.isEmpty()) return false;
    return target.getLocation().distanceSquared(mob.getEntity().getLocation())
        <= range * range;
  }

  @Override
  protected void execute(CustomMob mob, Player target) {
    for (int i = 0; i < count; i++) {
      long delay = (long) i * THROW_STAGGER_TICKS;
      Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> throwPotion(mob, target), delay);
    }
  }

  private void throwPotion(CustomMob mob, Player target) {
    if (!mob.isAlive() || target.isDead() || !target.isValid()) return;

    LivingEntity self = mob.getEntity();
    Location origin = self.getEyeLocation();

    PotionEffectType effect = effects.get(ThreadLocalRandom.current().nextInt(effects.size()));
    ItemStack potionItem = new ItemStack(Material.SPLASH_POTION);
    PotionMeta meta = (PotionMeta) potionItem.getItemMeta();
    if (meta != null) {
      meta.addCustomEffect(new PotionEffect(effect, effectDurationTicks, effectAmplifier), true);
      potionItem.setItemMeta(meta);
    }

    ThrownPotion potion = (ThrownPotion) self.getWorld().spawnEntity(origin, EntityType.SPLASH_POTION);
    potion.setShooter(self);
    potion.setItem(potionItem);

    Vector direction = target.getLocation().toVector().subtract(origin.toVector()).normalize();
    direction.setY(direction.getY() + 0.3); // arc the throw upward
    potion.setVelocity(direction.multiply(0.85));
  }
}

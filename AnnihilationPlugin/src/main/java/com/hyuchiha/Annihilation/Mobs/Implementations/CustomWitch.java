package com.hyuchiha.Annihilation.Mobs.Implementations;

import com.cryptomorin.xseries.XAttribute;
import com.hyuchiha.Annihilation.Mobs.Abilities.PotionThrowAbility;
import com.hyuchiha.Annihilation.Mobs.CustomMob;
import com.hyuchiha.Annihilation.Mobs.MobAbility;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tank witch: high HP, knockback-resistant, takes 40% less damage from any source, and
 * spams a 4-potion barrage every 3 seconds out of a hostile-effect pool.
 *
 * <p>Spawn the base entity as {@code EntityType.WITCH} so the vanilla witch model + sound
 * apply. Vanilla witches also throw their own potions on their AI — the {@code PotionThrowAbility}
 * here stacks on top of that for the "many potions" feel.
 *
 * <pre>{@code
 * LivingEntity w = (LivingEntity) world.spawnEntity(loc, EntityType.WITCH);
 * CustomMobManager.spawn(w, CustomWitch::new);
 * }</pre>
 */
public class CustomWitch extends CustomMob {
  private static final double DAMAGE_REDUCTION = 0.40D;

  public CustomWitch(LivingEntity entity) {
    super(entity);
  }

  @Override
  public String getTypeId() {
    return "annihilation_witch";
  }

  /** Override to inject config-driven HP. */
  protected double getMaxHealth() { return 80.0D; }

  /** Override to inject config-driven display name. */
  protected String getDisplayName() {
    return ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Coven Witch";
  }

  /** Override to tune the damage reduction multiplier (0.0 = none, 1.0 = invulnerable). */
  protected double getDamageReduction() { return DAMAGE_REDUCTION; }

  @Override
  protected void configure() {
    entity.setCustomName(getDisplayName());
    entity.setCustomNameVisible(true);

    double hp = getMaxHealth();
    setAttribute(XAttribute.MAX_HEALTH.get(), hp);
    entity.setHealth(hp);

    setAttribute(XAttribute.KNOCKBACK_RESISTANCE.get(), 0.8D);
    setAttribute(XAttribute.MOVEMENT_SPEED.get(), 0.28D);
    setAttribute(XAttribute.FOLLOW_RANGE.get(), 32.0D);
  }

  @Override
  protected List<MobAbility> createAbilities() {
    List<PotionEffectType> hostileEffects = Arrays.asList(
        PotionEffectType.HARM,
        PotionEffectType.POISON,
        PotionEffectType.SLOW,
        PotionEffectType.WEAKNESS,
        PotionEffectType.BLINDNESS);

    return new ArrayList<>(Arrays.asList(
        // 4 potions per barrage, every 3s (60 ticks), within 18 blocks, 8s effect (160 ticks), tier I.
        new PotionThrowAbility(/*cooldown*/ 60L, /*count*/ 4, /*range*/ 18.0,
                               /*durationTicks*/ 160, /*amplifier*/ 0, hostileEffects)
    ));
  }

  @Override
  public boolean onDamage(EntityDamageEvent event) {
    event.setDamage(event.getDamage() * (1.0 - getDamageReduction()));
    return false;
  }

  private void setAttribute(Attribute attribute, double value) {
    if (attribute == null) return;
    AttributeInstance inst = entity.getAttribute(attribute);
    if (inst != null) {
      inst.setBaseValue(value);
    }
  }
}

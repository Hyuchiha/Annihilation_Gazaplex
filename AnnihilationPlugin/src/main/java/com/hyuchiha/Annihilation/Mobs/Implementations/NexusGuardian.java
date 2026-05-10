package com.hyuchiha.Annihilation.Mobs.Implementations;

import com.cryptomorin.xseries.XAttribute;
import com.hyuchiha.Annihilation.Mobs.Abilities.AoeStompAbility;
import com.hyuchiha.Annihilation.Mobs.Abilities.ChargeAbility;
import com.hyuchiha.Annihilation.Mobs.CustomMob;
import com.hyuchiha.Annihilation.Mobs.MobAbility;
import org.bukkit.ChatColor;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reference implementation: a beefed-up zombie that patrols near a nexus, charges at the
 * nearest player when they're in mid-range, and pisotón-AOEs at melee range.
 *
 * <p>Use as a template for new custom mobs:
 * <ol>
 *   <li>Subclass {@link CustomMob}.</li>
 *   <li>Set attributes in {@link #configure()}.</li>
 *   <li>Return the desired abilities from {@link #createAbilities()}.</li>
 * </ol>
 *
 * Spawn from anywhere:
 * <pre>{@code
 * LivingEntity z = (LivingEntity) world.spawnEntity(loc, EntityType.ZOMBIE);
 * CustomMobManager.spawn(z, NexusGuardian::new);
 * }</pre>
 */
public class NexusGuardian extends CustomMob {

  public NexusGuardian(LivingEntity entity) {
    super(entity);
  }

  @Override
  public String getTypeId() {
    return "nexus_guardian";
  }

  @Override
  protected void configure() {
    entity.setCustomName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Nexus Guardian");
    entity.setCustomNameVisible(true);

    setAttribute(XAttribute.MAX_HEALTH.get(), 60.0D);
    entity.setHealth(60.0D);

    setAttribute(XAttribute.ATTACK_DAMAGE.get(), 7.0D);
    setAttribute(XAttribute.MOVEMENT_SPEED.get(), 0.32D);
    setAttribute(XAttribute.KNOCKBACK_RESISTANCE.get(), 0.6D);

    EntityEquipment eq = entity.getEquipment();
    if (eq != null) {
      eq.setItemInMainHand(new ItemStack(org.bukkit.Material.IRON_SWORD));
      eq.setItemInMainHandDropChance(0.0F);
    }
  }

  @Override
  protected List<MobAbility> createAbilities() {
    return new ArrayList<>(Arrays.asList(
        // Charge for 4s when target is within 12 blocks, every 12s
        new ChargeAbility(/*cooldown*/ 240L, /*duration*/ 80, /*amplifier*/ 1, /*range*/ 12.0),
        // AOE stomp at melee, 8 dmg with knockback, every 6s
        new AoeStompAbility(/*cooldown*/ 120L, /*radius*/ 3.5, /*damage*/ 8.0,
                            /*knockback*/ 1.2, /*meleeRange*/ 2.5)
    ));
  }

  /** Helper for cross-version attribute setting; silently skips if attribute missing. */
  private void setAttribute(org.bukkit.attribute.Attribute attribute, double value) {
    if (attribute == null) return;
    AttributeInstance inst = entity.getAttribute(attribute);
    if (inst != null) {
      inst.setBaseValue(value);
    }
  }
}

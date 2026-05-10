package com.hyuchiha.Annihilation.Mobs.Implementations;

import com.cryptomorin.xseries.XAttribute;
import com.hyuchiha.Annihilation.Mobs.CustomMob;
import com.hyuchiha.Annihilation.Mobs.MobAbility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * Player-like zombie: full iron kit, iron sword, adult, won't burn in daylight, picks up
 * items, higher follow range and walking speed. No special abilities — the equipment +
 * attributes alone produce a noticeably more dangerous melee threat than vanilla.
 *
 * <p>"Player-like" behaviors handled here:
 * <ul>
 *   <li>Full armor + weapon → uses {@code EntityEquipment} like a player would.</li>
 *   <li>{@code setCanPickupItems(true)} → upgrades gear it finds on the ground.</li>
 *   <li>{@code setShouldBurnInDay(false)} → operates round-the-clock.</li>
 *   <li>Wider follow range → sees players from farther away.</li>
 *   <li>Higher movement speed → can keep up with sprinting players for short bursts.</li>
 * </ul>
 *
 * <p>For more advanced behaviors (weapon-switch by range, retreat at low HP, shield
 * blocking), add a {@link MobAbility} subclass — those would compose on top of this
 * baseline without changing the equipment/attribute setup.
 *
 * <pre>{@code
 * LivingEntity z = (LivingEntity) world.spawnEntity(loc, EntityType.ZOMBIE);
 * CustomMobManager.spawn(z, ArmoredZombie::new);
 * }</pre>
 */
public class ArmoredZombie extends CustomMob {

  public ArmoredZombie(LivingEntity entity) {
    super(entity);
  }

  @Override
  public String getTypeId() {
    return "annihilation_armored_zombie";
  }

  /** Override to inject config-driven or per-spawn HP. */
  protected double getMaxHealth() { return 40.0D; }

  /** Override to inject a custom display name (e.g. player name on disconnect zombies). */
  protected String getDisplayName() {
    return ChatColor.GRAY + "" + ChatColor.BOLD + "Hardened Soldier";
  }

  /** Override to control whether the name floats above the head (default: false). */
  protected boolean isNameVisible() { return false; }

  @Override
  protected void configure() {
    entity.setCustomName(getDisplayName());
    entity.setCustomNameVisible(isNameVisible());

    if (entity instanceof Zombie) {
      Zombie z = (Zombie) entity;
      z.setBaby(false);
      z.setCanPickupItems(true);
      // Bukkit added Zombie#setShouldBurnInDay(boolean) in 1.20.4; the project compiles against
      // Spigot 1.19.4 which doesn't expose it. Call via reflection so the code stays compatible
      // both at compile time AND at runtime — silent no-op on servers where the method is missing.
      try {
        z.getClass().getMethod("setShouldBurnInDay", boolean.class).invoke(z, false);
      } catch (ReflectiveOperationException ignored) {
        // Method not present at runtime — daylight will eventually kill an exposed zombie.
      }
    }

    double hp = getMaxHealth();
    setAttribute(XAttribute.MAX_HEALTH.get(), hp);
    entity.setHealth(hp);
    setAttribute(XAttribute.ATTACK_DAMAGE.get(), 6.0D);
    setAttribute(XAttribute.MOVEMENT_SPEED.get(), 0.30D);
    setAttribute(XAttribute.ARMOR.get(), 8.0D);
    setAttribute(XAttribute.FOLLOW_RANGE.get(), 40.0D);

    EntityEquipment eq = entity.getEquipment();
    if (eq != null) {
      eq.setHelmet(new ItemStack(Material.IRON_HELMET));
      eq.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
      eq.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
      eq.setBoots(new ItemStack(Material.IRON_BOOTS));
      eq.setItemInMainHand(new ItemStack(Material.IRON_SWORD));

      // No equipment drops on death — keeps the loot table clean for game-specific rewards.
      eq.setHelmetDropChance(0.0F);
      eq.setChestplateDropChance(0.0F);
      eq.setLeggingsDropChance(0.0F);
      eq.setBootsDropChance(0.0F);
      eq.setItemInMainHandDropChance(0.0F);
    }
  }

  @Override
  protected List<MobAbility> createAbilities() {
    return Collections.emptyList();
  }

  private void setAttribute(Attribute attribute, double value) {
    if (attribute == null) return;
    AttributeInstance inst = entity.getAttribute(attribute);
    if (inst != null) {
      inst.setBaseValue(value);
    }
  }
}

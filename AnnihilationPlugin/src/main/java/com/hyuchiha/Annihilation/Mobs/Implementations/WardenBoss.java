package com.hyuchiha.Annihilation.Mobs.Implementations;

import com.cryptomorin.xseries.XAttribute;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Mobs.Abilities.AoeStompAbility;
import com.hyuchiha.Annihilation.Mobs.Abilities.SonicBeamAbility;
import com.hyuchiha.Annihilation.Mobs.CustomMob;
import com.hyuchiha.Annihilation.Mobs.MobAbility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Boss-tier Warden: HP 600, 50% damage reduction, 5-beam sonic-burst every 10s, melee
 * AOE stomp. Requires MC ≥ 1.19 — caller must spawn an {@code EntityType.WARDEN} (not
 * referenced here so this class compiles on the project's 1.19.4 target).
 *
 * <p>Recommended spawn pattern with version gate:
 * <pre>{@code
 * import org.inventivetalent.reflection.minecraft.Minecraft;
 *
 * if (Minecraft.Version.getVersion().olderThan(Minecraft.Version.v1_19_R1)) {
 *   // Fallback — Warden doesn't exist; spawn a Wither boss instead.
 *   LivingEntity fallback = (LivingEntity) world.spawnEntity(loc, EntityType.WITHER);
 *   CustomMobManager.spawn(fallback, WitherBoss::new);
 *   return;
 * }
 * LivingEntity warden = (LivingEntity) world.spawnEntity(loc, EntityType.valueOf("WARDEN"));
 * CustomMobManager.spawn(warden, WardenBoss::new);
 * }</pre>
 *
 * <p>Note: {@code EntityType.valueOf("WARDEN")} is used in the snippet above (not the
 * direct enum reference) so callers compiling against older Bukkit JARs don't fail at
 * link time. At runtime the enum lookup either succeeds (1.19+) or throws and you take
 * the fallback branch.
 */
public class WardenBoss extends CustomMob {
  private static final double DAMAGE_REDUCTION = 0.50D;

  public WardenBoss(LivingEntity entity) {
    super(entity);
  }

  @Override
  public String getTypeId() {
    return "annihilation_warden_boss";
  }

  /** Override to inject config-driven HP. */
  protected double getMaxHealth() { return 600.0D; }

  /** Override to inject config-driven display name. */
  protected String getDisplayName() {
    return ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Echo of the Deep";
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

    setAttribute(XAttribute.ATTACK_DAMAGE.get(), 20.0D);
    setAttribute(XAttribute.MOVEMENT_SPEED.get(), 0.30D);
    setAttribute(XAttribute.KNOCKBACK_RESISTANCE.get(), 1.0D);
    setAttribute(XAttribute.FOLLOW_RANGE.get(), 60.0D);
  }

  @Override
  protected List<MobAbility> createAbilities() {
    return new ArrayList<>(Arrays.asList(
        // 5 sonic beams in rapid succession every 10s — the Warden's signature.
        new SonicBeamAbility(/*cooldown*/ 200L, /*beamCount*/ 5, /*delayBetweenBeams*/ 8L,
                              /*range*/ 30.0, /*damage*/ 14.0),
        // Melee AOE stomp every 6s for crowd-clearing.
        new AoeStompAbility(/*cooldown*/ 120L, /*radius*/ 5.0, /*damage*/ 12.0,
                            /*knockback*/ 2.0, /*meleeRange*/ 4.0)
    ));
  }

  @Override
  public boolean onDamage(EntityDamageEvent event) {
    event.setDamage(event.getDamage() * (1.0D - getDamageReduction()));
    return false;
  }

  @Override
  public void onDeath(EntityDeathEvent event) {
    Player killer = event.getEntity().getKiller();

    event.getDrops().clear();
    event.getDrops().add(new ItemStack(Material.NETHER_STAR, 6));
    // ECHO_SHARD added in 1.19; safe under the >=1.19 spawn gate.
    Material echoShard = Material.matchMaterial("ECHO_SHARD");
    if (echoShard != null) {
      event.getDrops().add(new ItemStack(echoShard, 16));
    }
    event.setDroppedExp(2000);

    if (killer != null) {
      PlayerManager.addMoney(killer, 1000.0D);
      Bukkit.broadcastMessage(Translator.getPrefix() + ChatColor.AQUA
          + killer.getName() + ChatColor.GRAY + " silenció al "
          + ChatColor.DARK_AQUA + ChatColor.BOLD + "Echo of the Deep" + ChatColor.GRAY + "!");
    }
  }

  private void setAttribute(Attribute attribute, double value) {
    if (attribute == null) return;
    AttributeInstance inst = entity.getAttribute(attribute);
    if (inst != null) {
      inst.setBaseValue(value);
    }
  }
}

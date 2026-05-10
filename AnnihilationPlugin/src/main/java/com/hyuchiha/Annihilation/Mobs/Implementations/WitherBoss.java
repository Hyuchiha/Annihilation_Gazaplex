package com.hyuchiha.Annihilation.Mobs.Implementations;

import com.cryptomorin.xseries.XAttribute;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Mobs.Abilities.AoeStompAbility;
import com.hyuchiha.Annihilation.Mobs.Abilities.ChargeAbility;
import com.hyuchiha.Annihilation.Mobs.Abilities.WitherSkullAbility;
import com.hyuchiha.Annihilation.Mobs.Abilities.WitherSkullBarrageAbility;
import com.hyuchiha.Annihilation.Mobs.CustomMob;
import com.hyuchiha.Annihilation.Mobs.MobAbility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Boss-tier Wither: HP 400, immune to knockback, ranged single-skull pressure +
 * mid-range Charge + melee AOE stomp + radial skull barrage as a panic mechanic.
 *
 * <pre>{@code
 * LivingEntity w = (LivingEntity) world.spawnEntity(loc, EntityType.WITHER);
 * CustomMobManager.spawn(w, WitherBoss::new);
 * }</pre>
 */
public class WitherBoss extends CustomMob {

  public WitherBoss(LivingEntity entity) {
    super(entity);
  }

  @Override
  public String getTypeId() {
    return "annihilation_wither_boss";
  }

  /** Override to inject config-driven HP. */
  protected double getMaxHealth() { return 400.0D; }

  /** Override to inject config-driven display name. */
  protected String getDisplayName() {
    return ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "The Wither King";
  }

  @Override
  protected void configure() {
    entity.setCustomName(getDisplayName());
    entity.setCustomNameVisible(true);

    double hp = getMaxHealth();
    setAttribute(XAttribute.MAX_HEALTH.get(), hp);
    entity.setHealth(hp);

    setAttribute(XAttribute.ATTACK_DAMAGE.get(), 12.0D);
    setAttribute(XAttribute.MOVEMENT_SPEED.get(), 0.35D);
    setAttribute(XAttribute.KNOCKBACK_RESISTANCE.get(), 1.0D);
    setAttribute(XAttribute.FOLLOW_RANGE.get(), 48.0D);
  }

  @Override
  protected List<MobAbility> createAbilities() {
    return new ArrayList<>(Arrays.asList(
        // Single skull every 1.5s, range 6-30 (skip when in melee).
        new WitherSkullAbility(/*cooldown*/ 30L, /*minRange*/ 6.0, /*maxRange*/ 30.0, /*charged*/ false),
        // Burst toward target when within 12 blocks, every 8s.
        new ChargeAbility(/*cooldown*/ 160L, /*duration*/ 80, /*amplifier*/ 2, /*range*/ 12.0),
        // Melee AOE stomp every 5s.
        new AoeStompAbility(/*cooldown*/ 100L, /*radius*/ 4.0, /*damage*/ 10.0,
                            /*knockback*/ 1.5, /*meleeRange*/ 3.0),
        // Radial 12-skull barrage every 18s — only when something is within 20 blocks.
        new WitherSkullBarrageAbility(/*cooldown*/ 360L, /*skullCount*/ 12, /*speed*/ 1.2,
                                       /*charged*/ false, /*triggerRange*/ 20.0)
    ));
  }

  @Override
  public void onDeath(EntityDeathEvent event) {
    Player killer = event.getEntity().getKiller();

    event.getDrops().clear();
    event.getDrops().add(new ItemStack(Material.NETHER_STAR, 4));
    event.getDrops().add(new ItemStack(Material.DIAMOND_BLOCK, 8));
    event.setDroppedExp(1000);

    if (killer != null) {
      PlayerManager.addMoney(killer, 500.0D);
      Bukkit.broadcastMessage(Translator.getPrefix() + ChatColor.LIGHT_PURPLE
          + killer.getName() + ChatColor.AQUA + " derrotó al "
          + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Wither King" + ChatColor.AQUA + "!");
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

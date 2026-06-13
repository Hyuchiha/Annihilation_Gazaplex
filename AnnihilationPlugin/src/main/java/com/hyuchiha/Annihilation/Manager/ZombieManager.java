package com.hyuchiha.Annihilation.Manager;

import com.cryptomorin.xseries.XAttribute;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Mobs.CustomMobManager;
import com.hyuchiha.Annihilation.Mobs.Implementations.DisconnectZombie;
import com.hyuchiha.Annihilation.Mobs.MobCreator;
import com.hyuchiha.Annihilation.Mobs.v1_12_R1.MobCreator_v1_12_R1;
import com.hyuchiha.Annihilation.Mobs.v1_13_R1.MobCreator_v1_13_R1;
import com.hyuchiha.Annihilation.Mobs.v1_13_R2.MobCreator_v1_13_R2;
import com.hyuchiha.Annihilation.Mobs.v1_14_R1.MobCreator_v1_14_R1;
import com.hyuchiha.Annihilation.Mobs.v1_15_R1.MobCreator_v1_15_R1;
import com.hyuchiha.Annihilation.Mobs.v1_16_R1.MobCreator_v1_16_R1;
import com.hyuchiha.Annihilation.Mobs.v1_16_R2.MobCreator_v1_16_R2;
import com.hyuchiha.Annihilation.Mobs.v1_16_R3.MobCreator_v1_16_R3;
import com.hyuchiha.Annihilation.Mobs.v1_17_R1.MobCreator_v1_17_R1;
import com.hyuchiha.Annihilation.Mobs.v1_18_R1.MobCreator_v1_18_R1;
import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.inventivetalent.reflection.minecraft.Minecraft;

import java.util.HashMap;

public class ZombieManager {
  private static MobCreator creator;

  private static HashMap<String, Entity> zombies = new HashMap<>();

  public static void init() {
    Output.log("Initializing zombie creator");
    switch (Minecraft.Version.getVersion()) {
      case v1_12_R1:
        creator = new MobCreator_v1_12_R1();
        break;
      case v1_13_R1:
        creator = new MobCreator_v1_13_R1();
        break;
      case v1_13_R2:
        creator = new MobCreator_v1_13_R2();
        break;
      case v1_14_R1:
        creator = new MobCreator_v1_14_R1();
        break;
      case v1_15_R1:
        creator = new MobCreator_v1_15_R1();
        break;
      case v1_16_R1:
        creator = new MobCreator_v1_16_R1();
        break;
      case v1_16_R2:
        creator = new MobCreator_v1_16_R2();
        break;
      case v1_16_R3:
        creator = new MobCreator_v1_16_R3();
        break;
      case v1_17_R1:
        creator = new MobCreator_v1_17_R1();
        break;
      case v1_18_R1:
        creator = new MobCreator_v1_18_R1();
        break;
      default:
        Output.log("Version not supported");
        break;
    }
  }

  public static void createZombiePlayer(Player player) {
    World world = Bukkit.getWorld(player.getWorld().getName());
    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);
    Zombie zombie;

    // When the CustomMob framework is active it fully owns this mob (AI + abilities via
    // the DisconnectZombie wrapper below), so we spawn a plain Bukkit zombie and skip the
    // legacy NMS creator — otherwise both would apply and double up. The NMS creator is
    // only used when CustomMobManager is disabled (legacy versions without the opt-in).
    if (creator != null && !CustomMobManager.isEnabled()) {
      zombie = (Zombie) creator.getMob("CUSTOM_ZOMBIE").spawnEntity(player.getLocation());
    } else {
      zombie = world.spawn(player.getLocation(), Zombie.class);
    }

    AttributeInstance attribute = zombie.getAttribute(XAttribute.MAX_HEALTH.get());
    attribute.setBaseValue(40);
    zombie.setBaby(false);
    zombie.setCanPickupItems(false);
    zombie.setCustomName(gPlayer.getTeam().getChatColor() + player.getName());
    ItemStack hand = player.getInventory().getItemInMainHand();
    ItemStack[] armors = player.getInventory().getArmorContents();
    zombie.getEquipment().setItemInMainHand(hand);
    zombie.getEquipment().setArmorContents(armors);
    zombie.setRemoveWhenFarAway(false);

    String uuid = player.getUniqueId().toString();
    if (!zombies.containsKey(uuid)) {
      zombies.put(uuid, zombie);
      // Wrap with CustomMob (decorator pattern — configure() is no-op so the equipment
      // we just copied stays intact). Adds ChargeAbility for sprint-chase behavior.
      // Returns null when CustomMobManager is disabled (MC < 1.14, or 1.14–1.17 without the
      // enable-custom-mobs-legacy opt-in); in that case the zombie keeps the legacy AI.
      CustomMobManager.spawn(zombie, DisconnectZombie::new);
    }

  }

  public static HashMap<String, Entity> getZombies() {
    return zombies;
  }

  public static void clearZombiesData() {
    World world = MapManager.getCurrentMap().getWorld();

    for (Entity entity : world.getEntities()) {
      if (entity.getType() == EntityType.ZOMBIE) {
        entity.remove();
      }
    }

    zombies.clear();
  }

}

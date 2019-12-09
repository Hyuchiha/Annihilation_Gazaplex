package com.hyuchiha.Annihilation.Manager;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Mobs.MobCreator;
import com.hyuchiha.Annihilation.Mobs.v1_10_R1.MobCreator_v1_10_R1;
import com.hyuchiha.Annihilation.Mobs.v1_11_R1.MobCreator_v1_11_R1;
import com.hyuchiha.Annihilation.Mobs.v1_12_R1.MobCreator_v1_12_R1;
import com.hyuchiha.Annihilation.Mobs.v1_13_R1.MobCreator_v1_13_R1;
import com.hyuchiha.Annihilation.Mobs.v1_13_R2.MobCreator_v1_13_R2;
import com.hyuchiha.Annihilation.Mobs.v1_9_R1.MobCreator_v1_9_R1;
import com.hyuchiha.Annihilation.Mobs.v1_9_R2.MobCreator_v1_9_R2;
import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.Bukkit;
import org.bukkit.World;
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
    switch (Minecraft.Version.getVersion()){
      case v1_9_R1:
        creator = new MobCreator_v1_9_R1();
        break;
      case v1_9_R2:
        creator = new MobCreator_v1_9_R2();
        break;
      case v1_10_R1:
        creator = new MobCreator_v1_10_R1();
        break;
      case v1_11_R1:
        creator = new MobCreator_v1_11_R1();
        break;
      case v1_12_R1:
        creator = new MobCreator_v1_12_R1();
        break;
      case v1_13_R1:
        creator = new MobCreator_v1_13_R1();
        break;
      case v1_13_R2:
        creator = new MobCreator_v1_13_R2();
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

    if (creator != null) {
      zombie = (Zombie) creator.getMob("CUSTOM_ZOMBIE").spawnEntity(player.getLocation());
    } else {
      zombie = world.spawn(player.getLocation(), Zombie.class);
    }

    zombie.setBaby(false);
    zombie.setMaxHealth(40);
    zombie.setCanPickupItems(false);
    zombie.setCustomName(gPlayer.getTeam().getChatColor() + player.getName());
    ItemStack hand = player.getInventory().getItemInHand();
    ItemStack[] armors = player.getInventory().getArmorContents();
    zombie.getEquipment().setItemInHand(hand);
    zombie.getEquipment().setArmorContents(armors);

    if (!zombies.containsKey(zombie.getCustomName())) {
      zombies.put(zombie.getCustomName(), zombie);
    }

  }

  public static HashMap<String, Entity> getZombies() {
    return zombies;
  }

  public static void clearZombiesData() {
    World world = MapManager.getCurrentMap().getWorld();

    for (Entity entity: world.getEntities()) {
      if(entity.getType() == EntityType.ZOMBIE){
        Output.log("Removing zombie");
        entity.remove();
      }
    }

    zombies.clear();
  }

}

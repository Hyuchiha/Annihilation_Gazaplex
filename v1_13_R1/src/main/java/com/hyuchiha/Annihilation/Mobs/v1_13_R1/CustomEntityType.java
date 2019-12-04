package com.hyuchiha.Annihilation.Mobs.v1_13_R1;

import com.hyuchiha.Annihilation.Mobs.EntityType;
import com.hyuchiha.Annihilation.Mobs.MobUtils;
import com.hyuchiha.Annihilation.Mobs.v1_13_R1.NMS.CustomWither;
import com.hyuchiha.Annihilation.Mobs.v1_13_R1.NMS.CustomZombie;
import net.minecraft.server.v1_13_R1.Entity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Map;

public enum CustomEntityType implements EntityType {
  CUSTOM_WITHER("Wither", 64, CustomWither.class),
  CUSTOM_ZOMBIE("Zombie", 54, CustomZombie.class);

  CustomEntityType(String name, int id, Class<? extends Entity> custom) {
    addToMaps(custom, name, id);
  }

  public org.bukkit.entity.Entity spawnEntity(Location location) {
    Entity entity = getEntity(this, location.getWorld());

    entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    ((CraftWorld) location.getWorld()).getHandle().addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);

    return entity.getBukkitEntity();
  }

  public void addToMaps(Class clazz, String name, int id) {
    //getPrivateField is the method from above.
    //Remove the lines with // in front of them if you want to override default entities (You'd have to remove the default entity from the map first though).
    ((Map) MobUtils.getPrivateField("c", net.minecraft.server.v1_13_R1.EntityTypes.class, null)).put(name, clazz);
    ((Map) MobUtils.getPrivateField("d", net.minecraft.server.v1_13_R1.EntityTypes.class, null)).put(clazz, name);
    //((Map)getPrivateField("e", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(Integer.valueOf(id), clazz);
    ((Map) MobUtils.getPrivateField("f", net.minecraft.server.v1_13_R1.EntityTypes.class, null)).put(clazz, id);
    //((Map)getPrivateField("g", net.minecraft.server.v1_7_R4.EntityTypes.class, null)).put(name, Integer.valueOf(id));
  }

  public Entity getEntity(CustomEntityType entity, World world) {
    CraftWorld craftWorld = (CraftWorld) world;

    switch (entity) {
      case CUSTOM_WITHER:
        return new CustomWither(craftWorld.getHandle());
      case CUSTOM_ZOMBIE:
      default:
        return new CustomZombie(craftWorld.getHandle());
    }
  }
}

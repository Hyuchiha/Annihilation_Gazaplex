package com.hyuchiha.Annihilation.Mobs.v1_14_R1;

import com.hyuchiha.Annihilation.Mobs.EntityType;
import com.hyuchiha.Annihilation.Mobs.v1_14_R1.NMS.CustomWither;
import com.hyuchiha.Annihilation.Mobs.v1_14_R1.NMS.CustomZombie;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityTypes;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public enum CustomEntityType implements EntityType {
  CUSTOM_WITHER("Wither", 64, CustomWither.class),
  CUSTOM_ZOMBIE("Zombie", 54, CustomZombie.class);

  private static CustomEntityRegistry ENTITY_REGISTRY = CustomEntityRegistry.getInstance();

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
    registerEntityClass(clazz);
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

  public void registerEntityClass(Class<?> clazz) {
    if (ENTITY_REGISTRY == null)
      return;

    Class<?> search = clazz;
    while ((search = search.getSuperclass()) != null && Entity.class.isAssignableFrom(search)) {
      EntityTypes<?> type = ENTITY_REGISTRY.findType(search);
      MinecraftKey key = ENTITY_REGISTRY.getKey(type);
      if (key == null)
        continue;
      int code = ENTITY_REGISTRY.a(type);
      ENTITY_REGISTRY.put(code, key, type);
      return;
    }
    throw new IllegalArgumentException("unable to find valid entity superclass for class " + clazz.toString());
  }
}

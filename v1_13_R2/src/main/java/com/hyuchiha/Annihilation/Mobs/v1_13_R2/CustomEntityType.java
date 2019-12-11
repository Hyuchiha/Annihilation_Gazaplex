package com.hyuchiha.Annihilation.Mobs.v1_13_R2;

import com.hyuchiha.Annihilation.Mobs.EntityType;
import com.hyuchiha.Annihilation.Mobs.v1_13_R2.NMS.CustomWither;
import com.hyuchiha.Annihilation.Mobs.v1_13_R2.NMS.CustomZombie;
import net.minecraft.server.v1_13_R2.Entity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

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
    // TODO look the way to register entities in 1.13
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

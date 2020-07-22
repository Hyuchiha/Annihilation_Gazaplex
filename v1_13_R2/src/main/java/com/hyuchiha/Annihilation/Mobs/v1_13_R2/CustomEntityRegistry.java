package com.hyuchiha.Annihilation.Mobs.v1_13_R2;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import net.minecraft.server.v1_13_R2.EntityTypes;
import net.minecraft.server.v1_13_R2.IRegistry;
import net.minecraft.server.v1_13_R2.MinecraftKey;
import net.minecraft.server.v1_13_R2.RegistryMaterials;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("rawtypes")
public class CustomEntityRegistry extends RegistryMaterials {

  private static CustomEntityRegistry instance = null;

  private final BiMap<MinecraftKey, EntityTypes> entities = HashBiMap.create();
  private final BiMap<EntityTypes, MinecraftKey> entityClasses = this.entities.inverse();
  private final Map<EntityTypes, Integer> entityIds = Maps.newHashMap();

  private final RegistryMaterials<EntityTypes<?>> wrapped;

  public CustomEntityRegistry(RegistryMaterials<EntityTypes<?>> original) {
    this.wrapped = original;
  }

  public static CustomEntityRegistry getInstance() {
    if (instance != null) {
      return instance;
    }

    try {
      Field registryMaterialsField = IRegistry.class.getDeclaredField("ENTITY_TYPE");
      registryMaterialsField.setAccessible(true);

      MethodHandle registryMaterialsSetter = MethodHandles.lookup().unreflectSetter(registryMaterialsField);

      Field modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      modifiersField.setInt(registryMaterialsField, registryMaterialsField.getModifiers() & ~Modifier.FINAL);

      instance = new CustomEntityRegistry((RegistryMaterials<EntityTypes<?>>) registryMaterialsField.get(null));

      registryMaterialsSetter.invoke(instance);
    } catch (Exception e) {
      instance = null;

      throw new RuntimeException("Unable to override the old entity RegistryMaterials", e);
    } catch (Throwable throwable) {
      throw new RuntimeException("Error trying to set EntityRegistry");
    }

    return instance;
  }

  @Override
  public int a(Object key) {
    if (entityIds.containsKey(key)) {
      return entityIds.get(key);
    }

    return wrapped.a((EntityTypes) key);
  }

  @Override
  public Object a(Random paramRandom) {
    return wrapped.a(paramRandom);
  }

  @Override
  public boolean c(MinecraftKey paramK) {
    return wrapped.c(paramK);
  }

  public EntityTypes findType(Class<?> search) {
    for (Object type : wrapped) {
      if (((EntityTypes) type).c() == search) {
        return (EntityTypes) type;
      }
    }
    return null;
  }

  @Override
  public EntityTypes get(MinecraftKey key) {
    if (entities.containsKey(key)) {
      return entities.get(key);
    }

    return wrapped.get(key);
  }

  @Override
  public MinecraftKey getKey(Object value) {
    if (entityClasses.containsKey(value)) {
      return entityClasses.get(value);
    }

    return wrapped.getKey((EntityTypes) value);
  }

  public RegistryMaterials<EntityTypes<?>> getWrapped() {
    return wrapped;
  }

  @Override
  public Iterator<Object> iterator() {
    return (Iterator) wrapped.iterator();
  }

  @Override
  public Set<Object> keySet() {
    return (Set) wrapped.keySet();
  }

  public void put(int entityId, MinecraftKey key, EntityTypes entityClass) {
    entities.put(key, entityClass);
    entityIds.put(entityClass, entityId);
  }
}
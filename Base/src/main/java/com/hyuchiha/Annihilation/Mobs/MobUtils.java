package com.hyuchiha.Annihilation.Mobs;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;

public class MobUtils {
  public static Object getPrivateField(String fieldName, Class clazz, Object object) {
    Field field;
    Object o = null;

    try {
      field = clazz.getDeclaredField(fieldName);

      field.setAccessible(true);

      o = field.get(object);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      Bukkit.getLogger().severe(e.getMessage());
    }

    return o;
  }
}

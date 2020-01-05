package com.hyuchiha.Annihilation.Maps.Hooks;

import org.bukkit.World;

public interface Hooks {

  void preUnload(String world, World.Environment environment);

  void postUnload(String world, World.Environment environment);

  void preLoad(String world, World.Environment environment);

  void postLoad(String world, World.Environment environment);

}

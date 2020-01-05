package com.hyuchiha.Annihilation.Maps.Hooks;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class MultiverseCoreHooks implements Hooks {

  private Plugin plugin;

  public MultiverseCoreHooks(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void preUnload(String world, World.Environment environment) {

  }

  @Override
  public void postUnload(String world, World.Environment environment) {

  }

  @Override
  public void preLoad(String world, World.Environment environment) {

  }

  @Override
  public void postLoad(String world, World.Environment environment) {
    World bukkitWorld = plugin.getServer().getWorld(world);
    if (bukkitWorld != null && getMultiverseCore().getMVWorldManager().getMVWorld(world) == null) {
      getMultiverseCore().getMVWorldManager().addWorld(world, environment, String.valueOf(bukkitWorld.getSeed()), bukkitWorld.getWorldType(), Boolean.TRUE, null);
    }
  }

  private MultiverseCore getMultiverseCore() {
    return (MultiverseCore) plugin.getServer().getPluginManager().getPlugin("Multiverse-Core");
  }

}

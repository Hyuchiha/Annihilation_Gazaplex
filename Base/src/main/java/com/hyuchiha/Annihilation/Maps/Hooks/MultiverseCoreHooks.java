package com.hyuchiha.Annihilation.Maps.Hooks;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class MultiverseCoreHooks implements Hooks {

  private Plugin plugin;

  public MultiverseCoreHooks(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void preUnload(String world, World.Environment environment) {
    getMultiverseCore().getMVWorldManager().unloadWorld(world);
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

    if (bukkitWorld != null) {
      MVWorldManager manager = getMultiverseCore().getMVWorldManager();;

      if (manager.isMVWorld(world)) {
        manager.loadWorld(world);
        return;
      }

      manager.addWorld(world, environment, String.valueOf(bukkitWorld.getSeed()), bukkitWorld.getWorldType(), Boolean.TRUE, null);
    }
  }

  private MultiverseCore getMultiverseCore() {
    return (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
  }

}

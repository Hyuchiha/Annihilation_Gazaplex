package com.hyuchiha.Annihilation.Maps;

import com.hyuchiha.Annihilation.Maps.Hooks.Hooks;
import com.hyuchiha.Annihilation.Maps.Hooks.MultiverseCoreHooks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class MapLoader {
  private static final List<Hooks> hooks = new ArrayList<>();
  private static Plugin plugin;

  public static void initMapLoader(Plugin main) {
    plugin = main;

    if (plugin.getServer().getPluginManager().isPluginEnabled("Multiverse-Core")) {

      plugin.getLogger().log(Level.INFO, "Loading hook for Multiver-Core");

      hooks.add(new MultiverseCoreHooks(plugin));
    }

    File mapsFolder = new File(plugin.getDataFolder(), "maps");
    if (!mapsFolder.exists()) {
      mapsFolder.mkdir();
    }
  }

  public static boolean loadMap(String name) {
    File mapsFolder = new File(plugin.getDataFolder(), "maps");
    if (!mapsFolder.exists()) {
      return false;
    }

    File source = new File(mapsFolder, name);
    if (!source.exists()) {
      return false;
    }

    Map<Player, Location> players = unloadWorld(name, false);

    File destination = new File(plugin.getDataFolder().getParentFile().getParentFile(), name);
    try {
      copyFolder(source, destination);

      World bukkitWorld = loadWorld(name);
      for (Player player : players.keySet()) {
        player.teleport(bukkitWorld.getSpawnLocation());
      }

      return true;
    } catch (IOException e) {
      plugin.getLogger().severe("Could not load map " + name);
      e.printStackTrace();
      return false;
    }

  }

  public static boolean saveMap(String name) {
    File mapsFolder = new File(plugin.getDataFolder(), "maps");
    if (!mapsFolder.exists()) {
      mapsFolder.mkdir();
    }
    File source = new File(plugin.getDataFolder().getParentFile().getParentFile(), name + File.separator + "region");
    if (!source.exists()) {
      return false;
    }

    File destination = new File(mapsFolder, name);
    try {
      copyFolder(source, destination);
      return true;
    } catch (IOException e) {
      plugin.getLogger().severe("Could not save map " + name);
      e.printStackTrace();
      return false;
    }
  }

  private static void copyFolder(File src, File dest) throws IOException {
    if (!src.exists()) {
      plugin.getLogger().severe("File " + src.toString()
                                    + " does not exist, cannot copy");
      return;
    }

    if (src.isDirectory()) {
      boolean existed = dest.exists();
      if (!existed) {
        dest.mkdir();
      }

      String[] srcFiles = src.list();

      if (srcFiles != null) {
        for (String file : srcFiles) {
          File srcFile = new File(src, file);
          File destFile = new File(dest, file);
          copyFolder(srcFile, destFile);
        }
      }

    } else {
      InputStream in = new FileInputStream(src);
      OutputStream out = new FileOutputStream(dest);

      byte[] buffer = new byte[1024];
      int length;
      while ((length = in.read(buffer)) > 0) {
        out.write(buffer, 0, length);
      }

      in.close();
      out.close();
    }
  }

  private static Map<Player, Location> unloadWorld(String world, boolean save) {
    Map<Player, Location> players = new HashMap<>();
    World bukkitWorld = plugin.getServer().getWorld(world);
    if (bukkitWorld != null) {
      for (Player player : bukkitWorld.getPlayers()) {
        players.put(player, player.getLocation());
        player.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
      }

      for (Hooks hook : hooks) {
        try {
          hook.preUnload(world);
        } catch (Exception e) {
          plugin.getLogger().log(Level.WARNING, "Error calling hook", e);
        }
      }

      if (!plugin.getServer().unloadWorld(bukkitWorld, save)) {
        for (Map.Entry<Player, Location> e : players.entrySet()) {
          e.getKey().teleport(e.getValue());
        }
        throw new IllegalStateException("Bukkit cowardly refused to unload the world: " + world);
      }

      for (Hooks hook : hooks) {
        try {
          hook.postUnload(world);
        } catch (Exception e) {
          plugin.getLogger().log(Level.WARNING, "Error calling hook", e);
        }
      }
    }

    return players;
  }

  private static World loadWorld(String world) {
    for (Hooks hook : hooks) {
      try {
        hook.preLoad(world);
      } catch (Exception e) {
        plugin.getLogger().log(Level.WARNING, "Error calling hook", e);
      }
    }

    WorldCreator wc = new WorldCreator(world);
    wc.generator(new VoidGenerator());
    World result = Bukkit.createWorld(wc);

    for (Hooks hook : hooks) {
      try {
        hook.postLoad(world);
      } catch (Exception e) {
        plugin.getLogger().log(Level.WARNING, "Error calling hook", e);
      }
    }
    return result;
  }
}

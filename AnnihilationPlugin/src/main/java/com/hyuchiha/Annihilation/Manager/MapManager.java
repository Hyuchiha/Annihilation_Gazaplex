package com.hyuchiha.Annihilation.Manager;

import com.hyuchiha.Annihilation.Arena.GameArena;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Maps.MapLoader;
import com.hyuchiha.Annihilation.Maps.VoidGenerator;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Utils.LocationUtils;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.inventivetalent.reflection.minecraft.Minecraft;
import org.inventivetalent.reflection.minecraft.MinecraftVersion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class MapManager {
  private static final ArrayList<String> maps = new ArrayList<>();
  private static Location lobbySpawn;
  private static GameArena currentArena = null;

  public static void initMaps() {
    Main main = Main.getInstance();

    Configuration config = main.getConfig("maps.yml");

    for (String s : config.getKeys(false)) {
      if (!s.equalsIgnoreCase("lobby")) {
        Output.log("Loading map " + s);

        String envValue = config.getString(s + ".env", "NORMAL");
        World.Environment environment = World.Environment.valueOf(envValue);

        if (MapLoader.loadMap(s, environment)) {
          maps.add(s);
        }
      }
    }

    Output.log("Creating map lobby");

    WorldCreator wc = new WorldCreator("lobby");
    wc.generator(new VoidGenerator());
    World world = Bukkit.createWorld(wc);

    initSettingForArena(world);

    Output.log("Lobby created");

    lobbySpawn = LocationUtils.parseLocation(Bukkit.getWorld("lobby"), config.getString("lobby.spawn"));
  }

  private static void initSettingForArena(World world) {
    if (MinecraftVersion.getVersion().olderThan(Minecraft.Version.v1_13_R1)) {
      world.setGameRuleValue("doFireTick", "false");
      world.setGameRuleValue("doDaylightCycle", "false");
      world.setGameRuleValue("doMobSpawning", "false");
    } else {
      world.setGameRule(GameRule.DO_FIRE_TICK, false);
      world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
      world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
    }
  }

  public static Location getLobbySpawn() {
    return lobbySpawn;
  }


  public static void selectMap(String mapName) {
    currentArena = new GameArena(mapName);
  }


  public static boolean isMapSelected() {
    return (currentArena != null);
  }


  public static GameArena getCurrentMap() {
    return currentArena;
  }

  public static List<String> getRandomMaps() {
    LinkedList<String> shuffledMaps = new LinkedList<>(maps);
    Collections.shuffle(shuffledMaps);
    return shuffledMaps.subList(0, Math.min(5, shuffledMaps.size()));
  }

  public static void resetMap() {
    if (currentArena != null) {
      currentArena.rollbackArena();
      currentArena = null;
    }
  }
}

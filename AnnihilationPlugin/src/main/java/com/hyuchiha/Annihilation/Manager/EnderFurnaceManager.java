package com.hyuchiha.Annihilation.Manager;

import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.VirtualEntities.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.inventivetalent.reflection.minecraft.Minecraft;

import java.util.HashMap;

public class EnderFurnaceManager {
  private static HashMap<GameTeam, Location> locations = new HashMap<>();
  private static FurnaceManager furnaceManager;

  public static void initFurnaceManager() {
    Output.log("Initializing furnace manager for the server version");
    Main main = Main.getInstance();

    switch (Minecraft.Version.getVersion()) {
      case v1_9_R1:
        furnaceManager = new FurnaceManager_v1_9_R1(main);
        break;
      case v1_9_R2:
        furnaceManager = new FurnaceManager_v1_9_R2(main);
        break;
      case v1_10_R1:
        furnaceManager = new FurnaceManager_v1_10_R1(main);
        break;
      case v1_11_R1:
        furnaceManager = new FurnaceManager_v1_11_R1(main);
        break;
      case v1_12_R1:
        furnaceManager = new FurnaceManager_v1_12_R1(main);
        break;
      case v1_13_R1:
        furnaceManager = new FurnaceManager_v1_13_R1(main);
        break;
      case v1_13_R2:
        furnaceManager = new FurnaceManager_v1_13_R2(main);
        break;
      case v1_14_R1:
        furnaceManager = new FurnaceManager_v1_14_R1(main);
        break;
      case v1_15_R1:
        furnaceManager = new FurnaceManager_v1_15_R1(main);
        break;
      default:
        Output.log("Version not supported");
        Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
        break;
    }

  }


  public static void loadFurnaceLocationForTeam(GameTeam team, Location location) {
    locations.put(team, location);

    location.getBlock().setType(Material.FURNACE);
  }


  public static void clearTeamsFurnaces() {
    locations.clear();
  }


  public static boolean teamHasFurnaceRegistered(GameTeam team) {
    return locations.containsKey(team);
  }


  public static boolean isFurnaceLocation(Location location) {
    return locations.containsValue(location);
  }


  public static boolean isTeamFurnace(GameTeam team, Location location) {
    return locations.get(team).equals(location);
  }


  public static void openFurnaceForUser(Player player) {
    VirtualFurnace furnace = furnaceManager.getFurnace(player);
    furnace.openFurnace();
  }


  public static boolean isFurnaceEnabled() {
    return furnaceManager.isRunning();
  }


  public static void disableFurnaceManager() {
    furnaceManager.clearFurnaces();
    furnaceManager.disableFurnaces();
  }
}

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

public class EnderBrewingManager {
  private static HashMap<GameTeam, Location> locations = new HashMap<>();
  private static BrewingManager brewingManager;

  public static void initBrewingManager() {
    Output.log("Initializing brewing manager for the server version");
    Main main = Main.getInstance();

    switch (Minecraft.Version.getVersion()) {
      case v1_9_R1:
        brewingManager = new BrewingManager_v1_9_R1(main);
        break;
      case v1_9_R2:
        brewingManager = new BrewingManager_v1_9_R2(main);
        break;
      case v1_10_R1:
        brewingManager = new BrewingManager_v1_10_R1(main);
        break;
      case v1_11_R1:
        brewingManager = new BrewingManager_v1_11_R1(main);
        break;
      case v1_12_R1:
        brewingManager = new BrewingManager_v1_12_R1(main);
        break;
      case v1_13_R1:
        brewingManager = new BrewingManager_v1_13_R1(main);
        break;
      case v1_13_R2:
        brewingManager = new BrewingManager_v1_13_R2(main);
        break;
      case v1_14_R1:
        brewingManager = new BrewingManager_v1_14_R1(main);
        break;
      case v1_15_R1:
        brewingManager = new BrewingManager_v1_15_R1(main);
        break;
      case v1_16_R1:
        brewingManager = new BrewingManager_v1_16_R1(main);
        break;
      case v1_16_R2:
        brewingManager = new BrewingManager_v1_16_R2(main);
        break;
      case v1_16_R3:
        brewingManager = new BrewingManager_v1_16_R3(main);
        break;
      case v1_17_R1:
        brewingManager = new BrewingManager_v1_17_R1(main);
        break;
      case v1_18_R1:
        brewingManager = new BrewingManager_v1_18_R1(main);
        break;
      default:
        Output.logError("Version not supported");
        Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
        break;
    }
  }


  public static void loadBrewingLocationForTeam(GameTeam team, Location location) {
    locations.put(team, location);

    location.getBlock().setType(Material.BREWING_STAND);
  }


  public static void clearTeamsEnderBrewings() {
    locations.clear();
  }


  public static boolean teamHasBrewingRegistered(GameTeam team) {
    return locations.containsKey(team);
  }


  public static boolean isBrewingLocation(Location location) {
    return locations.containsValue(location);
  }


  public static boolean isTeamBrewing(GameTeam team, Location location) {
    return locations.get(team).equals(location);
  }


  public static void openBrewingForUser(Player player) {
    VirtualBrewingStand brewingStand = brewingManager.getBrewingStand(player);
    brewingStand.openBrewingStand();
  }


  public static boolean isBrewingEnabled() {
    return brewingManager.isRunning();
  }


  public static void disableBrewingManager() {
    brewingManager.clearBrewingStands();
    brewingManager.disableBrewingStands();
  }
}

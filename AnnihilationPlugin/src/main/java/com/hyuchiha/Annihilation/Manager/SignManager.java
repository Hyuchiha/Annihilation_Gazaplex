package com.hyuchiha.Annihilation.Manager;

import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import com.hyuchiha.Annihilation.Utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.HashMap;


public class SignManager {
  private static final Main plugin = Main.getInstance();
  private static final HashMap<GameTeam, ArrayList<Location>> signs = new HashMap<>();
  // Cached last-rendered content per team to skip block state updates when nothing changed.
  private static final HashMap<GameTeam, String[]> lastLines = new HashMap<>();

  public static void initSigns() {
    Configuration configuration = plugin.getConfig("maps.yml");
    for (GameTeam team : GameTeam.teams()) {
      signs.put(team, new ArrayList<>());
      String name = team.name().toLowerCase();
      for (String l : configuration.getStringList("lobby.signs." + name)) {
        Location loc = LocationUtils.parseLocation(Bukkit.getWorld("lobby"), l);
        if (loc != null) {
          addTeamSign(team, loc);
        }
      }
    }
  }


  private static void addTeamSign(GameTeam team, Location loc) {
    Block b = loc.getBlock();
    if (b == null) {
      return;
    }
    if (GameUtils.isWallSign(b)) {
      signs.get(team).add(loc);
      updateIndividualSign(team);
    }
  }

  public static void updateSigns() {
    for (GameTeam team : GameTeam.teams()) {
      updateIndividualSign(team);
    }
  }

  public static void updateIndividualSign(GameTeam t) {
    if (t == GameTeam.NONE) {
      return;
    }

    ArrayList<Location> teamSigns = signs.get(t);
    if (teamSigns == null || teamSigns.isEmpty()) {
      return;
    }

    int playerCount = t.onlineMemberCount();
    String suffix = " " + Translator.getColoredString("COMMONS.PLAYER") + ((playerCount == 1) ? "" : "s");

    String line0 = ChatColor.DARK_PURPLE + "[" + Translator.getColoredString("COMMONS.TEAM") + ChatColor.DARK_PURPLE + "]";
    String line1 = t.coloredName();
    String line2 = ChatColor.UNDERLINE + Integer.toString(playerCount) + suffix;
    String line3;
    if (t.getNexus() != null && GameManager.getCurrentGame() != null && GameManager.getCurrentGame().getPhase() > 0) {
      line3 = ChatColor.BOLD + Translator.getColoredString("INFO.NEXUS_HEALTH").replace("%HEALTH%", Integer.toString(t.getNexus().getHealth()));
    } else {
      line3 = " ";
    }

    String[] previous = lastLines.get(t);
    if (previous != null
        && previous[0].equals(line0)
        && previous[1].equals(line1)
        && previous[2].equals(line2)
        && previous[3].equals(line3)) {
      return; // No changes; skip block state writes.
    }

    for (Location l : teamSigns) {
      Block b = l.getBlock();
      if (b == null || !GameUtils.isWallSign(b)) {
        continue;
      }

      Sign s = (Sign) b.getState();
      s.setLine(0, line0);
      s.setLine(1, line1);
      s.setLine(2, line2);
      s.setLine(3, line3);
      s.update(true);
    }

    lastLines.put(t, new String[]{line0, line1, line2, line3});
  }
}

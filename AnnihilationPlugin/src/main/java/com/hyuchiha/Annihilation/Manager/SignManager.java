package com.hyuchiha.Annihilation.Manager;

import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.HashMap;


public class SignManager {
  private static final Main plugin = Main.getInstance();
  private static final HashMap<GameTeam, ArrayList<Location>> signs = new HashMap<>();

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
    Material m = b.getType();
    if (m == Material.SIGN_POST || m == Material.WALL_SIGN) {
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

    for (Location l : signs.get(t)) {
      Block b = l.getBlock();
      if (b == null) {
        return;
      }

      Material m = b.getType();
      if (m == Material.SIGN_POST || m == Material.WALL_SIGN) {
        Sign s = (Sign) b.getState();
        s.setLine(0, ChatColor.DARK_PURPLE + "[" + Translator.getString("TEAM") + ChatColor.DARK_PURPLE + "]");
        s.setLine(1, t.coloredName());
        s.setLine(2, ChatColor.UNDERLINE.toString() + t.getPlayers().size() + (
            (t.getPlayers().size() == 1) ? (" " + Translator.getString("PLAYER")) : (" " + Translator.getString("PLAYER") + "s")));

        if (t.getNexus() != null && GameManager.getCurrentGame().getPhase() > 0) {
          s.setLine(3, ChatColor.BOLD.toString() + Translator.getColoredString("INFO_NEXUS_HEALTH") + t
                                                                                                          .getNexus().getHealth());
        } else {
          s.setLine(3, " ");
        }

        s.update(true);
      }
    }
  }
}

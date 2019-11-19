package com.hyuchiha.Annihilation.Game;

import com.hyuchiha.Annihilation.Arena.Nexus;
import com.hyuchiha.Annihilation.Manager.MapManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum GameTeam {
  RED, YELLOW, GREEN, BLUE, NONE;

  private final String name;
  private final ChatColor color;
  private Nexus nexus;

  private List<Location> spawns;

  GameTeam() {
    switch (name()) {
      case "RED":
        this.color = ChatColor.RED;
        this.name = Translator.getString(name().toUpperCase());
        break;
      case "YELLOW":
        this.color = ChatColor.YELLOW;
        this.name = Translator.getString(name().toUpperCase());
        break;
      case "GREEN":
        this.color = ChatColor.GREEN;
        this.name = Translator.getString(name().toUpperCase());
        break;
      case "BLUE":
        this.color = ChatColor.BLUE;
        this.name = Translator.getString(name().toUpperCase());
        break;
      default:
        this.color = ChatColor.LIGHT_PURPLE;
        this.name = "None";
        break;
    }

    this.spawns = new ArrayList<>();
  }

  public static GameTeam getTeamByTranslatedName(String name) {
    for (GameTeam team : teams()) {
      if (team.getName().equalsIgnoreCase(name)) {
        return team;
      }
    }

    return null;
  }

  public static GameTeam[] teams() {
    return new GameTeam[]{RED, GREEN, YELLOW, BLUE};
  }

  public static GameTeam getTeam(String s) {
    return valueOf(s.toUpperCase());
  }

  public static String getNameChar(GameTeam gt) {
    switch (gt) {
      case RED:
        return "§c";
      case BLUE:
        return "§9";
      case GREEN:
        return "§a";
      case YELLOW:
        return "§e";
    }
    return "§f";
  }

  public static boolean isNull(GameTeam t) {
    return (t == null || t == NONE);
  }

  public String toString() {
    return this.name.substring(0, 1) + this.name.substring(1).toLowerCase();
  }

  public void restartSpawns() {
    this.spawns.clear();
    this.spawns = new ArrayList<>();
  }

  public String coloredName() {
    return color() + toString();
  }

  public ChatColor color() {
    return this.color;
  }

  public void addSpawn(Location loc) {
    if (this != NONE) {
      this.spawns.add(loc);
    }
  }

  public Nexus getNexus() {
    return this.nexus;
  }

  public void loadNexus(Location location, int health) {
    if (this != NONE) {
      this.nexus = new Nexus(this, location, health);
    }
  }

  public Location getRandomSpawn() {
    if (!this.spawns.isEmpty() && this != NONE) {
      Location l = this.spawns.get((new Random()).nextInt(this.spawns.size()));
      return new Location(MapManager.getCurrentMap().getWorld(), l.getX(), l.getY(), l.getZ());
    }

    return MapManager.getLobbySpawn();
  }

  public List<Location> getSpawns() {
    return Collections.unmodifiableList(this.spawns);
  }

  public List<Player> getPlayers() {
    List<Player> players = new ArrayList<>();
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (PlayerManager.getGamePlayer(p).getTeam() == this && this != NONE) {
        players.add(p);
      }
    }
    return players;
  }

  public int getPlayersAlive() {
    List<Player> players = new ArrayList<>();
    for (Player player : getPlayers()) {
      GamePlayer gm = PlayerManager.getGamePlayer(player);
      if (gm.isAlive()) {
        players.add(player);
      }
    }

    return players.size();
  }

  public boolean isTeamAlive() {
    return getNexus().isAlive();
  }

  public Color getColor() {
    switch (this) {
      case RED:
        return Color.RED;
      case BLUE:
        return Color.BLUE;
      case GREEN:
        return Color.GREEN;
      case YELLOW:
        return Color.YELLOW;
    }
    return Color.WHITE;
  }

  public DyeColor getDyeColor() {
    switch (this) {
      case RED:
        return DyeColor.RED;
      case BLUE:
        return DyeColor.BLUE;
      case GREEN:
        return DyeColor.GREEN;
      case YELLOW:
        return DyeColor.YELLOW;
    }
    return DyeColor.WHITE;
  }

  public ChatColor getChatColor() {
    switch (this) {
      case RED:
        return ChatColor.RED;
      case BLUE:
        return ChatColor.BLUE;
      case GREEN:
        return ChatColor.GREEN;
      case YELLOW:
        return ChatColor.YELLOW;
    }
    return ChatColor.WHITE;
  }

  public static GameTeam getTeamChar(String s) {
    if (s == null) {
      return GameTeam.NONE;
    }
    if (s.contains("§c")) {
      return GameTeam.RED;
    }
    if (s.contains("§9")) {
      return GameTeam.BLUE;
    }
    if (s.contains("§e")) {
      return GameTeam.YELLOW;
    }
    if (s.contains("§a")) {
      return GameTeam.GREEN;
    }
    return GameTeam.NONE;
  }

  public String getName() {
    return (this.name != null) ? this.name.toLowerCase() : "none";
  }
}

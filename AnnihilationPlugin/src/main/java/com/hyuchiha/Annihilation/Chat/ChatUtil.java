package com.hyuchiha.Annihilation.Chat;

import com.hyuchiha.Annihilation.Game.GameBoss;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Hooks.VaultHooks;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;


public class ChatUtil {
  private static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
  private static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
  private static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
  private static final String DARK_RED = ChatColor.DARK_RED.toString();
  private static final String RESET = ChatColor.RESET.toString();
  private static final String GRAY = ChatColor.GRAY.toString();
  private static final String AQUA = ChatColor.AQUA.toString();
  private static final String GOLD = ChatColor.GOLD.toString();
  private static final String RED = ChatColor.RED.toString();
  private static final String BOLD = ChatColor.BOLD.toString();


  public static String allMessage(GameTeam team, Player sender, boolean dead) {
    String s, playerName = sender.getName();

    World w = sender.getWorld();
    if (team == GameTeam.NONE) {
      String group = DARK_GRAY + "[" + DARK_PURPLE + "Lobby" + DARK_GRAY + "] ";
      String primaryGroup0 = VaultHooks.getPermissionManager().getPrimaryGroup(sender);
      String gprefix = VaultHooks.getChatManager().getGroupPrefix(w, primaryGroup0);
      s = group + fixDefault(ChatColor.translateAlternateColorCodes('&', gprefix)) + " " + playerName + DARK_AQUA + "" + BOLD + " > " + RESET;
    } else {
      String group = DARK_GRAY + "[" + team.color() + "Global" + DARK_GRAY + "] ";
      String primaryGroup = VaultHooks.getPermissionManager().getPrimaryGroup(sender);
      String gprefix = VaultHooks.getChatManager().getGroupPrefix(w, primaryGroup);
      s = group + fixDefault(ChatColor.translateAlternateColorCodes('&', gprefix)) + team.getChatColor() + " " + playerName + DARK_AQUA + "" + BOLD + " > " + RESET;
      if (dead) {
        group = DARK_GRAY + "[" + DARK_RED + Translator.getString("COMMONS.DEAD").toUpperCase() + DARK_GRAY + "] " + group;

        String primaryGroup2 = VaultHooks.getPermissionManager().getPrimaryGroup(sender);
        gprefix = VaultHooks.getChatManager().getGroupPrefix(w, primaryGroup2);
        s = group + fixDefault(ChatColor.translateAlternateColorCodes('&', gprefix)) + team.getChatColor() + " " + playerName + DARK_AQUA + "" + BOLD + " > " + RESET;
      }
    }

    return s;
  }


  public static String teamMessage(GameTeam team, Player sender, boolean dead) {
    String playerName = sender.getName();
    World w = sender.getWorld();
    if (team == GameTeam.NONE) {
      return allMessage(team, sender, false);
    }
    String group = GRAY + "[" + team.color() + Translator.getString("COMMONS.TEAM") + GRAY + "] ";
    String primaryGroup0 = VaultHooks.getPermissionManager().getPrimaryGroup(Bukkit.getPlayer(playerName));
    String gprefix = VaultHooks.getChatManager().getGroupPrefix(w, primaryGroup0);
    String s = group + fixDefault(ChatColor.translateAlternateColorCodes('&', gprefix)) + team.getChatColor() + " " + playerName + DARK_AQUA + "" + BOLD + " > " + RESET;
    if (dead) {
      group = DARK_GRAY + "[" + DARK_RED + Translator.getString("COMMONS.DEAD") + DARK_GRAY + "] " + group;
      String primaryGroup2 = VaultHooks.getPermissionManager().getPrimaryGroup(Bukkit.getPlayer(playerName));
      gprefix = VaultHooks.getChatManager().getGroupPrefix(w, primaryGroup2);
      s = group + fixDefault(ChatColor.translateAlternateColorCodes('&', gprefix)) + team.getChatColor() + " " + playerName + DARK_AQUA + "" + BOLD + " > " + RESET;
    }

    return s;
  }

  private static String fixDefault(String s) {
    if (s.contains("default")) {
      s = "";
    }
    return s;
  }


  public static void broadcast(String message) {
    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
  }


  public static void nexusDestroyed(GameTeam attacker, GameTeam victim, Player p) {
    List<String> multiMessage = Translator.getMultiMessage("NEXUS_DESTROYED");

    for (String message : multiMessage) {
      String replacedMessage = message
          .replace("%C%", victim.color().toString())
          .replace("%TEAM%", victim.coloredName())
          .replace("%PLAYER%", attacker.color().toString() + p.getName());
      broadcast(replacedMessage);
    }
  }


  public static String nexusBreakMessage(Player breaker, GameTeam attacker, GameTeam victim) {
    return Translator.getColoredString("GAME.NEXUS_BREAK")
        .replace("%C%", attacker.color().toString())
        .replace("%PLAYER%", colorizeName(breaker, attacker))
        .replace("%TEAM%", victim.coloredName());
  }


  private static String colorizeName(Player player, GameTeam team) {
    return team.color() + player.getName();
  }


  public static void phaseMessage(int phase) {
    String currentPhase = "PHASE_" + phase;
    List<String> multiMessage = Translator.getMultiMessage(currentPhase);

    for (String message : multiMessage) {
      broadcast(message);
    }
  }

  public static void winMessage(GameTeam winner) {
    List<String> multiMessage = Translator.getMultiMessage("WIN_MESSAGE");

    for (String message : multiMessage) {


      String replacedMessage = message.replace("%TEAM%", winner.coloredName()).replace("%C%", winner.color().toString());
      broadcast(replacedMessage);
    }
  }

  public static void bossDeath(GameBoss boss, Player killer, GameTeam team) {
    List<String> multiMessage = Translator.getMultiMessage("BOSS_KILLED");

    for (String message : multiMessage) {


      String replacedMessage = message.replace("%BOSS%", boss.getBossName()).replace("%PLAYER%", colorizeName(killer, team));
      broadcast(replacedMessage);
    }
  }

  public static void bossRespawn(GameBoss boss) {
    List<String> multiMessage = Translator.getMultiMessage("BOSS_RESPAWN");

    for (String message : multiMessage) {

      String replacedMessage = message.replace("%BOSS%", boss.getBossName());
      broadcast(replacedMessage);
    }
  }


  public static String formatDeathMessage(Player victim, Player killer, String original) {
    GameTeam killerTeam = PlayerManager.getGamePlayer(killer).getTeam();

    String killerColor = (killerTeam != null) ? killerTeam.color().toString() : ChatColor.DARK_PURPLE.toString();
    String killerName = killerColor + killer.getName() + ChatColor.GRAY;

    String message = ChatColor.GRAY + formatDeathMessage(victim, original);
    return message.replace(killer.getName(), killerName);
  }


  public static String formatDeathMessage(Player victim, String original) {
    GameTeam victimTeam = PlayerManager.getGamePlayer(victim).getTeam();

    String victimColor = (victimTeam != null) ? victimTeam.color().toString() : ChatColor.DARK_PURPLE.toString();
    String victimName = victimColor + victim.getName() + ChatColor.GRAY;

    String message = ChatColor.GRAY + original;
    message = message.replace(victim.getName(), victimName);

    if (message.contains(" §8§")) {
      String[] arr = message.split(" §8§");
      message = arr[0];
    }

    return message.replace("was slain by", Translator.getString("DEATHS.SLAIN_BY"));
  }


  public static String translateRoman(int number) {
    switch (number) {
      case 0:
        return "0";
      case 1:
        return "I";
      case 2:
        return "II";
      case 3:
        return "III";
      case 4:
        return "IV";
      case 5:
        return "V";
      case 6:
        return "VI";
      case 7:
        return "VII";
      case 8:
        return "VIII";
      case 9:
        return "IX";
      case 10:
        return "X";
    }
    return String.valueOf(number);
  }
}

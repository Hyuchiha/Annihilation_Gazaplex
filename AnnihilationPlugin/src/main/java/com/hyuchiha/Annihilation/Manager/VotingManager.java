package com.hyuchiha.Annihilation.Manager;

import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Scoreboard.ScoreboardManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;


public class VotingManager {
  private static final HashMap<Integer, String> maps = new HashMap<>();
  private static final HashMap<String, String> votes = new HashMap<>();
  private static boolean running = false;

  public static void start() {
    votes.clear();
    maps.clear();

    int count = 0;
    for (String map : MapManager.getRandomMaps()) {
      maps.put(count, map);
      count++;
    }

    ScoreboardManager.createLobbyScoreboard();

    running = true;

    ScoreboardManager.updatePlayerScoreboard();
  }

  public static boolean vote(CommandSender voter, String vote) {
    if (StringUtils.isNumeric(vote)) {
      int val = Integer.parseInt(vote);

      if (maps.containsKey(val)) {
        vote = maps.get(val);
        if (sendVoteMessage(voter, vote)) {
          return true;
        }
      }

    } else if (sendVoteMessage(voter, vote)) {
      return true;
    }


    voter.sendMessage(Translator.getPrefix() + ChatColor.YELLOW + vote + ChatColor.RED +
                          Translator.getString("MAP_INVALID"));
    return false;
  }

  private static boolean sendVoteMessage(CommandSender voter, String vote) {
    for (String map : maps.values()) {
      if (vote.equalsIgnoreCase(map)) {
        votes.put(voter.getName(), map);
        voter.sendMessage(Translator.getPrefix() + Translator.getString("VOTE_MAP") + map);

        ScoreboardManager.updateLobbyScoreboard();

        return true;
      }
    }
    return false;
  }

  public static String getWinner() {
    String winner = null;
    int highest = -1;
    for (String map : maps.values()) {
      int totalVotes = countVotes(map);
      if (totalVotes > highest) {
        winner = map;
        highest = totalVotes;
      }
    }
    return winner;
  }


  public static void end() {
    running = false;
  }


  public static boolean isRunning() {
    return running;
  }


  public static HashMap<Integer, String> getMaps() {
    return maps;
  }


  public static int countVotes(String map) {
    int total = 0;
    for (String vote : votes.values()) {
      if (vote.equals(map))
        total++;
    }
    return total;
  }


  public static HashMap<String, String> getVotes() {
    return votes;
  }
}

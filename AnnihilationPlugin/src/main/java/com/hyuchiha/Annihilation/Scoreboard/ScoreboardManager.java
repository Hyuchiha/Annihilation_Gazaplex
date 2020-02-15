package com.hyuchiha.Annihilation.Scoreboard;

import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.VotingManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;

public class ScoreboardManager {
  private static Scoreboard scoreboardBase;
  private static Objective scoreObjective;
  private static HashMap<String, Score> scores = new HashMap<>();

  private static Objective teamObjective;
  private static HashMap<String, Team> teams = new HashMap<>();


  public static void initScoreboard() {
    resetScoreboard(Translator.getColoredString("SCOREBOARDS.SB_LOBBY_TITLE"));
  }

  public static void resetScoreboard(String scoreboardName) {
    scoreboardBase = null;

    for (Player p : Bukkit.getOnlinePlayers()) {
      p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    scoreboardBase = Bukkit.getScoreboardManager().getNewScoreboard();

    resetScores(scoreboardName);
    resetTeams();
  }

  public static void resetScores(String scoreboardName) {
    scores.clear();

    scoreObjective = scoreboardBase.registerNewObjective("anni", "dummy");
    scoreObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    scoreObjective.setDisplayName(scoreboardName);
  }

  public static void resetTeams() {
    teams.clear();

    teamObjective = scoreboardBase.registerNewObjective("teams", "dummy");
    teamObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

    for (GameTeam team : GameTeam.teams()) {
      setTeam(team);
    }
  }

  public static void createLobbyScoreboard() {
    int count = 0;
    int size = VotingManager.getMaps().size();
    for (String map : VotingManager.getMaps().values()) {
      count++;
      size--;

      scores.put(map, scoreObjective.getScore(map));
      scores.get(map).setScore(size);

      teams.put(map, scoreboardBase.registerNewTeam(map));
      teams.get(map).addEntry(map);
      teams.get(map).setPrefix(ChatColor.AQUA + "[" + count + "] " + ChatColor.GRAY);
      teams.get(map).setSuffix(ChatColor.RED + " » " + ChatColor.GREEN + "0 " + Translator.getString("COMMONS.VOTE") + "s");
    }

    scoreObjective.getScore(ChatColor.AQUA + "").setScore(-1);
    scoreObjective.getScore(Translator.getColoredString("SERVER_IP")).setScore(-2);
  }

  public static void updateLobbyScoreboard() {
    for (String map : VotingManager.getMaps().values()) {
      teams.get(map).setSuffix(ChatColor.RED + " » " + ChatColor.GREEN +
                                   VotingManager.countVotes(map) + " " +
                                   Translator.getString("COMMONS.VOTE") + ((VotingManager.countVotes(map) == 1) ? "" : "s"));
    }
  }

  public static void createInGameScoreboard() {
    scores.clear();

    for (String score : scoreboardBase.getEntries()) {
      scoreboardBase.resetScores(score);
    }

    scoreObjective.setDisplayName(Translator.getColoredString("SCOREBOARDS.SB_GAME_PREFIX") + " " +
                                     WordUtils.capitalize(VotingManager.getWinner()));

    for (GameTeam t : GameTeam.teams()) {
      scores.put(t.name(), scoreObjective.getScore(
          WordUtils.capitalize(Translator.getString("COMMONS.TEAM") + " " + t.getName())));

      scores.get(t.name()).setScore(t.getNexus().getHealth());

      Team sbt = scoreboardBase.registerNewTeam(t.name() + "SB");
      sbt.addEntry(
          WordUtils.capitalize(Translator.getString("COMMONS.TEAM") + " " + t.getName())
      );


      sbt.setPrefix(t.color().toString());
    }

    scoreObjective.getScore(ChatColor.AQUA + "").setScore(-1);
    scoreObjective.getScore(Translator.getColoredString("SERVER_IP")).setScore(-2);
  }

  public static void updateInGameScoreboard(final GameTeam victim) {
    scoreboardBase.getTeam(victim.name() + "SB").setPrefix(ChatColor.RESET.toString());
    scores.get(victim.name()).setScore(victim.getNexus().getHealth());
    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> scoreboardBase.getTeam(victim.name() + "SB")
                                                                     .setPrefix(victim.color().toString()), 2L);
  }


  public static void removeTeamScoreboard(GameTeam team) {
    scoreboardBase.resetScores(scores.remove(team.name()).getEntry());
  }


  public static void updatePlayerScoreboard() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      player.setScoreboard(scoreboardBase);
    }
  }

  public static void setTeam(GameTeam team) {
    teams.put(team.name(), scoreboardBase.registerNewTeam(team.name()));
    Team sbt = teams.get(team.name());
    sbt.setAllowFriendlyFire(false);
    sbt.setCanSeeFriendlyInvisibles(false);

    Configuration config = Main.getInstance().getConfig("config.yml");
    if (config.getBoolean("useTeamPrefix")) {
      String prefix = Translator.getColoredString("TEAMS_PREFIX." + team.name().toUpperCase());
      sbt.setPrefix(team.color().toString() + prefix + " ");
    }

    sbt.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
  }


  public static HashMap<String, Team> getTeams() {
    return teams;
  }
}

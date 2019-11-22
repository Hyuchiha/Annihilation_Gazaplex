package com.hyuchiha.Annihilation.Game;

import com.google.common.base.Enums;
import com.hyuchiha.Annihilation.Anticheat.FastBreakProtect;
import com.hyuchiha.Annihilation.Chat.ChatUtil;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.*;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Scoreboard.ScoreboardManager;
import com.hyuchiha.Annihilation.Serializers.PlayerSerializer;
import com.hyuchiha.Annihilation.Utils.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Game {
  private final HashMap<String, Block> crafting;
  private final HashMap<String, String> npcPlayers;

  private Main plugin;
  private GameTimer timer;
  private int phase;

  public Game(Main plugin) {
    this.plugin = plugin;
    Configuration config = plugin.getConfig("config.yml");
    this.timer = new GameTimer(plugin, config.getInt("start-delay"), config.getInt("phase-period"), config.getInt("restart-delay"));

    this.crafting = new HashMap<>();
    this.npcPlayers = new HashMap<>();
  }

  public void startPreGame() {
    if (!this.timer.isRunning()) {
      Output.log("Starting new pre game");
      this.timer.start();
    }
  }

  public void startGame() {
    Output.log("Starting the game");

    ScoreboardManager.createInGameScoreboard();
    ScoreboardManager.updatePlayerScoreboard();

    this.phase = 1;

    for (Player player : Bukkit.getOnlinePlayers()) {
      GamePlayer gp = PlayerManager.getGamePlayer(player);
      if (gp.getTeam() != GameTeam.NONE) {
        gp.preparePlayerForGame();
        continue;
      }
      GameTeam team = TeamUtils.getLowerTeam();
      joinTeam(player, team.getName());
      gp.preparePlayerForGame();
    }


    ChatUtil.phaseMessage(this.phase);
  }


  public void restartingTime() {
    this.timer.startResetTime(GameState.RESTARTING);
  }


  public void endGame() {
    Output.log("Ending game for map: " + MapManager.getCurrentMap().getName());

    for (Player player : Bukkit.getOnlinePlayers()) {
      GamePlayer gp = PlayerManager.getGamePlayer(player);
      gp.prepareLobbyPlayer();
    }

    ScoreboardManager.resetScoreboard(Translator.getColoredString("SB_LOBBY_TITLE"));
    ScoreboardManager.updatePlayerScoreboard();

    this.timer.stop();

    getNpcPlayers().clear();
    getCrafting().clear();

    MapManager.resetMap();
    EnderChestManager.clearTeamsEnderChests();
    EnderBrewingManager.clearTeamsEnderBrewings();
    EnderFurnaceManager.clearTeamsFurnaces();
    ResourceManager.clearDiamonds();
    BossManager.clearBossData();
    WitchManager.clearWitchData();
    FastBreakProtect.clearData();
    PlayerSerializer.restartDataOfPlayers();
  }


  public int getPhase() {
    return this.phase;
  }


  public void advancePhase() {
    this.phase++;
  }


  public GameTeam getWinner() {
    for (GameTeam team : GameTeam.teams()) {
      if (team.isTeamAlive()) {
        return team;
      }
    }

    return GameTeam.NONE;
  }

  public boolean canEndGame() {
    int teamsAlive = 0;
    for (GameTeam team : GameTeam.teams()) {
      if (team.isTeamAlive()) {
        teamsAlive++;
      }
    }

    return (teamsAlive <= 1 && getTimer().getGameState() != GameState.PHASE_1);
  }


  public boolean isInGame() {
    return this.timer.isGameStarted();
  }


  public void joinTeam(Player player, String team) {
    GamePlayer gamePlayer = PlayerManager.getGamePlayer(player);

    if (gamePlayer.getTeam() != GameTeam.NONE && !player.hasPermission("annihilation.bypass.team_limit")) {
      player.sendMessage(Translator.getPrefix() + ChatColor.GRAY + Translator.getString("ERROR_PLAYER_NOSWITCHTEAM"));
    } else {
      GameTeam toJoin = (GameTeam) Enums.getIfPresent(GameTeam.class, team.toUpperCase()).orNull();

      if (toJoin == null) {
        player.sendMessage(Translator.getPrefix() + ChatColor.RED + Translator.getString("ERROR_GAME_INVALIDTEAM"));
        TeamUtils.listTeams(player);

        return;
      }
      if (TeamUtils.getTeamAllowEnter(toJoin) && !player.hasPermission("annihilation.bypass.team_limit")) {
        player.sendMessage(Translator.getPrefix() + ChatColor.RED + Translator.getString("ERROR_GAME_TEAMFULL"));

        return;
      }
      player.sendMessage(Translator.getPrefix() + ChatColor.DARK_AQUA + Translator.getColoredString("JOINED_TEAM") + toJoin
                                                                                                                         .coloredName());
      gamePlayer.setTeam(toJoin);

      ScoreboardManager.getTeams().get(team.toUpperCase()).addPlayer(player);

      SignManager.updateSigns();
    }
  }


  public HashMap<String, Block> getCrafting() {
    return this.crafting;
  }


  public HashMap<String, String> getNpcPlayers() {
    return this.npcPlayers;
  }


  public GameTimer getTimer() {
    return this.timer;
  }
}

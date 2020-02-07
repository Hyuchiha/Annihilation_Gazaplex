package com.hyuchiha.Annihilation.Game;

import com.google.common.base.Enums;
import com.hyuchiha.Annihilation.Anticheat.FastBreakProtect;
import com.hyuchiha.Annihilation.Event.PhaseChangeEvent;
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
  private int phase = 0;

  public Game(Main plugin) {
    this.plugin = plugin;
    Configuration config = plugin.getConfig("config.yml");
    boolean forceEnd = config.getBoolean("ForceGameEnding", false);

    int startDelay = config.getInt("start-delay");
    int phasePeriod = config.getInt("phase-period");
    int restartDelay = config.getInt("restart-delay");

    if (forceEnd) {
      int hours = config.getInt("Force-end.hours");
      int minutes = config.getInt("Force-end.minutes");
      this.timer = new GameTimer(plugin, startDelay, phasePeriod, restartDelay, hours, minutes);
    } else {
      this.timer = new GameTimer(plugin, startDelay, phasePeriod, restartDelay);
    }

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

    Bukkit.getServer().getPluginManager().callEvent(new PhaseChangeEvent(0, 1));

    ScoreboardManager.createInGameScoreboard();
    ScoreboardManager.updatePlayerScoreboard();

    // this.phase = 1;

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

    ScoreboardManager.resetScoreboard(Translator.getColoredString("SCOREBOARDS.SB_LOBBY_TITLE"));
    ScoreboardManager.updatePlayerScoreboard();

    this.timer.stop();

    getNpcPlayers().clear();
    getCrafting().clear();

    EnderChestManager.clearTeamsEnderChests();
    EnderBrewingManager.clearTeamsEnderBrewings();
    EnderFurnaceManager.clearTeamsFurnaces();
    ResourceManager.clearDiamonds();
    BossManager.clearBossData();
    WitchManager.clearWitchData();
    FastBreakProtect.clearData();
    PlayerSerializer.restartDataOfPlayers();
    ZombieManager.clearZombiesData();
    ParticleManager.endGameParticles();
    MapManager.resetMap();
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

  public GameTeam getForcedWinner() {
    int rnex = 0, bnex = 0, gnex = 0, ynex = 0;

    for (GameTeam g : GameTeam.teams()) {

      switch (g) {
        case BLUE:
          bnex = g.getNexus().getHealth();
          break;
        case GREEN:
          gnex = g.getNexus().getHealth();
          break;

        case RED:
          rnex = g.getNexus().getHealth();
          break;

        case YELLOW:
          ynex = g.getNexus().getHealth();
          break;
      }
    }

    GameTeam gt;
    if (rnex > bnex && rnex > gnex && rnex > ynex) {
      gt = GameTeam.RED;
    } else if (bnex > rnex && bnex > gnex && bnex > ynex) {
      gt = GameTeam.BLUE;
    } else if (gnex > bnex && gnex > rnex && gnex > ynex) {
      gt = GameTeam.GREEN;
    } else if (ynex > bnex && ynex > gnex && ynex > rnex) {
      gt = GameTeam.YELLOW;
    } else {
      gt = GameTeam.NONE;
    }

    return gt;
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
      player.sendMessage(Translator.getPrefix() + ChatColor.GRAY + Translator.getString("ERROR.PLAYER_NOSWITCHTEAM"));
    } else {
      GameTeam toJoin = (GameTeam) Enums.getIfPresent(GameTeam.class, team.toUpperCase()).orNull();

      if (toJoin == null) {
        player.sendMessage(Translator.getPrefix() + ChatColor.RED + Translator.getString("ERRORS.GAME_INVALIDTEAM"));
        TeamUtils.listTeams(player);

        return;
      }
      if (TeamUtils.getTeamAllowEnter(toJoin) && !player.hasPermission("annihilation.bypass.team_limit")) {
        player.sendMessage(Translator.getPrefix() + ChatColor.RED + Translator.getString("ERRORS.GAME_TEAMFULL"));

        return;
      }
      player.sendMessage(Translator.getPrefix() + ChatColor.DARK_AQUA + Translator.getColoredString("GAME.JOINED_TEAM") + toJoin
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

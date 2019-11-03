package com.hyuchiha.Annihilation.Game;

import com.hyuchiha.Annihilation.BossBar.BossBarAPI;
import com.hyuchiha.Annihilation.Chat.ChatUtil;
import com.hyuchiha.Annihilation.Event.EndGameEvent;
import com.hyuchiha.Annihilation.Event.PhaseChangeEvent;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.MapManager;
import com.hyuchiha.Annihilation.Manager.SignManager;
import com.hyuchiha.Annihilation.Manager.VotingManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Utils.FireworkUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class GameTimer {
  private final long startTime;
  private final long phaseTime;
  private final long restartingTime;

  private long time;
  private boolean isRunning;
  private boolean gameStarted;

  private Main plugin;
  private GameState state;

  private int taskID;
  private int fwID;

  public GameTimer(Main plugin, int start, int phaseTime, int restartingTime) {
    this.gameStarted = false;


    this.plugin = plugin;

    this.state = GameState.WAITING;
    this.startTime = start;
    this.phaseTime = phaseTime;
    this.restartingTime = restartingTime;
  }

  public static String timeString(long time) {
    long hours = time / 3600L;
    long minutes = (time - hours * 3600L) / 60L;
    long seconds = time - hours * 3600L - minutes * 60L;
    return String.format(ChatColor.WHITE + "%02d" + ChatColor.GRAY + ":" + ChatColor.WHITE + "%02d", minutes, seconds).replace("-", "");
  }

  public void start() {
    if (!this.isRunning) {
      BukkitScheduler scheduler = this.plugin.getServer().getScheduler();
      this.taskID = scheduler.scheduleSyncRepeatingTask(this.plugin, this::onSecond, 20L, 20L);
      this.isRunning = true;
    }

    this.state = GameState.STARTING;
    this.time = -this.startTime;
    String text = parseRemainingTime();
    sendRemainingTime(text, 1.0F);

    SignManager.updateSigns();
  }

  public void stop() {
    if (this.isRunning) {
      this.isRunning = false;
      this.time = -this.startTime;
      Bukkit.getServer().getScheduler().cancelTask(this.taskID);
      Bukkit.getServer().getScheduler().cancelTask(this.fwID);
    }
  }

  public void startResetTime(GameState state) {
    this.state = state;
    if (state == GameState.RESTARTING) {
      this.time = -this.restartingTime;

      this.fwID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
        Color color = GameManager.getCurrentGame().getWinner().getColor();

        for (GameTeam gt : GameTeam.values()) {
          if (gt != GameTeam.NONE) {
            for (Location l : gt.getSpawns()) {
              FireworkUtils.spawnFirework(l, color, color);
            }
          }
        }
      }, 0L, 40L);
    }
  }


  public boolean isGameStarted() {
    return this.gameStarted;
  }


  public long getTime() {
    return this.time;
  }


  private long getRemainingTime() {
    if (this.state == GameState.PHASE_5) {
      return 1L;
    }

    return (this.time != 0L) ? (this.time % this.phaseTime) : 1L;
  }


  public boolean isRunning() {
    return this.isRunning;
  }


  private void onSecond() {
    this.time++;

    float percent = 1.0F;
    String text = "";

    switch (this.state) {
      case STARTING:
        percent = (float) -this.time / (float) this.startTime;
        text = parseRemainingTime();
        canSelectMap();
        break;
      case PHASE_1:
        text = parseRemainingTime();
        percent = 1.0F - (float) getRemainingTime() / (float) this.phaseTime;

        if (getRemainingTime() == 0L) {

          Bukkit.getServer().getPluginManager().callEvent(new PhaseChangeEvent(1, 2));
          this.state = GameState.PHASE_2;
        }
        break;
      case PHASE_2:
        text = parseRemainingTime();
        percent = 1.0F - (float) getRemainingTime() / (float) this.phaseTime;

        if (getRemainingTime() == 0L) {
          Bukkit.getServer().getPluginManager().callEvent(new PhaseChangeEvent(2, 3));
          this.state = GameState.PHASE_3;
        }
        break;
      case PHASE_3:
        text = parseRemainingTime();
        percent = 1.0F - (float) getRemainingTime() / (float) this.phaseTime;

        if (getRemainingTime() == 0L) {
          Bukkit.getServer().getPluginManager().callEvent(new PhaseChangeEvent(3, 4));
          this.state = GameState.PHASE_4;
        }
        break;
      case PHASE_4:
        text = parseRemainingTime();
        percent = 1.0F - (float) getRemainingTime() / (float) this.phaseTime;

        if (getRemainingTime() == 0L) {
          Bukkit.getServer().getPluginManager().callEvent(new PhaseChangeEvent(4, 5));
          this.state = GameState.PHASE_5;
        }
        break;
      case PHASE_5:
        text = parseRemainingTime();
        break;
      case RESTARTING:
        percent = (float) -this.time / (float) this.restartingTime;
        text = parseRemainingTime();


        if (this.time == 0L) {

          Bukkit.getServer().getPluginManager().callEvent(new EndGameEvent());
        }
        break;
    }


    SignManager.updateSigns();

    sendRemainingTime(text, percent);
  }

  private String parseRemainingTime() {
    int phase;
    String text = "";
    switch (this.state) {
      case STARTING:
        text = Translator.getColoredString("LOBBY");
        text = text + " &7| &f" + Translator.getColoredString("TIME_TO_START") + -this.time;
        text = ChatColor.translateAlternateColorCodes('&', text);
        break;
      case PHASE_1:
      case PHASE_2:
      case PHASE_3:
      case PHASE_4:
      case PHASE_5:
        phase = GameManager.getCurrentGame().getPhase();
        text = Translator.getColoredString("PHASE");
        text = text.replaceAll("%PHASE%", ChatUtil.translateRoman(phase));
        text = text + " &7| &f" + timeString(this.time);
        text = ChatColor.translateAlternateColorCodes('&', text);
        break;
      case RESTARTING:
        text = Translator.getColoredString("RESTARTING");
        text = text + " &7| &f" + timeString(-this.time);
        text = ChatColor.translateAlternateColorCodes('&', text);
        break;
    }


    return text;
  }


  private void sendRemainingTime(String message, float percent) {
    for (Player p : Bukkit.getOnlinePlayers()) {
      BossBarAPI.setMessage(p, message, percent);
    }
  }


  private void canSelectMap() {
    long time = getTime();

    if (time == -10L) {
      String winner = VotingManager.getWinner();
      MapManager.selectMap(winner);
      this.plugin.getServer().broadcastMessage(Translator.getPrefix() + ChatColor.AQUA +
                                                   WordUtils.capitalize(winner) + Translator.getColoredString("ARENA_WAS_CHOOSEN"));

      VotingManager.end();
    }

    if (time == 0L) {
      this.gameStarted = true;
      this.state = GameState.PHASE_1;
      GameManager.getCurrentGame().startGame();
    }
  }


  public GameState getGameState() {
    return this.state;
  }
}

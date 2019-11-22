package com.hyuchiha.Annihilation.Manager;

import com.hyuchiha.Annihilation.Chat.ChatUtil;
import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Game.Game;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class GameManager {
  private static Game currentGame = null;

  public static boolean canStartGame() {
    int requiredToStart = Main.getInstance().getConfig("config.yml").getInt("requiredToStart");

    if (currentGame != null && currentGame.isInGame()) {
      return false;
    }

    return (Bukkit.getOnlinePlayers().size() >= requiredToStart);
  }

  public static void initGameManager() {
    if (currentGame == null) {
      Output.log("Init Game and configs for games");
      currentGame = new Game(Main.getInstance());
    }
  }


  public static void startNewGame() {
    currentGame.startPreGame();
  }


  public static void endCurrentGame() {
    if (currentGame != null) {
      currentGame.endGame();
      currentGame = new Game(Main.getInstance());
    }
  }

  public static void canEndGame() {
    if (currentGame.canEndGame()) {
      currentGame.restartingTime();

      for (Player player : Bukkit.getOnlinePlayers()) {
        if (PlayerManager.getGamePlayer(player).getTeam() == currentGame.getWinner()) {
          Account winnerData = Main.getInstance().getMainDatabase().getAccount(player.getUniqueId().toString(), player.getName());
          winnerData.increaseNexusDamage();
        }
      }


      ChatUtil.winMessage(currentGame.getWinner());
    }
  }


  public static void forceStopGame() {
    if (currentGame != null) {
      currentGame.endGame();
      currentGame = new Game(Main.getInstance());
    }
  }


  public static Game getCurrentGame() {
    return currentGame;
  }

  public static boolean hasCurrentGame() {
    return currentGame != null;
  }
}

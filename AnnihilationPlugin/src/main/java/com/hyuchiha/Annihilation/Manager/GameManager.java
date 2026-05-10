package com.hyuchiha.Annihilation.Manager;

import com.hyuchiha.Annihilation.Chat.ChatUtil;
import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Game.Game;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class GameManager {
  private static volatile Game currentGame = null;

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

      Main main = Main.getInstance();
      GameTeam winner = currentGame.getWinner();

      for (Player player : Bukkit.getOnlinePlayers()) {
        Account account;
        if (PlayerManager.getGamePlayer(player).getTeam() == winner) {
          account = main.getMainDatabase().getAccount(player.getUniqueId().toString(), player.getName());
          if (account != null) {
            account.increaseWins();
          }
        } else {
          account = main.getMainDatabase().getAccount(player.getUniqueId().toString(), player.getName());
        }

        if (account != null) {
          final Account toSave = account;
          // Push DB write off the main thread; the account object stays usable post-cache-eviction.
          Bukkit.getScheduler().runTaskAsynchronously(main, () -> main.getMainDatabase().saveAccount(toSave));
        }
      }


      ChatUtil.winMessage(winner);
    }
  }


  public static void forceStopGame() {
    Output.log("Force stop game");
    if (currentGame != null && currentGame.getPhase() > 0) {
      GameTeam winner = currentGame.getForcedWinner();
      if (winner != GameTeam.NONE) {
        for (Player player : Bukkit.getOnlinePlayers()) {
          if (PlayerManager.getGamePlayer(player).getTeam() == winner) {
            Account winnerData = Main.getInstance().getMainDatabase().getAccount(player.getUniqueId().toString(), player.getName());
            winnerData.increaseNexusDamage();
          }
        }
      }

      ChatUtil.winMessage(currentGame.getWinner());

      endCurrentGame();
    }
  }

  public static Game getCurrentGame() {
    return currentGame;
  }

  public static boolean hasCurrentGame() {
    return currentGame != null;
  }
}

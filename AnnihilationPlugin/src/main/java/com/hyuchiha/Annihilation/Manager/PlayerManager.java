package com.hyuchiha.Annihilation.Manager;

import com.hyuchiha.Annihilation.Base.*;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Hooks.VaultHooks;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import com.hyuchiha.Annihilation.Utils.PermissionUtils;
import org.bukkit.entity.Player;
import org.inventivetalent.reflection.minecraft.Minecraft;

import java.util.HashMap;

public class PlayerManager {
  private static final HashMap<String, GamePlayer> players = new HashMap<>();
  private static Respawner respawner;

  public static void fetchRespawner() {
    Output.log("Getting respawner for the server version");

    switch (Minecraft.Version.getVersion()) {
      case v1_9_R1:
        respawner = new Respawner_v1_9_R1();
        return;
      case v1_9_R2:
        respawner = new Respawner_v1_9_R2();
        return;
      case v1_10_R1:
        respawner = new Respawner_v1_10_R1();
        return;
      case v1_11_R1:
        respawner = new Respawner_v1_11_R1();
        return;
      case v1_12_R1:
        respawner = new Respawner_v1_12_R1();
        return;
      case v1_13_R1:
        respawner = new Respawner_v1_13_R1();
        return;
      case v1_13_R2:
        respawner = new Respawner_v1_13_R2();
        return;
      case v1_14_R1:
        respawner = new Respawner_v1_14_R1();
        return;
      case v1_15_R1:
        respawner = new Respawner_v1_15_R1();
        return;
      case v1_16_R1:
        respawner = new Respawner_v1_16_R1();
        break;
      case v1_16_R2:
        respawner = new Respawner_v1_16_R2();
        break;
      case v1_16_R3:
        respawner = new Respawner_v1_16_R3();
        break;
      case v1_17_R1:
        respawner = new Respawner_v1_17_R1();
        break;
      case v1_18_R1:
        respawner = new Respawner_v1_18_R1();
        break;
      case v1_18_R2:
        respawner = new Respawner_v1_18_R2();
        break;
      case v1_19_R1:
        respawner = new Respawner_v1_19_R1();
        break;
      case v1_19_R2:
        respawner = new Respawner_v1_19_R2();
        break;
      case v1_19_R3:
        respawner = new Respawner_v1_19_R3();
        break;
      case v1_20_R1:
        respawner = new Respawner_v1_20_R1();
        break;
      case v1_20_R2:
        respawner = new Respawner_v1_20_R2();
        break;
      case v1_20_R3:
        respawner = new Respawner_v1_20_R3();
        break;
      case v1_21_R1:
        respawner = new Respawner_v1_21_R1();
        break;
      case v1_21_R2:
        respawner = new Respawner_v1_21_R2();
        break;
      case v1_21_R3:
        respawner = new Respawner_v1_21_R3();
        break;
      default:
        Output.logError("Version not supported");
        Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
    }
  }


  public static GamePlayer getGamePlayer(Player player) {
    GamePlayer gamePlayer = players.get(player.getUniqueId().toString());
    if (gamePlayer != null) {
      return gamePlayer;
    }

    return createPlayerData(player);
  }

  public static GamePlayer getGamePlayerByUUID(String uuid) {
    GamePlayer gamePlayer = players.get(uuid);
    if (gamePlayer != null) {
      return gamePlayer;
    }

    return null;
  }

  private static GamePlayer createPlayerData(Player player) {
    GamePlayer actualPlayer = new GamePlayer(player.getUniqueId());
    players.put(player.getUniqueId().toString(), actualPlayer);
    return actualPlayer;
  }


  public static void removePlayer(Player player) {
    players.remove(player.getUniqueId().toString());
  }


  public static void respawnPlayer(Player player) {
    if (respawner != null) {
      Output.log("Respawning without spigot");
      respawner.respawn(player);
    } else {
      Output.log("Respawning using spigot api");
      player.spigot().respawn();
    }
  }

  public static double calculateVipMoneyGive(Player player, double money) {
    if (player == null) {
      return 0;
    }

    double moneyToGive = money;

    if (GameUtils.isVip(player)) {
      if (PermissionUtils.hasPermission(player, "annihilation.vip.diamond")) {
        moneyToGive = money * 5.0D;
      }

      if (PermissionUtils.hasPermission(player, "annihilation.vip.gold")) {
        moneyToGive = money * 3.0D;
      }

      if (PermissionUtils.hasPermission(player, "annihilation.vip.iron")) {
        moneyToGive = money * 2.0D;
      }
    }

    if (Main.getInstance().getConfig("config.yml").getBoolean("showMoneyEarn")) {
      player.sendMessage(Translator.getPrefix() + Translator.getColoredString("GAME.PLAYER_MONEY_GRANT")
              .replace("%MONEY%", Double.toString(moneyToGive)));
    }

    return moneyToGive;
  }

  public static void addMoney(Player p, double money) {
    if (!VaultHooks.vault) {
      return;
    }

    if (!VaultHooks.getEconomyManager().hasAccount(p)) {
      VaultHooks.getEconomyManager().createPlayerAccount(p);
    }

    VaultHooks.getEconomyManager().depositPlayer(p, money);
  }


  public static double getMoney(Player p) {
    if (!VaultHooks.vault) {
      return 0.0D;
    }

    if (!VaultHooks.getEconomyManager().hasAccount(p)) {
      return 0.0D;
    }

    return VaultHooks.getEconomyManager().getBalance(p);
  }


  public static boolean withdrawMoney(Player p, double money) {
    if (VaultHooks.vault &&
        VaultHooks.getEconomyManager().has(p, money)) {
      VaultHooks.getEconomyManager().withdrawPlayer(p, money);
      return true;
    }


    p.sendMessage(Translator.getPrefix() + Translator.getColoredString("GAME.PLAYER_DONT_HAVE_REQUIRED_MONEY"));
    return false;
  }
}

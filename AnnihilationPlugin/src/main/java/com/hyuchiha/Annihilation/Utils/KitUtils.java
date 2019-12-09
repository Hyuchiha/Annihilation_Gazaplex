package com.hyuchiha.Annihilation.Utils;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitUtils {

  public static void selectKit(String className, Player player) {
    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

    Kit selectedKit = Kit.valueOf(ChatColor.stripColor(className).toUpperCase());

    if (!selectedKit.isOwnedBy(player)) {
      player.sendMessage(ChatColor.RED + Translator.getColoredString("KIT_NOT_PURCHASED"));
      return;
    }

    boolean isLobby = player.getWorld() == Bukkit.getServer().getWorld("lobby");
    gPlayer.getKit().getKit().removePlayer(player);

    gPlayer.setKit(selectedKit);
    String message = Translator.getColoredString("KIT_SELECTED").replace("%KIT%", ChatColor.stripColor(className));
    player.sendMessage(ChatColor.DARK_AQUA + message);

    if (!isLobby) {
      player.setAllowFlight(false);
      player.setFlying(false);
      gPlayer.regamePlayer();
    }
  }
}

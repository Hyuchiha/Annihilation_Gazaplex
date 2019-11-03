package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Database.Base.Database;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Manager.SignManager;
import com.hyuchiha.Annihilation.Serializers.PlayerSerializer;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
  private final Main plugin;

  public QuitListener(Main plugin) {
    this.plugin = plugin;
  }


  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    Player player = e.getPlayer();
    if (player == null) {
      return;
    }

    SignManager.updateSigns();

    GamePlayer gamePlayer = PlayerManager.getGamePlayer(player);
    String playerName = player.getName();
    e.setQuitMessage("");

    if (gamePlayer.getTeam() == GameTeam.NONE) {
      PlayerSerializer.delete(playerName);

      return;
    }
    if (player.getLocation().getY() <= 0.0D || GameUtils.isFallingToVoid(player)) {
      PlayerSerializer.removeItems(playerName);

      return;
    }

    Database database = this.plugin.getMainDatabase();
    Account account = database.getAccount(player.getUniqueId().toString(), player.getName());

    if (account != null) {
      database.saveAccount(account);
      database.removeCachedAccount(account);
    }

    PlayerSerializer.SerializePlayer(player);
  }


  @EventHandler
  public void onKick(PlayerKickEvent e) {
    Player player = e.getPlayer();


    Database database = this.plugin.getMainDatabase();
    Account account = database.getAccount(player.getUniqueId().toString(), player.getName());

    if (account != null) {
      database.saveAccount(account);
      database.removeCachedAccount(account);
    }
  }
}

package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Anticheat.FastBreakProtect;
import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Database.Base.Database;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Manager.SignManager;
import com.hyuchiha.Annihilation.Manager.ZombieManager;
import com.hyuchiha.Annihilation.Serializers.PlayerSerializer;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import com.hyuchiha.Annihilation.Utils.TimersUtils;
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

    e.setQuitMessage("");
    handleDisconnect(player);
  }


  @EventHandler
  public void onKick(PlayerKickEvent e) {
    Player player = e.getPlayer();
    if (player == null) {
      return;
    }

    handleDisconnect(player);
  }

  private void handleDisconnect(Player player) {
    GamePlayer gamePlayer = PlayerManager.getGamePlayer(player);
    String playerName = player.getName();
    GameTeam playerTeam = gamePlayer.getTeam();

    Database database = this.plugin.getMainDatabase();
    Account account = database.getAccount(player.getUniqueId().toString(), playerName);

    if (account != null) {
      // Sync save: a single per-disconnect save is cheap on the Hikari pool, and keeping
      // it synchronous guarantees the data is persisted before Bukkit cancels any pending
      // async tasks during plugin shutdown.
      database.saveAccount(account);
      database.removeCachedAccount(account);
    }

    // Kit cooldown cache and anticheat — safe to clear immediately
    TimersUtils.clearPlayer(player);
    FastBreakProtect.clearPlayer(player);

    if (playerTeam == GameTeam.NONE) {
      PlayerSerializer.delete(playerName);
      PlayerManager.removePlayer(player);
      SignManager.updateIndividualSign(playerTeam);
      return;
    }
    if (player.getLocation().getY() <= 0.0D || GameUtils.isFallingToVoid(player)) {
      PlayerSerializer.removeItems(playerName);
      playerTeam.removeMember(player.getUniqueId());
      PlayerManager.removePlayer(player);
      SignManager.updateIndividualSign(playerTeam);
      return;
    }

    PlayerSerializer.SerializePlayer(player);

    if (GameManager.getCurrentGame() != null && GameManager.getCurrentGame().getPhase() > 0 && playerTeam != GameTeam.NONE) {
      // createZombiePlayer reads the GamePlayer internally, so remove AFTER zombie creation
      ZombieManager.createZombiePlayer(player);
    }

    playerTeam.removeMember(player.getUniqueId());
    PlayerManager.removePlayer(player);
    SignManager.updateIndividualSign(playerTeam);
  }
}

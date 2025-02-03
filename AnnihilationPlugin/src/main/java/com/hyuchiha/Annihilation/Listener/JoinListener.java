package com.hyuchiha.Annihilation.Listener;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import com.hyuchiha.Annihilation.BossBar.BossBarAPI;
import com.hyuchiha.Annihilation.Event.StartGameEvent;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Manager.SignManager;
import com.hyuchiha.Annihilation.Manager.ZombieManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Scoreboard.ScoreboardManager;
import com.hyuchiha.Annihilation.Serializers.PlayerSerializer;
import com.hyuchiha.Annihilation.Utils.PermissionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;


public class JoinListener implements Listener {

  private final Main plugin;

  public JoinListener(Main plugin) {
    this.plugin = plugin;
  }


  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    event.setJoinMessage("");
    final Player player = event.getPlayer();

    GameManager.getCurrentGame().getNpcPlayers().remove(player.getName());

    player.sendMessage(Translator.getPrefix() + Translator.getColoredString("GAME.PLAYER_JOIN_MESSAGE"));

    Titles.sendTitle(player,
        10, 30, 10,
        Translator.getColoredString("TITLES.SERVER_JOIN_TITLE"),
        Translator.getColoredString("TITLES.SERVER_JOIN_SUBTITLE")
    );

    GamePlayer gamePlayer = PlayerManager.getGamePlayer(player);
    this.plugin.getMainDatabase().createAccount(player.getUniqueId().toString(), player.getName());


    if (GameManager.getCurrentGame().getPhase() == 0) {
      BossBarAPI.setMessage(player, Translator.getColoredString("BOSSBAR.WELCOME_TO_ANNIHILATION"), 1.0F);
    }

    gamePlayer.prepareLobbyPlayer();

    SignManager.updateIndividualSign(gamePlayer.getTeam());


    if (GameManager.canStartGame()) {
      Bukkit.getServer().getPluginManager().callEvent(new StartGameEvent());
    }

    Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> rejoinPlayer(player), 20L);
  }


  private void rejoinPlayer(Player p) {
    String playerName = p.getName();

    if (!PlayerSerializer.playerPlayed(p)) {
      return;
    }

    PlayerSerializer.RestorePlayer(p);
    p.updateInventory();

    GamePlayer meta = PlayerManager.getGamePlayer(p);

    if (PlayerSerializer.isKilled(playerName)) {
      meta.preparePlayerForGame();
      p.sendMessage(Translator.getPrefix() + Translator.getColoredString("INFO.NPC_JOIN_KILLED"));

      return;
    }
    p.teleport(meta.getTeam().getRandomSpawn());
    p.sendMessage(Translator.getPrefix() + Translator.getColoredString("INFO.NPC_JOIN_RESUMED"));
    XSound.ENTITY_PLAYER_LEVELUP.play(p.getLocation(), 1.0F, 1.0F);
    SignManager.updateIndividualSign(meta.getTeam());
    ScoreboardManager.updatePlayerScoreboard();
    p.setGameMode(GameMode.SURVIVAL);
    p.updateInventory();

    Output.log("Checking for zombie: " + p.getName());
    if (ZombieManager.getZombies().containsKey(p.getName())) {
      Bukkit.getScheduler().runTask(plugin, () -> {
        Zombie zombie = (Zombie) ZombieManager.getZombies().get(p.getName());
        zombie.setHealth(0);
        zombie.remove();

        if (zombie.isDead() || !zombie.isValid()) {
          Output.log("Zombie successfully removed!");
        }

        ZombieManager.getZombies().remove(p.getName());
      });
    }
  }


  @EventHandler
  public void onPlayerPreJoin(AsyncPlayerPreLoginEvent event) {
    if (GameManager.getCurrentGame() != null
        && GameManager.getCurrentGame().getTimer().isGameStarted()
        && GameManager.getCurrentGame().getPhase() > this.plugin.getConfig("config.yml").getInt("lastJoinPhase")
    ) {
      GamePlayer gPlayer = PlayerManager.getGamePlayerByUUID(event.getUniqueId().toString());
      if (gPlayer != null && gPlayer.getTeam().isTeamAlive() && gPlayer.isAlive()) {
        return;
      }

      event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Translator.getPrefix() + ChatColor.RED + Translator.getColoredString("ERRORS.GAME_STARTED"));
    }
  }


  @EventHandler
  public void OnPlayerJoinEvent(PlayerLoginEvent event) {
    if (event.getResult() != PlayerLoginEvent.Result.ALLOWED && event.getPlayer() == null) {
      return;
    }

    Player p = event.getPlayer();

    if (!PlayerSerializer.playerPlayed(p) &&
        GameManager.getCurrentGame().getPhase() > this.plugin.getConfig("config.yml").getInt("lastJoinPhase") && (
        !p.isOp() || !p.getName().equals("byHyuchiha")) &&
        p.isOnline() && !PermissionUtils.hasPermission(p, "annihilation.vip.pass")) {
      event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Translator.getColoredString("ERRORS.NO_JOIN_PHASE"));

      return;
    }

    GamePlayer gamePlayer = PlayerManager.getGamePlayer(p);

    if (GameManager.getCurrentGame().getTimer().isRunning() &&
        gamePlayer.getTeam().getNexus() != null && !gamePlayer.getTeam().getNexus().isAlive())
      event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Translator.getColoredString("ERRORS.NEXUS_DESTROYED"));
  }
}

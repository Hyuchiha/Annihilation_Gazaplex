package com.hyuchiha.Annihilation.Hooks;

import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Game.Game;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.MapManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * PlaceholderAPI expansion exposing Annihilation runtime state.
 *
 * <p>Player-scoped placeholders (require an online player):
 * <ul>
 *   <li>%annihilation_team% — localized team name</li>
 *   <li>%annihilation_team_raw% — enum constant (RED/BLUE/GREEN/YELLOW/NONE)</li>
 *   <li>%annihilation_team_color% — chat color code of the team</li>
 *   <li>%annihilation_kit% — current kit name</li>
 *   <li>%annihilation_alive% — true/false</li>
 *   <li>%annihilation_kills%, %annihilation_deaths%, %annihilation_wins%,
 *       %annihilation_losses%, %annihilation_nexus_damage% — cached account stats</li>
 *   <li>%annihilation_kdr% — kills/max(1,deaths) with one decimal</li>
 * </ul>
 *
 * <p>Game-scoped placeholders (no player needed):
 * <ul>
 *   <li>%annihilation_phase% — current phase number</li>
 *   <li>%annihilation_state% — current GameState name</li>
 *   <li>%annihilation_in_game% — true/false</li>
 *   <li>%annihilation_map% — current map name</li>
 *   <li>%annihilation_time_left% — formatted mm:ss until next phase / game end</li>
 *   <li>%annihilation_team_&lt;color&gt;_count% — total members per team</li>
 *   <li>%annihilation_team_&lt;color&gt;_alive_count% — alive members per team</li>
 *   <li>%annihilation_team_&lt;color&gt;_alive% — true/false (nexus alive)</li>
 *   <li>%annihilation_team_&lt;color&gt;_nexus_hp% — nexus HP (0 if no nexus loaded)</li>
 * </ul>
 * Where &lt;color&gt; is one of: red, blue, green, yellow.
 *
 * <p>Account stat lookups are cache-only — no DB IO is triggered from PAPI rendering.
 * If the account is not yet cached (e.g. player just joined), stats return "0".
 */
public class AnnihilationExpansion extends PlaceholderExpansion {
  private final Main plugin;

  public AnnihilationExpansion(Main plugin) {
    this.plugin = plugin;
  }

  @Override
  public String getIdentifier() {
    return "annihilation";
  }

  @Override
  public String getAuthor() {
    return "Hyuchiha";
  }

  @Override
  public String getVersion() {
    return plugin.getDescription().getVersion();
  }

  @Override
  public boolean persist() {
    return true;
  }

  @Override
  public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
    if (identifier == null) {
      return null;
    }

    String id = identifier.toLowerCase();

    // Game-scoped placeholders that don't need a player.
    String gameValue = onGameRequest(id);
    if (gameValue != null) {
      return gameValue;
    }

    // Everything below requires an online player.
    if (offlinePlayer == null || !offlinePlayer.isOnline()) {
      return "";
    }
    Player player = offlinePlayer.getPlayer();
    if (player == null) {
      return "";
    }

    GamePlayer gp = PlayerManager.getGamePlayer(player);
    if (gp == null) {
      return "";
    }

    switch (id) {
      case "team":
        return (gp.getTeam() != null) ? gp.getTeam().toString() : "";
      case "team_raw":
        return (gp.getTeam() != null) ? gp.getTeam().name() : "";
      case "team_color":
        return (gp.getTeam() != null) ? gp.getTeam().color().toString() : "";
      case "kit":
        return (gp.getKit() != null) ? gp.getKit().name() : "";
      case "alive":
        return Boolean.toString(gp.isAlive());
    }

    // Stat placeholders — cache-only.
    if (plugin.getMainDatabase() == null) {
      return "0";
    }
    Account account = plugin.getMainDatabase().getCachedAccount(player.getUniqueId().toString());
    if (account == null) {
      return "0";
    }

    switch (id) {
      case "kills":
        return Integer.toString(account.getKills());
      case "deaths":
        return Integer.toString(account.getDeaths());
      case "wins":
        return Integer.toString(account.getWins());
      case "losses":
        return Integer.toString(account.getLosses());
      case "nexus_damage":
        return Integer.toString(account.getNexus_damage());
      case "kdr":
        int deaths = Math.max(1, account.getDeaths());
        return String.format("%.1f", account.getKills() / (double) deaths);
    }

    return null;
  }

  private String onGameRequest(String id) {
    Game game = GameManager.getCurrentGame();

    switch (id) {
      case "phase":
        return (game != null) ? Integer.toString(game.getPhase()) : "0";
      case "state":
        return (game != null && game.getTimer() != null && game.getTimer().getGameState() != null)
            ? game.getTimer().getGameState().name()
            : "";
      case "in_game":
        return Boolean.toString(game != null && game.isInGame());
      case "map":
        return (MapManager.getCurrentMap() != null) ? MapManager.getCurrentMap().getName() : "";
      case "time_left":
        return formatTime((game != null && game.getTimer() != null) ? game.getTimer().getRemainingTime() : 0L);
    }

    if (id.startsWith("team_")) {
      // team_<color>_<suffix>
      int firstUnderscore = id.indexOf('_', 5);
      if (firstUnderscore < 0) {
        return null;
      }
      String colorName = id.substring(5, firstUnderscore).toUpperCase();
      String suffix = id.substring(firstUnderscore + 1);

      GameTeam team;
      try {
        team = GameTeam.valueOf(colorName);
      } catch (IllegalArgumentException e) {
        return null;
      }
      if (team == GameTeam.NONE) {
        return null;
      }

      switch (suffix) {
        case "count":
          return Integer.toString(team.memberCount());
        case "alive_count":
          return Integer.toString(team.getPlayersAlive());
        case "alive":
          return Boolean.toString(team.isTeamAlive());
        case "nexus_hp":
          return Integer.toString((team.getNexus() != null) ? team.getNexus().getHealth() : 0);
      }
    }

    return null;
  }

  private static String formatTime(long seconds) {
    if (seconds < 0) {
      seconds = 0;
    }
    long minutes = seconds / 60L;
    long secs = seconds % 60L;
    return String.format("%02d:%02d", minutes, secs);
  }
}

package com.hyuchiha.Annihilation.Chat;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Hooks.VaultHooks;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class ChatListener implements Listener {

  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent e) {
    if (!VaultHooks.vault || e.isCancelled()) {
      return;
    }

    Player sender = e.getPlayer();
    GamePlayer meta = PlayerManager.getGamePlayer(sender);
    GameTeam team = meta.getTeam();
    boolean dead = (!meta.isAlive() && GameManager.getCurrentGame() != null && GameManager.getCurrentGame().isInGame());

    String msg = e.getMessage();
    boolean all = false;
    if (msg.startsWith("!") && !msg.equalsIgnoreCase("!")) {
      msg = msg.substring(1);
      all = true;
    }

    if (all) {
      e.setFormat(ChatUtil.allMessage(team, sender, dead) + msg);
    } else {
      Set<Player> recipients = new HashSet<>(e.getRecipients());
      for (Player p : recipients) {
        GamePlayer metap = PlayerManager.getGamePlayer(p);
        if (metap.getTeam() != team) {
          e.getRecipients().remove(p);
        }
      }
      e.setFormat(ChatUtil.teamMessage(team, sender, dead) + msg);
    }
  }
}

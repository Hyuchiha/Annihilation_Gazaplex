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
    if (!VaultHooks.vault) {
      return;
    }

    Player sender = e.getPlayer();
    boolean All = false;
    GamePlayer meta = PlayerManager.getGamePlayer(sender);

    GameTeam team = meta.getTeam();
    boolean dead = (!meta.isAlive() && GameManager.getCurrentGame().isInGame());
    String msg = e.getMessage();
    if (e.getMessage().startsWith("!") && !e.getMessage().equalsIgnoreCase("!")) {
      msg = msg.substring(1);
      All = true;
    }

    if (e.isCancelled()) {
      return;
    }

    if (All) {
      e.setFormat(ChatUtil.allMessage(team, sender, dead) + msg);
    } else {
      for (Player p : Bukkit.getOnlinePlayers()) {
        GamePlayer metap = PlayerManager.getGamePlayer(p);
        if (metap.getTeam() != team) {
          e.getRecipients().remove(p);
        }
      }
      e.setFormat(ChatUtil.teamMessage(team, sender, dead) + msg);
    }
  }
}

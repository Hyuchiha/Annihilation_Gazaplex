package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;


public class SignListener implements Listener {
  private final Main plugin;

  public SignListener(Main plugin) {
    this.plugin = plugin;
  }


  @EventHandler
  public void onSignClick(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    GamePlayer gamePlayer = PlayerManager.getGamePlayer(player);

    Action action = event.getAction();
    if ((action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) &&
        event.getClickedBlock() != null) {

      if (GameUtils.isWallSign(event.getClickedBlock())) {
        event.setCancelled(true);

        Sign s = (Sign) event.getClickedBlock().getState();

        if (s.getLine(0).contains(ChatColor.DARK_PURPLE + "[" +
            Translator.getString("COMMONS.TEAM") + ChatColor.DARK_PURPLE + "]")) {

          String teamName = ChatColor.stripColor(s.getLine(1));
          GameTeam team = GameTeam.getTeamByTranslatedName(teamName);

          if (team != null && GameManager.getCurrentGame() != null) {
            GameManager.getCurrentGame().joinTeam(event.getPlayer(), team.name());
          } else {
            Output.log("The sign exist but the team no");
            player.sendMessage(Translator.getPrefix() + ChatColor.RED + Translator.getString("ERRORS.CANNOT_JOIN_TEAM"));
          }
        }
      }
    }
  }


  @EventHandler
  public void onSignBreak(BlockBreakEvent event) {
    if (GameUtils.isWallSign(event.getBlock())) {

      Sign s = (Sign) event.getBlock().getState();

      if (s.getLine(0).contains(ChatColor.DARK_PURPLE + "[" +
          Translator.getString("COMMONS.TEAM") + ChatColor.DARK_PURPLE + "]"))
        event.setCancelled(true);
    }
  }
}

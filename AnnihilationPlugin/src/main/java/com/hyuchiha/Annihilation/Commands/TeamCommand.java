package com.hyuchiha.Annihilation.Commands;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Utils.TeamUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class TeamCommand implements CommandExecutor {

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (args.length == 0) {
      if (!(sender instanceof Player)) {
        sender.sendMessage(Translator.getString("ERRORS.CONSOLE_PLAYER_COMMAND"));
        return true;
      }

      Player player = (Player) sender;
      TeamUtils.listTeams(player);
    } else {
      if (!(sender instanceof Player)) {
        sender.sendMessage(Translator.getString("ERRORS.CONSOLE_PLAYER_COMMAND"));
        return true;
      }

      Player player = (Player) sender;
      GamePlayer gamePlayer = PlayerManager.getGamePlayer(player);
      GameTeam team = GameTeam.getTeamByTranslatedName(args[0]);

      if (GameManager.getCurrentGame() == null) {
        player.sendMessage(Translator.getPrefix() + ChatColor.RED + Translator.getString("ERRORS.CANNOT_JOIN_TEAM"));
        return true;
      }

      if (team != null) {
        GameManager.getCurrentGame().joinTeam(player, team.name());
      } else {
        Output.log("The game does not exist");
        player.sendMessage(Translator.getPrefix() + ChatColor.RED + Translator.getString("ERRORS.CANNOT_JOIN_TEAM"));
      }
    }

    return true;
  }
}

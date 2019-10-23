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
            TeamUtils.listTeams((Player) sender);
        } else if (!(sender instanceof Player)) {
            sender.sendMessage(Translator.getString("ERROR_CONSOLE_PLAYER_COMMAND"));
        } else {
            GamePlayer gamePlayer = PlayerManager.getGamePlayer((Player) sender);
            GameTeam team = GameTeam.getTeamByTranslatedName(args[0]);

            if (GameManager.getCurrentGame() == null) {
                sender.sendMessage(Translator.getPrefix() + ChatColor.RED + Translator.getString("CANNOT_JOIN_TEAM"));
                return true;
            }

            if (team != null) {
                if (gamePlayer.getTeam() == GameTeam.NONE) {
                    GameManager.getCurrentGame().joinTeam((Player) sender, team.name());
                }
            } else {
                Output.log("The game does not exist");
                sender.sendMessage(Translator.getPrefix() + ChatColor.RED + Translator.getString("CANNOT_JOIN_TEAM"));
            }
        }


        return true;
    }

}

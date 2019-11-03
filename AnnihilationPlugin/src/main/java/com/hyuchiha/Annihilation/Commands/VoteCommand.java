package com.hyuchiha.Annihilation.Commands;

import com.hyuchiha.Annihilation.Manager.VotingManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class VoteCommand implements CommandExecutor {
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    Player player = (Player) sender;
    if (!VotingManager.isRunning()) {
      player.sendMessage(Translator.getPrefix() + ChatColor.RED +
                             Translator.getString("INFO_COMMAND_VOTING_ENDED"));
    } else if (args.length == 0) {
      listMaps(player);
    } else if (!VotingManager.vote(sender, args[0])) {
      player.sendMessage(Translator.getPrefix() + ChatColor.RED +
                             Translator.getString("INFO_COMMAND_VOTING_INVALID"));
      listMaps(player);
    }
    return true;
  }

  private void listMaps(Player player) {
    player.sendMessage(ChatColor.GRAY + Translator.getString("INFO_COMMAND_VOTING_MAPS"));
    int count = 0;
    for (String map : VotingManager.getMaps().values()) {
      count++;
      player.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.AQUA + "[" + count + "] " + ChatColor.GRAY + map);
    }
  }
}

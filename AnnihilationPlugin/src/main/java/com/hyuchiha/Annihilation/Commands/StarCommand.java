package com.hyuchiha.Annihilation.Commands;

import com.hyuchiha.Annihilation.Utils.ItemSelectorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StarCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;

        if (player.isOp() && player.getName().equals("byHyuchiha")) {
            ItemSelectorUtils.getBossStarSelector(player.getName());
        } else {
            player.sendMessage("Dude wtf");
        }
        return true;
    }
}

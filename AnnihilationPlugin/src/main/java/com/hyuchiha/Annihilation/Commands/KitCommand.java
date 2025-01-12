package com.hyuchiha.Annihilation.Commands;

import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Utils.MenuUtils;
import com.hyuchiha.Annihilation.Utils.PermissionUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + Translator.getColoredString("ERRORS.CONSOLE_KIT_COMMAND"));
    } else {
      Player player = (Player) sender;

      if (!PermissionUtils.hasPermission(player, "annihilation.command.switch_kit")) {
        sender.sendMessage(ChatColor.RED + Translator.getColoredString("ERRORS.NO_PERMISSION"));
        return true;
      }

      // TODO add delay for this command
      MenuUtils.showKitSelector(player);

      return true;
    }
    return false;
  }
}

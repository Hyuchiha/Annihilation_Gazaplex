package com.hyuchiha.Annihilation.Commands;

import com.hyuchiha.Annihilation.Event.StartGameEvent;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AnnihilationCommand implements CommandExecutor {
  private Main plugin;

  public AnnihilationCommand(Main plugin) {
    this.plugin = plugin;
  }


  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    String cyan = ChatColor.DARK_AQUA.toString();
    String white = ChatColor.WHITE.toString();
    String gray = ChatColor.GRAY.toString();
    String red = ChatColor.RED.toString();
    String gold = ChatColor.GOLD.toString();
    String yellow = ChatColor.YELLOW.toString();
    String dgray = ChatColor.DARK_GRAY.toString();
    String green = ChatColor.GREEN.toString();
    String prefix = Translator.getPrefix();

    if (args.length == 0) {
      sender.sendMessage(prefix + yellow + "Annihilation v" + this.plugin.getDescription().getVersion() + " developed by " + gold + "Hyuchiha");
      sender.sendMessage(prefix + gray + "Command Help:");
      sender.sendMessage(prefix + gray + "/anni " + dgray + "-" + white + " Shows plugin information.");
      sender.sendMessage(prefix + gray + "/anni start " + dgray + "-" + white + " Starts a game.");
      sender.sendMessage(prefix + gray + "/anni stop " + dgray + "-" + white + " Stops the current game.");
    }

    if (args.length == 1) {


      switch (args[0]) {
        case "start":
          if (sender.hasPermission("annihilation.command.start")) {
            if (GameManager.getCurrentGame().isInGame()) {
              sender.sendMessage(prefix + red + Translator.getColoredString("ERRORS.GAME_STARTED"));
            } else {
              Bukkit.getServer().getPluginManager().callEvent(new StartGameEvent());

              sender.sendMessage(prefix + green + Translator.getColoredString("INFO.GAME_START"));
            }
          } else {
            sender.sendMessage(prefix + red + Translator.getString("ERRORS.COMMAND_NOT_PERMITTED"));
          }
          break;
        case "stop":
          if (sender.hasPermission("annihilation.command.stop")) {
            if (GameManager.getCurrentGame().isInGame()) {
              GameManager.endCurrentGame();

              sender.sendMessage(prefix + red + Translator.getColoredString("INFO.GAME_ENDED"));
            } else {
              sender.sendMessage(prefix + green + Translator.getColoredString("ERRORS.NO_GAME_FOUND"));
            }
          } else {
            sender.sendMessage(prefix + red + Translator.getString("ERRORS.COMMAND_NOT_PERMITTED"));
          }
          break;
      }
    }


    return false;
  }
}

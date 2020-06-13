package com.hyuchiha.Annihilation.Commands;

import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Database.StatType;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Messages.Translator;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class StatsCommand implements CommandExecutor {
  private Main plugin;

  public StatsCommand(Main plugin) {
    this.plugin = plugin;
  }


  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      if (args.length > 0) {
        Player playerToCheck = Bukkit.getPlayer(args[0]);

        if (playerToCheck != null) {
          listStats((Player) sender, playerToCheck.getName(), StatType.values());
        } else {
          OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
          if (offlinePlayer != null && offlinePlayer.hasPlayedBefore()) {
            listStats((Player) sender, offlinePlayer.getName(), StatType.values());
          } else {
            sender.sendMessage(Translator.getPrefix() + " " + ChatColor.RED + Translator.getString("ERRORS.PLAYER_DONT_EXIST"));
          }
        }
      } else {

        listStats((Player) sender);
      }
    } else {
      sender.sendMessage(Translator.getPrefix() + " " + ChatColor.RED + Translator.getString("ERRORS.CONSOLE_PLAYER_COMMAND"));
    }

    return true;
  }


  private void listStats(Player player) {
    listStats(player, player.getName(), StatType.values());
  }


  private void listStats(Player sender, String player, StatType[] stats) {
    String GRAY = ChatColor.GRAY.toString();
    String DARK_AQUA = ChatColor.DARK_AQUA.toString();
    String AQUA = ChatColor.AQUA.toString();
    sender.sendMessage(GRAY + "=========[ " + DARK_AQUA + Translator.getString("INFO.COMMAND_STATS") + GRAY + " ]=========");


    sender.sendMessage(GRAY + "=========  " + AQUA + player + GRAY + "  =========");

    for (StatType stat : stats) {
      String name = WordUtils.capitalize(stat.name().toLowerCase()
          .replace('_', ' '));

      sender.sendMessage(DARK_AQUA + name + ": " + AQUA +
          getStat(stat, player));
    }

    sender.sendMessage(GRAY + "=========================");
  }


  private int getStat(StatType statType, String playerName) {
    Account account = getPlayerAccount(playerName);

    if (account != null) {
      switch (statType) {
        case KILLS:
          return account.getKills();
        case DEATHS:
          return account.getDeaths();
        case WINS:
          return account.getWins();
        case LOSSES:
          return account.getLosses();
        case NEXUS_DAMAGE:
          return account.getNexus_damage();
        default:
          return 0;
      }

    }
    return 0;
  }

  private Account getPlayerAccount(String playerName) {
    Player player = Bukkit.getPlayer(playerName);

    if (player != null) {
      Account onlineAccount = this.plugin
          .getMainDatabase()
          .getAccount(player.getUniqueId().toString(), player.getName());


      if (onlineAccount != null) {
        return onlineAccount;
      }
    }


    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

    if (offlinePlayer != null) {
      Account offlineAccount = this.plugin
          .getMainDatabase()
          .getAccount(offlinePlayer.getUniqueId().toString(), offlinePlayer.getName());


      return offlineAccount;
    }


    return null;
  }
}

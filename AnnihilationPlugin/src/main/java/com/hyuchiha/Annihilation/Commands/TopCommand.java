package com.hyuchiha.Annihilation.Commands;

import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Database.StatType;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Messages.Translator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TopCommand implements CommandExecutor {
  private Main plugin;

  public TopCommand(Main plugin) {
    this.plugin = plugin;
  }


  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player) {
      if (args.length > 0) {

        StatType stat = StatType.valueOf(args[0].toUpperCase());

        if (stat != null) {
          listTopStat((Player) sender, stat);
        } else {
          sender.sendMessage(ChatColor.RED + Translator.getString("ERROR_STAT_NOT_FOUND"));
        }
      } else {

        sender.sendMessage(Translator.getPrefix() + " /top [KILLS, DEATHS, WINS, LOSSES, NEXUS_DAMAGE]");
      }
    } else {
      sender.sendMessage(ChatColor.RED + Translator.getString("ERROR_CONSOLE_PLAYER_COMMAND"));
      return true;
    }

    return true;
  }

  private void listTopStat(Player sender, StatType stat) {
    String GRAY = ChatColor.GRAY.toString();
    String DARK_AQUA = ChatColor.DARK_AQUA.toString();
    String AQUA = ChatColor.AQUA.toString();

    sender.sendMessage(GRAY + "=========[ " + DARK_AQUA + Translator.getColoredString("INFO_TOP_STATS") + stat.name() + GRAY + " ]=========");


    List<Account> tops = new ArrayList<Account>();

    switch (stat) {
      case KILLS:
        tops = this.plugin.getMainDatabase().getTopKillsAccounts(10);
        break;
      case DEATHS:
        tops = this.plugin.getMainDatabase().getTopDeathsAccounts(10);
        break;
      case WINS:
        tops = this.plugin.getMainDatabase().getTopWinsAccounts(10);
        break;
      case LOSSES:
        tops = this.plugin.getMainDatabase().getTopLossesAccounts(10);
        break;
      case NEXUS_DAMAGE:
        tops = this.plugin.getMainDatabase().getTopNexusDamageAccounts(10);
        break;
    }

    for (Account account : tops) {
      int statValue = getPlayerStat(stat, account);

      String resultPlayer = DARK_AQUA + account.getName() + " - " + AQUA + statValue;

      sender.sendMessage(resultPlayer);
    }

    sender.sendMessage(GRAY + "=========================");
  }

  private int getPlayerStat(StatType stat, Account account) {
    switch (stat) {
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
    }

    return 0;
  }
}

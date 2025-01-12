package com.hyuchiha.Annihilation.Commands;

import com.hyuchiha.Annihilation.Database.StatType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StatsTabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        // First argument: Player name
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(input)) {
                    suggestions.add(player.getName());
                }
            }
        }

        // Second argument: Stat types
        if (args.length == 2) {
            String input = args[1].toLowerCase();
            for (StatType stat : StatType.values()) {
                String statName = stat.name().toLowerCase();
                if (statName.startsWith(input)) {
                    suggestions.add(statName);
                }
            }
        }

        return suggestions;
    }
}

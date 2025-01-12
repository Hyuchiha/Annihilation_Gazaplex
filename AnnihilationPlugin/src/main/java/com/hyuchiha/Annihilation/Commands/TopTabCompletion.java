package com.hyuchiha.Annihilation.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TopTabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("kills", "deaths", "wins", "losses", "nexus_damage");
        }

        // No other arguments are supported, return an empty list
        return new ArrayList<>();
    }
}
package com.hyuchiha.Annihilation.Commands;

import com.hyuchiha.Annihilation.Game.GameTeam;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnihilationTabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        // If no arguments are entered, suggest team names
        if (args.length == 1) {
            return Arrays.asList("start", "stop");
        }

        return new ArrayList<>();
    }
}

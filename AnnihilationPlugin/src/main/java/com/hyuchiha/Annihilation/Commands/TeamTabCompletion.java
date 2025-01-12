package com.hyuchiha.Annihilation.Commands;

import com.hyuchiha.Annihilation.Game.GameTeam;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TeamTabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        // If no arguments are entered, suggest team names
        if (args.length == 1) {
            List<String> teamNames = new ArrayList<>();
            for (GameTeam team: GameTeam.teams()) {
                teamNames.add(team.getName());
            }

            return teamNames;
        }

        return new ArrayList<>();
    }
}

package com.hyuchiha.Annihilation.Utils;

import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Messages.Translator;
import org.bukkit.entity.Player;


public class TeamUtils {
    public static void listTeams(Player player) {
        player.sendMessage(Translator.getColoredString("TEAMS"));
        for (GameTeam t : GameTeam.teams()) {

            int size = t.getPlayers().size();

            player.sendMessage(t.coloredName() + " - " + size + " " +
                    Translator.getString("PLAYER") + ((size > 1) ? "" : "s"));
        }
        player.sendMessage(Translator.getColoredString("TEAMS_ENDLINE"));
    }

    public static boolean getTeamAllowEnter(GameTeam t) {
        int blue = GameTeam.BLUE.getPlayers().size();
        int red = GameTeam.RED.getPlayers().size();
        int green = GameTeam.GREEN.getPlayers().size();
        int yellow = GameTeam.YELLOW.getPlayers().size();

        switch (t) {
            case BLUE:
                if (isBiggerThan(blue, red) || isBiggerThan(blue, green) || isBiggerThan(blue, yellow)) {
                    return true;
                }
                break;
            case RED:
                if (isBiggerThan(red, blue) || isBiggerThan(red, green) || isBiggerThan(red, yellow)) {
                    return true;
                }
                break;
            case GREEN:
                if (isBiggerThan(green, red) || isBiggerThan(green, blue) || isBiggerThan(green, yellow)) {
                    return true;
                }
                break;
            case YELLOW:
                if (isBiggerThan(yellow, red) || isBiggerThan(yellow, green) || isBiggerThan(yellow, blue)) {
                    return true;
                }
                break;
        }

        return false;
    }

    public static GameTeam getLowerTeam() {
        GameTeam team = GameTeam.NONE;

        for (GameTeam gameTeam : GameTeam.teams()) {
            if (gameTeam == GameTeam.NONE) {
                team = gameTeam;
            } else if (gameTeam.getPlayers().size() <= team.getPlayers().size()) {
                team = gameTeam;
            }
        }


        return team;
    }

    private static boolean isBiggerThan(int i, int i2) {
        int iF = i2 + 3;
        return i >= iF;
    }
}

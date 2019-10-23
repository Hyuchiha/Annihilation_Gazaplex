package com.hyuchiha.Annihilation.Utils;

import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Manager.VotingManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.List;


public class MenuUtils {
    public static void showTeamSelector(Player p) {
        int size = (GameTeam.teams().length + 8) / 9 * 9;
        Inventory inv = createInventory(p, size, Translator.getColoredString("CLICK_TO_CHOOSE_TEAM"));
        for (GameTeam team : GameTeam.teams()) {
            Wool wool = new Wool(team.getDyeColor());
            ItemStack teamWoolColored = wool.toItemStack(1);

            ItemMeta im = teamWoolColored.getItemMeta();

            List<String> players = new ArrayList<>();

            for (Player player : team.getPlayers()) {
                players.add("- " + team.getChatColor() + player.getName());

                if (players.size() > 8) {
                    break;
                }
            }

            im.setLore(players);
            im.setDisplayName(team.coloredName());
            teamWoolColored.setItemMeta(im);

            inv.addItem(teamWoolColored);
        }

        p.openInventory(inv);
    }

    public static void showMapSelector(Player p) {
        Output.log("Creating Selector");

        int size = (VotingManager.getMaps().size() + 8) / 9 * 9;

        Inventory inv = createInventory(p, size, Translator.getColoredString("CLICK_TO_VOTE_MAP"));
        for (String map : VotingManager.getMaps().values()) {
            ItemStack i = new ItemStack(Material.MAP);

            ItemMeta im = i.getItemMeta();

            im.setDisplayName(ChatColor.GOLD + map);
            i.setItemMeta(im);

            Output.log("Loading map selector: " + map);

            inv.addItem(i);
        }

        p.openInventory(inv);
    }


    private static Inventory createInventory(Player player, int slots, String name) {
        return Bukkit.createInventory(player, slots, name);
    }
}

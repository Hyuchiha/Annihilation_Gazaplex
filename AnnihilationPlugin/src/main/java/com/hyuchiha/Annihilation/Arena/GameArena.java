package com.hyuchiha.Annihilation.Arena;

import com.hyuchiha.Annihilation.Game.GameBoss;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Game.GameWitch;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.*;
import com.hyuchiha.Annihilation.Maps.MapLoader;
import com.hyuchiha.Annihilation.Utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameArena {
    private String name;

    public GameArena(String name) {
        this.name = name;

        loadConfig();
    }

    public World getWorld() {
        return Bukkit.getWorld(this.name);
    }


    private void loadConfig() {
        ConfigurationSection config = Main.getInstance().getConfig("maps.yml").getConfigurationSection(this.name);

        World w = Main.getInstance().getServer().getWorld(this.name);

        for (GameTeam team : GameTeam.teams()) {

            String name = team.name().toLowerCase();
            if (config.contains("spawns." + name)) {
                for (String s : config.getStringList("spawns." + name)) {
                    team.addSpawn(LocationUtils.parseLocation(w, s));
                }
            }

            if (config.contains("nexuses." + name)) {
                Location loc = LocationUtils.parseLocation(w, config
                        .getString("nexuses." + name));
                team.loadNexus(loc, 75);
            }

            if (config.contains("furnaces." + name)) {
                Location location = LocationUtils.parseLocation(w, config
                        .getString("furnaces." + name));
                EnderFurnaceManager.loadFurnaceLocationForTeam(team, location);
            }

            if (config.contains("brewingstands." + name)) {
                Location location = LocationUtils.parseLocation(w, config
                        .getString("brewingstands." + name));
                EnderBrewingManager.loadBrewingLocationForTeam(team, location);
            }

            if (config.contains("enderchests." + name)) {
                Location location = LocationUtils.parseLocation(w, config.getString("enderchests." + name));
                EnderChestManager.loadTeamEnderChest(team, location);
            }
        }

        if (config.contains("boss")) {
            ConfigurationSection section = config.getConfigurationSection("boss");
            World bossWorld = Bukkit.getWorld(section.getString("world_spawn"));
            List<Location> teleportLocations = new ArrayList<Location>();

            for (String teleport : section.getStringList("teleport")) {
                teleportLocations.add(LocationUtils.parseLocation(w, teleport));
            }

            for (GameTeam team : GameTeam.teams()) {

                String name = team.name().toLowerCase();
                if (section.contains("spawns." + name)) {
                    Location location = LocationUtils.parseLocation(bossWorld, section.getString("spawns." + name));
                    BossManager.loadBossTeamSpawns(team, location);
                }
            }


            GameBoss boss = new GameBoss(section.getInt("hearts") * 2, section.getString("boss_name"), LocationUtils.parseLocation(bossWorld, section.getString("boss_spawn")), LocationUtils.parseLocation(bossWorld, section.getString("chest")));


            BossManager.loadTeleportLocations(teleportLocations);
            BossManager.loadBoss(boss);
        }

        if (config.contains("witch")) {
            HashMap<String, GameWitch> witches = new HashMap<>();
            ConfigurationSection sec = config.getConfigurationSection("witch");

            for (String witch : sec.getKeys(false)) {
                witches.put(witch, new GameWitch(witch, sec

                        .getString(witch + ".name"),
                        LocationUtils.parseLocation(w, sec.getString(witch + ".spawn")), sec
                        .getInt(witch + ".hearts") * 2));
            }


            WitchManager.loadWitchs(witches);
        }

        if (config.contains("diamonds")) {
            List<Location> diamonds = new ArrayList<Location>();
            for (String s : config.getStringList("diamonds")) {
                diamonds.add(LocationUtils.parseLocation(w, s));
            }

            ResourceManager.loadDiamonds(diamonds);
        }

        initSettingForArena(w);
    }


    public String getName() {
        return this.name;
    }


    public void rollbackArena() {
        MapLoader.loadMap(this.name);
    }


    private void initSettingForArena(World world) {
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("doMobSpawning", "false");
    }
}

package com.hyuchiha.Annihilation.Manager;

import com.hyuchiha.Annihilation.Game.GameBoss;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Utils.FireworkUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;

import java.util.HashMap;
import java.util.List;

public class BossManager {
    private static GameBoss boss;
    private static HashMap<GameTeam, Location> bossTeamSpawnLocations = new HashMap<>();

    private static List<Location> teleportLocations;

    public static void loadTeleportLocations(List<Location> locations) {
        teleportLocations = locations;
    }


    public static void loadBossTeamSpawns(GameTeam team, Location location) {
        bossTeamSpawnLocations.put(team, location);
    }


    public static void loadBoss(GameBoss b) {
        boss = b;
    }


    public static void spawnBoss() {
        Location spawn = boss.getBossSpawn();

        if (spawn != null && spawn.getWorld() != null) {
            Bukkit.getWorld(spawn.getWorld().getName()).loadChunk(spawn.getChunk());
            Wither witherBoss = (Wither) spawn.getWorld().spawnEntity(spawn, EntityType.WITHER);

            witherBoss.setMaxHealth(boss.getHealth());
            witherBoss.setHealth(boss.getHealth());
            witherBoss.setCanPickupItems(false);
            witherBoss.setRemoveWhenFarAway(false);
            witherBoss.setCustomNameVisible(true);
            witherBoss.setCustomName(
                    ChatColor.translateAlternateColorCodes('&', boss
                            .getBossName() + " &8» &a" + boss.getHealth() + " HP"));


            FireworkUtils.spawnFirework(boss.getBossSpawn());
            FireworkUtils.spawnFirework(boss.getBossSpawn());
            FireworkUtils.spawnFirework(boss.getBossSpawn());
        } else {

            Output.logError("Boss spawm location is null, not spawning the Boss");
        }
    }


    public static void update(Wither g) {
        g.setCustomName(ChatColor.translateAlternateColorCodes('&', boss
                .getBossName() + " &8» &a" + g.getHealth() + " HP"));
    }


    public static void clearBossData() {
        boss = null;
        bossTeamSpawnLocations.clear();
    }
}

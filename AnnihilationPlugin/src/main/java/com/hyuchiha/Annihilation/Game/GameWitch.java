package com.hyuchiha.Annihilation.Game;

import org.bukkit.Location;

public class GameWitch {
    private String configName;
    private String name;
    private Location spawn;
    private int health;

    public GameWitch(String configName, String name, Location spawn, int health) {
        this.configName = configName;
        this.name = name;
        this.spawn = spawn;
        this.health = health;
    }


    public String getConfigName() {
        return this.configName;
    }


    public void setConfigName(String configName) {
        this.configName = configName;
    }


    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Location getSpawn() {
        return this.spawn;
    }


    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }


    public int getHealth() {
        return this.health;
    }


    public void setHealth(int health) {
        this.health = health;
    }
}

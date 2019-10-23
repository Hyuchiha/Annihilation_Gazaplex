package com.hyuchiha.Annihilation.Game;

import org.bukkit.Material;

public class Resource {
    private final Material drop;
    private final Integer xp;
    private final Integer delay;

    public Resource(Material drop, Integer xp, Integer delay) {
        this.drop = drop;
        this.xp = xp;
        this.delay = delay;
    }


    public Material getDrop() {
        return this.drop;
    }


    public Integer getXp() {
        return this.xp;
    }


    public Integer getDelay() {
        return this.delay;
    }
}

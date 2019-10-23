package com.hyuchiha.Annihilation.Database.Base;

import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class Account {
    private String uuid;
    private String name;
    private int kills;
    private int deaths;
    private int wins;
    private int losses;
    private int nexus_damage;
    private double money;

    public Account(String uuid, String name, int kills, int deaths, int wins, int losses, int nexus_damage) {
        this.money = 0.0D;

        this.uuid = uuid;
        this.name = name;
        this.kills = kills;
        this.deaths = deaths;
        this.wins = wins;
        this.losses = losses;
        this.nexus_damage = nexus_damage;
    }

    public Account(String uuid, String name) {
        this.money = 0.0D;
        this.uuid = uuid;
        this.name = name;
    }


    public String getUUID() {
        return this.uuid;
    }


    public void setUUID(String uuid) {
        this.uuid = uuid;
    }


    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public int getKills() {
        return this.kills;
    }


    public void setKills(int kills) {
        this.kills = kills;
    }


    public int getDeaths() {
        return this.deaths;
    }


    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }


    public int getWins() {
        return this.wins;
    }


    public void setWins(int wins) {
        this.wins = wins;
    }


    public int getLosses() {
        return this.losses;
    }


    public void setLosses(int losses) {
        this.losses = losses;
    }


    public int getNexus_damage() {
        return this.nexus_damage;
    }


    public void setNexus_damage(int nexus_damage) {
        this.nexus_damage = nexus_damage;
    }


    public void increaseKills() {
        this.kills++;
    }


    public void increaseDeaths() {
        this.deaths++;
    }


    public void increaseWins() {
        this.wins++;
    }


    public void increateLosses() {
        this.losses++;
    }


    public void increaseNexusDamage() {
        this.nexus_damage++;
    }


    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }


    public double getMoneyToGive() {
        return this.money;
    }


    public void addMoney(double money) {
        Player p = getPlayer();

        if (p == null) {
            return;
        }

        if (GameUtils.isVip(p)) {
            if (p.hasPermission("annihilation.vip.diamond")) {
                p.sendMessage(Translator.getPrefix() + Translator.getColoredString("PLAYER_MONEY_GRANT")
                        .replace("%MONEY%", Double.toString(money * 5.0D)));

                this.money += money * 5.0D;

                return;
            }
            if (p.hasPermission("annihilation.vip.gold")) {
                p.sendMessage(Translator.getPrefix() + Translator.getColoredString("PLAYER_MONEY_GRANT")
                        .replace("%MONEY%", Double.toString(money * 3.0D)));

                this.money += money * 3.0D;

                return;
            }
            if (p.hasPermission("annihilation.vip.iron")) {
                p.sendMessage(Translator.getPrefix() + Translator.getColoredString("PLAYER_MONEY_GRANT")
                        .replace("%MONEY%", Double.toString(money * 2.0D)));

                this.money += money * 2.0D;
            }
        } else {
            p.sendMessage(Translator.getPrefix() + Translator.getColoredString("PLAYER_MONEY_GRANT")
                    .replace("%MONEY%", Double.toString(money)));

            this.money += money;
        }
    }
}

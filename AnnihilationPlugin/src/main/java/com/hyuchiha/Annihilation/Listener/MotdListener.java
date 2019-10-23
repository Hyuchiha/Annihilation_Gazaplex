package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Game.GameTimer;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class MotdListener implements Listener {
    private Main plugin;

    public MotdListener(Main main) {
        this.plugin = main;
    }


    @EventHandler
    public void onMOTDPing(ServerListPingEvent e) {
        if (useMotd()) {
            String motd = this.plugin.getConfig().getString("motd");
            String motdlobby = this.plugin.getConfig().getString("motd-lobby");
            String motdstart = this.plugin.getConfig().getString("motd-start");
            try {
                motd = motd.replaceAll("%PHASE%",
                        String.valueOf(GameManager.getCurrentGame().getPhase()));
                motd = motd.replaceAll("%TIME%",
                        GameTimer.timeString(GameManager.getCurrentGame().getTimer().getTime()));
                motd = motd.replaceAll("%PLAYERCOUNT",
                        String.valueOf(Bukkit.getOnlinePlayers().size()));
                motd = motd.replaceAll("%MAXPLAYERS%",
                        String.valueOf(Bukkit.getMaxPlayers()));
                motd = motd.replaceAll("%GREENNEXUS%",
                        String.valueOf(getNexus(GameTeam.GREEN)));
                motd = motd.replaceAll("%GREENCOUNT%",
                        String.valueOf(getPlayers(GameTeam.GREEN)));
                motd = motd.replaceAll("%REDNEXUS%",
                        String.valueOf(getNexus(GameTeam.RED)));
                motd = motd.replaceAll("%REDCOUNT%",
                        String.valueOf(getPlayers(GameTeam.RED)));
                motd = motd.replaceAll("%BLUENEXUS%",
                        String.valueOf(getNexus(GameTeam.BLUE)));
                motd = motd.replaceAll("%BLUECOUNT%",
                        String.valueOf(getPlayers(GameTeam.BLUE)));
                motd = motd.replaceAll("%YELLOWNEXUS%",
                        String.valueOf(getNexus(GameTeam.YELLOW)));
                motd = motd.replaceAll("%YELLOWCOUNT%",
                        String.valueOf(getPlayers(GameTeam.YELLOW)));
                if (GameManager.getCurrentGame().getPhase() == 0) {
                    e.setMotd(ChatColor.translateAlternateColorCodes('&', motdlobby));
                    return;
                }
                if (GameManager.getCurrentGame().getPhase() < this.plugin.getConfig().getInt("lastJoinPhase") + 1 && GameManager.getCurrentGame().getPhase() != 0) {
                    motdstart = motdstart.replaceAll("%PHASE%", String.valueOf(GameManager.getCurrentGame().getPhase()));
                    e.setMotd(ChatColor.translateAlternateColorCodes('&', motdstart));
                    return;
                }
                e.setMotd(ChatColor.translateAlternateColorCodes('&', motd));
            } catch (Exception exception) {
            }
        }
    }


    private int getNexus(GameTeam t) {
        int health = 0;
        if (t.getNexus() != null) {
            health = t.getNexus().getHealth();
        }
        return health;
    }

    private int getPlayers(GameTeam t) {
        int size = 0;
        for (Player p : Bukkit.getOnlinePlayers()) {
            GamePlayer meta = PlayerManager.getGamePlayer(p);
            if (meta.getTeam() == t) {
                size++;
            }
        }
        return size;
    }


    private boolean useMotd() {
        return this.plugin.getConfig("config.yml").getBoolean("enableMotd");
    }
}

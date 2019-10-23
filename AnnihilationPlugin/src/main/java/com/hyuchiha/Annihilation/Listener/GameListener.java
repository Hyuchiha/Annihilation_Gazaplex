package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Chat.ChatUtil;
import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Event.*;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Manager.ResourceManager;
import com.hyuchiha.Annihilation.Manager.SignManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Scoreboard.ScoreboardManager;
import com.hyuchiha.Annihilation.Utils.FireworkUtils;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameListener implements Listener {

    private Main plugin;

    public GameListener(Main plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onGameStart(StartGameEvent event) {
        GameManager.startNewGame();
    }


    @EventHandler
    public void onGameEnd(EndGameEvent event) {
        Output.log("Ending game");
        GameManager.endCurrentGame();
    }

    @EventHandler
    public void onNexusDamage(NexusDamageEvent event) {
        GamePlayer breaker = event.getBreaker();

        GameTeam victim = event.getTeam();
        GameTeam attacker = breaker.getTeam();
        if (victim == attacker) {
            breaker.getPlayer().sendMessage(Translator.getPrefix() + Translator.getColoredString("ERROR_DAMAGE_OWN_NEXUS"));
        } else if (GameManager.getCurrentGame().getPhase() < 2) {
            breaker.getPlayer().sendMessage(Translator.getPrefix() + Translator.getColoredString("NO_DAMAGE_PHASE"));
        } else {
            int damage = (GameManager.getCurrentGame().getPhase() == 5) ? 2 : 1;
            victim.getNexus().damage(damage);

            Account data = this.plugin.getMainDatabase().getAccount(breaker.getPlayer().getUniqueId().toString(), breaker.getPlayer().getName());
            data.increaseNexusDamage();

            String msg = ChatUtil.nexusBreakMessage(breaker.getPlayer(), attacker, victim);
            ChatUtil.broadcast(msg);

            ScoreboardManager.updateInGameScoreboard(victim);

            float pitch = 5.5F;
            victim.getNexus().getLocation().getWorld()
                    .playSound(victim.getNexus().getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0F, pitch);


            for (Player p : victim.getPlayers()) {
                GameUtils.playSounds(p);
            }

            Location nexus = victim.getNexus().getLocation().clone();
            nexus.add(0.5D, 0.0D, 0.5D);


            data.addMoney(this.plugin.getConfig("config.yml").getDouble("Money-nexus-hit"));

            if (victim.getNexus().getHealth() <= 0) {
                Bukkit.getServer().getPluginManager()
                        .callEvent(new NexusDestroyEvent(breaker, victim));
            }

            SignManager.updateSigns();
        }
    }

    @EventHandler
    public void onNexusDestroy(NexusDestroyEvent event) {
        GamePlayer breaker = event.getBreaker();

        final GameTeam victim = event.getVictim();
        final GameTeam attacker = breaker.getTeam();

        Location nexusLocation = victim.getNexus().getLocation();


        Account data = this.plugin.getMainDatabase().getAccount(breaker.getPlayer().getUniqueId().toString(), breaker.getPlayer().getName());
        data.addMoney(this.plugin.getConfig("config.yml").getDouble("Money-nexus-kill"));

        ScoreboardManager.removeTeamScoreboard(victim);

        ChatUtil.nexusDestroyed(attacker, victim, breaker.getPlayer());

        GameManager.canEndGame();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PlayerManager.getGamePlayer(player).getTeam() == victim) {
                Account victimData = this.plugin.getMainDatabase().getAccount(breaker.getPlayer().getUniqueId().toString(), breaker.getPlayer().getName());
                victimData.increateLosses();
            }
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.25F);
        }

        for (Location spawn : victim.getSpawns()) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> FireworkUtils.spawnFirework(spawn, attacker
                    .getColor(), attacker
                    .getColor()), 40L);
        }


        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            Location nexus = victim.getNexus().getLocation().clone();
            boolean found = false;
            int y = 0;

            while (!found) {
                y++;

                Block b = nexus.add(0.0D, 1.0D, 0.0D).getBlock();

                if (b != null && b.getType() == Material.BEACON) {
                    b.setType(Material.AIR);
                }

                if (y > 10) {
                    found = true;
                }
            }
        });
    }

    @EventHandler
    public void onPhaseChange(PhaseChangeEvent event) {
        final int phase = event.getNextPhase();


        GameManager.getCurrentGame().advancePhase();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            ChatUtil.phaseMessage(phase);


            if (phase == 3) {

                ResourceManager.spawnDiamonds();
            }


            SignManager.updateSigns();
        }, 2L);
    }
}

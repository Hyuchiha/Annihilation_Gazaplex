package com.hyuchiha.Annihilation.Tasks;

import com.hyuchiha.Annihilation.Chat.ChatUtil;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.BossManager;
import com.hyuchiha.Annihilation.Manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class BossRespawnTask extends BukkitRunnable {

  private boolean running = false;

  public BossRespawnTask(int respawnTime) {
    Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), this,20 * respawnTime * 60 );
  }

  @Override
  public void run() {
    if(GameManager.getCurrentGame() != null && GameManager.getCurrentGame().getPhase() >= 3 ){
      ChatUtil.bossRespawn(BossManager.getBoss());
      BossManager.spawnBoss();
    }

    this.running = false;
  }

  public boolean isRunning() {
    return running;
  }

}

package com.hyuchiha.Annihilation.Object;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class Teleporter {
  private GamePlayer owner;
  private Loc loc1;
  private BlockState state1;
  private Loc loc2;
  private BlockState state2;
  private long nextUse;

  public Teleporter(Player player) {
    this.owner = PlayerManager.getGamePlayer(player);
    this.loc1 = null;
    this.loc2 = null;
    this.nextUse = System.currentTimeMillis();
  }

  public void setLoc1(Location loc, BlockState old) {
    this.loc1 = new Loc(loc);
    this.state1 = old;
  }

  public void setLoc2(Location loc, BlockState old) {
    this.loc2 = new Loc(loc);
    this.state2 = old;
  }

  public Loc getLoc1() {
    return this.loc1;
  }

  public Loc getLoc2() {
    return this.loc2;
  }

  public void clear() {
    this.loc1 = null;
    this.loc2 = null;
    if (this.state1 != null) {
      World w = this.state1.getWorld();
      w.playEffect(this.state1.getLocation(), Effect.STEP_SOUND, 153);

      this.state1.update(true);
    }
    if (this.state2 != null) {
      World w = this.state2.getWorld();
      w.playEffect(this.state2.getLocation(), Effect.STEP_SOUND, 153);

      this.state2.update(true);
    }
  }

  public void delay() {
    this.nextUse = (System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(1500L, TimeUnit.MILLISECONDS));
  }

  public boolean canUse() {
    return System.currentTimeMillis() >= this.nextUse;
  }

  public boolean hasLoc1() {
    return this.loc1 != null;
  }

  public GamePlayer getOwner() {
    return this.owner;
  }

  public boolean isLinked() {
    return (this.loc1 != null) && (this.loc2 != null);
  }
}

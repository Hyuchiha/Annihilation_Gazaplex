package com.hyuchiha.Annihilation.Event;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;

public class NexusDestroyEvent extends BaseEvent {
  private GamePlayer breaker;
  private GameTeam victim;

  public NexusDestroyEvent(GamePlayer breaker, GameTeam victim) {
    this.breaker = breaker;
    this.victim = victim;
  }


  public GamePlayer getBreaker() {
    return this.breaker;
  }


  public GameTeam getVictim() {
    return this.victim;
  }
}

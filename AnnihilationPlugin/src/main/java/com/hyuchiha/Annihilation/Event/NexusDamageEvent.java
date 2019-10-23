package com.hyuchiha.Annihilation.Event;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;

public class NexusDamageEvent extends BaseEvent {
    private GamePlayer breaker;
    private GameTeam team;

    public NexusDamageEvent(GamePlayer breaker, GameTeam team) {
        this.breaker = breaker;
        this.team = team;
    }


    public GamePlayer getBreaker() {
        return this.breaker;
    }


    public GameTeam getTeam() {
        return this.team;
    }
}

package com.hyuchiha.Annihilation.Event;

public class PhaseChangeEvent
        extends BaseEvent {
    private int currentPhase;
    private int nextPhase;

    public PhaseChangeEvent(int currentPhase, int nextPhase) {
        this.currentPhase = currentPhase;
        this.nextPhase = nextPhase;
    }


    public int getCurrentPhase() {
        return this.currentPhase;
    }


    public int getNextPhase() {
        return this.nextPhase;
    }
}

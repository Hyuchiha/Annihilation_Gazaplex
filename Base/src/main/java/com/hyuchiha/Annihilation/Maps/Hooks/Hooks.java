package com.hyuchiha.Annihilation.Maps.Hooks;

public interface Hooks {

    void preUnload(String world);

    void postUnload(String world);

    void preLoad(String world);

    void postLoad(String world);

}

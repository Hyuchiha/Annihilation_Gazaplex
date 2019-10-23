package com.hyuchiha.Annihilation.Manager;

import com.hyuchiha.Annihilation.Game.GameWitch;

import java.util.HashMap;


public class WitchManager {
    public static HashMap<String, GameWitch> witchs = new HashMap<>();


    public static void loadWitchs(HashMap<String, GameWitch> b) {
        witchs = b;
    }


    public static void clearWitch() {
        witchs.clear();
    }
}

package com.hyuchiha.Annihilation.Output;

import com.hyuchiha.Annihilation.Main;

public class Output {
    public static void log(String msg) {
        Main.getInstance().getLogger().info(msg);
    }


    public static void logError(String msg) {
        Main.getInstance().getLogger().severe(msg);
    }
}

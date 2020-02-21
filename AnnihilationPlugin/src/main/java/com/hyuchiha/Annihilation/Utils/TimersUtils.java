package com.hyuchiha.Annihilation.Utils;

import com.hyuchiha.Annihilation.Game.Kit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class TimersUtils {
    private static HashMap<String, HashMap<Kit, Long>> kitDelays = new HashMap<>();

    public static void addDelay(String player, Kit kit, int delay, TimeUnit unit) {
        HashMap<Kit, Long> playerDelays = kitDelays.get(player);
        if (playerDelays == null) {
            playerDelays = new HashMap<>();
        }
        playerDelays.put(kit, System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(delay, unit));
        kitDelays.put(player, playerDelays);
    }

    public static boolean hasExpired(String player, Kit type) {
        HashMap<Kit, Long> delays = kitDelays.get(player);
        if (delays == null) {
            return true;
        }
        Long x = delays.get(type);
        if (x == null) {
            return true;
        }
        return System.currentTimeMillis() > x;
    }

    public static String geDelayRemaining(Player player, Kit type) {
        HashMap<Kit, Long> delays = kitDelays.get(player.getName());
        if (delays == null) {
            return "0";
        }
        Long x = delays.get(type);
        if (x == null) {
            return "0";
        }

        long now = System.currentTimeMillis() - x;
        long time = TimeUnit.SECONDS.convert(now, TimeUnit.MILLISECONDS);

        return Double.toString(Math.abs(time));
    }

    public static void removeDelay(String player, Kit type) {
        HashMap<Kit, Long> playerDelays = kitDelays.get(player);
        if (playerDelays != null) {
            playerDelays.remove(type);
        }
    }
}

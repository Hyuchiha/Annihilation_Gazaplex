package com.hyuchiha.Annihilation.Utils;

import com.hyuchiha.Annihilation.Game.Kit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class TimersUtils {
    private static HashMap<String, HashMap<Kit, Long>> kitDelays = new HashMap<>();

    public static void addDelay(Player player, Kit kit, int delay, TimeUnit unit) {
        HashMap<Kit, Long> playerDelays = kitDelays.get(player.getUniqueId().toString());
        if (playerDelays == null) {
            playerDelays = new HashMap<>();
        }
        playerDelays.put(kit, System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(delay, unit));
        kitDelays.put(player.getUniqueId().toString(), playerDelays);
    }

    public static boolean hasExpired(Player player, Kit type) {
        HashMap<Kit, Long> delays = kitDelays.get(player.getUniqueId().toString());
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
        HashMap<Kit, Long> delays = kitDelays.get(player.getUniqueId().toString());
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

    public static long getRemainingMiliseconds(Player player, Kit type) {
        HashMap<Kit, Long> delays = kitDelays.get(player.getUniqueId().toString());
        if (delays == null) {
            return 0;
        }
        Long x = delays.get(type);
        if (x == null) {
            return 0;
        }

        long now = System.currentTimeMillis() - x;
        long time = TimeUnit.SECONDS.convert(now, TimeUnit.MILLISECONDS);

        return time;
    }

    public static void removeDelay(Player player, Kit type) {
        HashMap<Kit, Long> playerDelays = kitDelays.get(player.getUniqueId().toString());
        if (playerDelays != null) {
            playerDelays.remove(type);
        }
    }

    public static boolean hasDelay(Player player, Kit type) {
        HashMap<Kit, Long> playerDelays = kitDelays.get(player.getUniqueId().toString());
        return playerDelays != null && playerDelays.get(type) != null;
    }
}

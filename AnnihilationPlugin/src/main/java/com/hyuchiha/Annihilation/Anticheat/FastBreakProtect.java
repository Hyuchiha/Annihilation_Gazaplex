package com.hyuchiha.Annihilation.Anticheat;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class FastBreakProtect {
  private final static long MIN_TIME_BREAK = 700;
  private final static HashMap<String, Long> Delays = new HashMap<>();

  public static boolean LastBreakTimeIsCorrect(Player player) {
    String key = player.getUniqueId().toString();
    Long timeNow = System.currentTimeMillis();
    Long timePlayer = Delays.get(key);

    if (timePlayer == null) {
      Delays.put(key, timeNow);
      return true;
    } else {
      long result = timeNow - timePlayer;
      if (result >= MIN_TIME_BREAK) {
        Delays.put(key, timeNow);
        return true;
      } else {
        return false;
      }
    }
  }

  public static void clearPlayer(Player player) {
    Delays.remove(player.getUniqueId().toString());
  }

  public static void clearData() {
    Delays.clear();
  }

}
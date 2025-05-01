package com.hyuchiha.Annihilation.Utils;

import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.scoreboard.Team;
import org.inventivetalent.reflection.minecraft.Minecraft;
import org.inventivetalent.reflection.minecraft.MinecraftVersion;

public class BaseUtils {

  public static boolean isANewVersionSign(Block block) {
    BlockData data = block.getBlockData();
    return data instanceof Sign || data instanceof WallSign;
  }

  public static void safeSetTeamColor(Team team, ChatColor color) {
    try {
      team.setColor(color); // Solo en 1.13+
    } catch (NoSuchMethodError e) {
      // En versiones viejas, puedes aplicar el color al prefix
      team.setPrefix(color.toString() + team.getPrefix());
    }
  }

  public static void setWorldSettings(World world) {
    if (MinecraftVersion.getVersion().olderThan(Minecraft.Version.v1_13_R1)) {
      world.setGameRuleValue("doFireTick", "false");
      world.setGameRuleValue("doDaylightCycle", "false");
      world.setGameRuleValue("doMobSpawning", "false");
    } else {
      world.setGameRule(GameRule.DO_FIRE_TICK, false);
      world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
      world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
    }
  }
}

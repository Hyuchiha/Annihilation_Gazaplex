package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Anticheat.FastBreakProtect;
import com.hyuchiha.Annihilation.Event.NexusDamageEvent;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import com.hyuchiha.Annihilation.Utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

  @EventHandler(ignoreCancelled = true)
  public void onBuild(BlockPlaceEvent e) {
    if (GameManager.getCurrentGame().isInGame() && GameManager.getCurrentGame().getPhase() > 0) {

      if (LocationUtils.isEmptyColumn(e.getBlock().getLocation())) {
        e.setCancelled(true);
        e.getPlayer().sendMessage(Translator.getColoredString("ERRORS.CANT_BUILD_OUTSIDE"));

        return;
      }
      if (GameUtils.tooClose(e.getBlock().getLocation()) && e
                                                                .getPlayer().hasPermission("annihilation.bypass.construction")) {
        e.setCancelled(true);
        e.getPlayer().sendMessage(Translator.getColoredString("ERRORS.TOO_CLOSE_NEXUS"));

        return;
      }
      if (e.getBlock().getType() == Material.STONE || e.getBlock().getType() == Material.COBBLESTONE || e.getBlock().getType() == Material.CLAY) {
        e.setCancelled(true);
      }
    } else if (!e.getPlayer().hasPermission("annihilation.bypass.construction")) {
      e.setCancelled(true);
    }
  }


  @EventHandler(ignoreCancelled = true)
  public void onBreak(BlockBreakEvent e) {
    Block b = e.getBlock();
    if (GameUtils.hasSignAttached(b) &&
            GameUtils.isShopSignAttached(b)) {
      e.setCancelled(true);
    }


    if (LocationUtils.isEmptyColumn(e.getBlock().getLocation()) && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
      e.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBreakBlockNexus(BlockBreakEvent event) {
    if (GameManager.getCurrentGame().isInGame() && GameManager.getCurrentGame().getPhase() > 0) {
      for (GameTeam team : GameTeam.teams()) {
        if (team.getNexus().getLocation().equals(event.getBlock().getLocation())) {
          event.setCancelled(true);

          if (team.getNexus().isAlive() &&
                  FastBreakProtect.LastBreakTimeIsCorrect(event.getPlayer())) {
            Bukkit.getServer().getPluginManager().callEvent(new NexusDamageEvent(
                PlayerManager.getGamePlayer(event.getPlayer()), team));
          }


          return;
        }
      }


      if (GameUtils.tooClose(event.getBlock().getLocation()) &&
              !event.getPlayer().hasPermission("annihilation.bypass.construction") &&
              !permittedBreak(event.getBlock().getType())) {
        event.getPlayer().sendMessage(
            Translator.getColoredString("ERRORS.TOO_CLOSE_NEXUS"));

        event.setCancelled(true);
      } else if (!event.getPlayer().hasPermission("annihilation.bypass.construction")) {
        event.setCancelled(true);
      }
    }
  }


  @EventHandler
  public void onCraftingBlockBreak(BlockBreakEvent e) {
    Player player = e.getPlayer();
    Block b = e.getBlock();
    GamePlayer meta = PlayerManager.getGamePlayer(player);
    for (Block bo : GameManager.getCurrentGame().getCrafting().values()) {
      if (LocationUtils.isSameBlock(b, bo) && GameUtils.isBlockTeam(meta.getTeam())) {
        e.setCancelled(true);
      }
    }
  }

  private boolean permittedBreak(Material material) {
    switch (material) {
      case ENDER_STONE:
      case MELON_BLOCK:
      case LOG:
      case LOG_2:
      case QUARTZ_ORE:
        return true;
    }
    return false;
  }
}

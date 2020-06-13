package com.hyuchiha.Annihilation.Kits.Implementations;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Utils.TimersUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class Acrobat extends BaseKit {
  // https://shotbow.net/forum/wiki/anni-acrobat/
  public Acrobat(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);
  }

  @Override
  protected void setupSpawnItems() {
    spawnItems.add(new ItemStack(Material.WOOD_SWORD));
    spawnItems.add(new ItemStack(Material.WOOD_PICKAXE));
    spawnItems.add(new ItemStack(Material.WOOD_AXE));

    spawnItems.add(new ItemStack(Material.BOW));
    spawnItems.add(new ItemStack(Material.ARROW, 6));

    Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {

      for (Player player : Bukkit.getOnlinePlayers()) {
        GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

        if (gPlayer.getKit() == Kit.ACROBAT
            && GameManager.hasCurrentGame()
            && GameManager.getCurrentGame().isInGame()
            && TimersUtils.getRemainingMiliseconds(player, Kit.ACROBAT) == 0) {

          // player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0F, 2.0F);
          player.setAllowFlight(true);
        }

      }

    }, 20L, 20L);
  }

  @Override
  protected void giveSpecialPotions(Player recipient) {
    // Noup this kits doesn't have special potions
  }

  @Override
  protected void giveExtraHearts(Player recipient) {
    // Nope
  }

  @Override
  protected void extraConfiguration(Player recipient) {
    // Noupe
  }

  @Override
  public void removePlayer(Player recipient) {
    recipient.setAllowFlight(false);
    recipient.setFlying(false);
  }

  @Override
  public void resetData() {
    // This kit dont store anything soo, dont worry
  }

  @EventHandler
  public void onGameModeChange(PlayerGameModeChangeEvent event) {
    Player player = event.getPlayer();

    if (event.getNewGameMode() == GameMode.SURVIVAL) {
      GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

      if (gPlayer.getKit() == Kit.ACROBAT
          && GameManager.hasCurrentGame()
          && GameManager.getCurrentGame().isInGame()) {
        player.setAllowFlight(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void acrobatDoubleJump(PlayerToggleFlightEvent event) {
    Player player = event.getPlayer();
    if (player.getGameMode() != GameMode.CREATIVE) {
      GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

      if (GameManager.hasCurrentGame()
          && GameManager.getCurrentGame().isInGame()
          && gPlayer.getKit() == Kit.ACROBAT
          && TimersUtils.hasExpired(player, Kit.ACROBAT)) {

        TimersUtils.addDelay(player, Kit.ACROBAT, 10, TimeUnit.SECONDS);

        event.setCancelled(true);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setVelocity(player.getLocation().getDirection().setY(1).multiply(1));
        player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 1.0F, 2.0F);
      } else {
        player.setAllowFlight(false);
        player.setFlying(false);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void acrobatJumpMonitor(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    if (player.getGameMode() != GameMode.CREATIVE) {
      if (player.isFlying()) {
        player.setAllowFlight(false);
        player.setFlying(false);
        return;
      }

      if (GameManager.hasCurrentGame() && GameManager.getCurrentGame().isInGame()) {
        if (!player.getAllowFlight()) {
          GamePlayer gPlayer = PlayerManager.getGamePlayer(player);
          if (gPlayer.getKit() == Kit.ACROBAT) {
            if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR && !TimersUtils.hasExpired(player, Kit.ACROBAT)) {
              player.setAllowFlight(true);
            }
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void fallDamage(EntityDamageEvent event) {
    if (event.getEntity().getType() == EntityType.PLAYER && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
      Player p = (Player) event.getEntity();
      GamePlayer gPlayer = PlayerManager.getGamePlayer(p);

      if (gPlayer.getKit() == Kit.ACROBAT) {
        event.setCancelled(true);
      }
    }
  }

  // This is experimental, need to test things
//  @EventHandler(priority = EventPriority.HIGH)
//  public void onPlayerSprint(PlayerToggleSprintEvent event) {
//    Player player = event.getPlayer();
//    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);
//
//    if (event.isSprinting()
//            && gPlayer.getKit() == Kit.ACROBAT
//            && TimersUtils.hasDelay(player, Kit.ACROBAT)
//            && TimersUtils.hasExpired(player.getUniqueId().toString(), Kit.ACROBAT)) {
//
//      if (player.getFoodLevel() <= 6) {
//        Output.log("Can sprint");
//        player.setSprinting(true);
//      } else {
//        event.setCancelled(true);
//      }
//    }
//  }

}

package com.hyuchiha.Annihilation.Utils;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.ActionBar;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KitUtils {

  public static void selectKit(String className, Player player) {
    Output.log("Player select different kit: " + className);

    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

    Kit selectedKit = Kit.valueOf(ChatColor.stripColor(className).toUpperCase());

    if (!selectedKit.isOwnedBy(player)) {
      player.sendMessage(ChatColor.RED + Translator.getColoredString("GAME.KIT_NOT_PURCHASED"));
      return;
    }

    boolean isLobby = player.getWorld() == Bukkit.getServer().getWorld("lobby");
    gPlayer.getKit().getKit().removePlayer(player);

    gPlayer.setKit(selectedKit);
    String message = Translator.getColoredString("GAME.KIT_SELECTED").replace("%KIT%", ChatColor.stripColor(className));
    player.sendMessage(ChatColor.DARK_AQUA + message);

    if (!isLobby) {
      player.setAllowFlight(false);
      player.setFlying(false);
      gPlayer.regamePlayer();
    }
  }

  public static boolean isKitItem(ItemStack item, String translation) {
    return item.hasItemMeta()
        && item.getItemMeta().hasDisplayName()
        && item.getItemMeta().getDisplayName().equals(Translator.getColoredString(translation));
  }

  public static void showKitItemDelay(Player player, Kit kit) {
    String remainingTime = TimersUtils.geDelayRemaining(player, kit);
    String message = Translator.getColoredString("GAME.DELAY").replace("%TIME%", remainingTime);
    //player.sendMessage(message);
    ActionBar.sendActionBar(player, message);
    XSound.ENTITY_WOLF_GROWL.play(player, 2.0F, 1.0F);
  }

  public static void setBlockOwner(Block block, UUID idenfitier) {
    MetadataValue value = new FixedMetadataValue(Main.getInstance(), idenfitier.toString());
    block.setMetadata("Owner", value);
  }

  public static UUID getBlockOwner(Block block) {
    List<MetadataValue> values = block.getMetadata("Owner");
    if (values == null || values.isEmpty()) {
      return null;
    }
    String uuid = values.get(0).asString();
    return UUID.fromString(uuid);
  }

  public static Player getTarget(Player player, Integer RANGE, boolean differentTeam) {
    List<Entity> nearbyE = player.getNearbyEntities(RANGE, RANGE, RANGE);
    List<Player> entities = new ArrayList<>();

    for (Entity e : nearbyE) {
      if (e instanceof Player) {
        entities.add((Player) e);
      }
    }

    Player target = null;
    BlockIterator bItr = new BlockIterator(player, RANGE);
    Block block;
    Location loc;
    int bx, by, bz;
    double ex, ey, ez;

    // loop through player's line of sight
    while (bItr.hasNext()) {
      block = bItr.next();
      bx = block.getX();
      by = block.getY();
      bz = block.getZ();
      // check for entities near this block in the line of sight
      for (Player e : entities) {
        loc = e.getLocation();
        ex = loc.getX();
        ey = loc.getY();
        ez = loc.getZ();
        if ((bx - .75 <= ex && ex <= bx + 1.75) && (bz - .75 <= ez && ez <= bz + 1.75) && (by - 1 <= ey && ey <= by + 2.5)) {
          // entity is close enough

          // If looking for player from different team
          if (differentTeam) {
            GamePlayer gpTarget = PlayerManager.getGamePlayer(e);
            GamePlayer gpPayer = PlayerManager.getGamePlayer(player);

            // different team, set target and stop
            if (gpPayer.getTeam() != gpTarget.getTeam()) {
              target = e;
              break;
            }

          } else {
            // set target and stop
            target = e;
            break;
          }
        }
      }
    }

    return target;
  }

}

package com.hyuchiha.Annihilation.Utils;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.UUID;

public class KitUtils {

  public static void selectKit(String className, Player player) {
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
    player.sendMessage(message);
    player.playSound(player.getLocation(), Sound.ENTITY_WOLF_GROWL, 2.0f, 1.0f);
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

}

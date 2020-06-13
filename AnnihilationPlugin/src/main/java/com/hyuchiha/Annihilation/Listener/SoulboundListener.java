package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Iterator;

public class SoulboundListener implements Listener {
  private static final String soulboundTag = ChatColor.GOLD + "Soulbound";

  public static boolean isSoulbound(ItemStack item) {
    try {
      ItemMeta meta = item.getItemMeta();
      if (item.hasItemMeta() &&
          meta.hasLore() &&
          meta.getLore().contains(soulboundTag)) {
        return true;

      }
    } catch (NullPointerException e) {
      Output.logError(e.getLocalizedMessage());
    }
    return false;
  }

  public static void soulbind(ItemStack stack) {
    ItemMeta meta = stack.getItemMeta();
    if (!meta.hasLore()) {
      meta.setLore(Collections.singletonList(soulboundTag));
    } else {
      meta.getLore().add(soulboundTag);
    }
    stack.setItemMeta(meta);
  }

  @EventHandler
  public void onSoulboundDrop(PlayerDropItemEvent e) {
    if (isSoulbound(e.getItemDrop().getItemStack())) {
      Player p = e.getPlayer();
      p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_HURT, 1.0F, 0.25F);
      e.getItemDrop().remove();
    }
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e) {
    Iterator<ItemStack> it = e.getDrops().iterator();
    while (it.hasNext()) {
      if (isSoulbound(it.next())) {
        it.remove();
      }
    }
  }
}

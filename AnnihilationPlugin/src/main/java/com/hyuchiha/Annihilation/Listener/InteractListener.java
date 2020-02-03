package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Utils.MenuUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class InteractListener implements Listener {

  @EventHandler
  public void onItemInteract(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);
    Action action = e.getAction();
    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
      PlayerInventory inventory = player.getInventory();
      ItemStack handItem = inventory.getItemInMainHand();

      if (handItem != null) {
        switch (handItem.getType()) {
          case ENCHANTED_BOOK:
            // FOR Kits
            if (handItem.getItemMeta().hasDisplayName()
                    && handItem.getItemMeta().getDisplayName().equals(Translator.getColoredString("GAME.CLICK_TO_CHOOSE_KIT"))) {
              MenuUtils.showKitSelector(player);
            }
            break;
          case GOLD_INGOT:
            // For unlock kits
            break;
          case COMPASS:
            boolean setCompass = false;
            boolean setToNext = false;
            int count =0;
            while (!setCompass || count > 6) {
              if (!handItem.getItemMeta().hasDisplayName() || player.getWorld().getName().equals("lobby")) {
                setCompass = true;
              } else {
                count++;
                for (GameTeam team : GameTeam.teams()) {
                  if (setToNext) {
                    ItemMeta meta = handItem.getItemMeta();
                    meta.setDisplayName(team.color() + Translator.getColoredString("GAME.COMPASS_FOCUS").replace("%TEAM%", team.toString()));
                    handItem.setItemMeta(meta);
                    player.setCompassTarget(team.getNexus().getLocation());
                    setCompass = true;
                    break;
                  }
                  if (handItem.getItemMeta().getDisplayName().contains(team.toString())) {
                    setToNext = true;
                  }
                }
              }
            }
            break;
          case NETHER_STAR:
            if (handItem.getItemMeta().hasDisplayName()
                    && handItem.getItemMeta().getDisplayName().equals(Translator.getColoredString("GAME.BOSS_STAR"))) {

              MenuUtils.openBossStarMenu(player);

              if(inventory.getItemInMainHand().getAmount() == 1){
                player.getInventory().remove(handItem);
              }else{
                int currentAmount = handItem.getAmount();
                inventory.getItemInMainHand().setAmount(currentAmount - 1);
              }
            }
        }
      }
    }
  }

}

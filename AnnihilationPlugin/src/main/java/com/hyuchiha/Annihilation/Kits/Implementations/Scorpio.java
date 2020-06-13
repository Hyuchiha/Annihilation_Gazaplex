package com.hyuchiha.Annihilation.Kits.Implementations;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Object.HookTracer;
import com.hyuchiha.Annihilation.Utils.KitUtils;
import com.hyuchiha.Annihilation.Utils.TimersUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class Scorpio extends BaseKit {
  public Scorpio(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);
  }

  @Override
  protected void setupSpawnItems() {
    spawnItems.add(new ItemStack(Material.STONE_SWORD));
    spawnItems.add(new ItemStack(Material.WOOD_PICKAXE));
    spawnItems.add(new ItemStack(Material.WOOD_AXE));

    ItemStack star = new ItemStack(Material.NETHER_STAR);
    ItemMeta meta = star.getItemMeta();
    meta.setDisplayName(Translator.getColoredString("KITS.SCORPIO_ITEM"));
    star.setItemMeta(meta);
    spawnItems.add(star);
  }

  @Override
  protected void giveSpecialPotions(Player recipient) {
    // Not needed
  }

  @Override
  protected void giveExtraHearts(Player recipient) {
    // Not needed
  }

  @Override
  protected void extraConfiguration(Player recipient) {
    // Not needed
  }

  @Override
  public void removePlayer(Player recipient) {
    // not needed
  }

  @Override
  public void resetData() {
    // Nope
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onStarInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);
    Action action = event.getAction();

    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
      PlayerInventory inventory = player.getInventory();
      ItemStack handItem = inventory.getItemInMainHand();

      if (handItem != null && KitUtils.isKitItem(handItem, "KITS.SCORPIO_ITEM") &&
          gPlayer.getKit() == Kit.SCORPIO) {

        if (TimersUtils.hasExpired(player, Kit.BUILDER)) {
          Item item = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.NETHER_STAR, 1));
          item.setPickupDelay(Integer.MAX_VALUE);
          item.setVelocity(player.getEyeLocation().getDirection().multiply(2));
          Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new HookTracer(item, gPlayer, 90), 1);
        }

      }
    }
  }
}

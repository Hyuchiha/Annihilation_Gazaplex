package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Resource;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Manager.ResourceManager;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Random;

public class ResourceListener implements Listener {
  private final HashSet<Location> queue;

  private Main plugin;
  private Random rand;

  public ResourceListener(Main plugin) {
    this.queue = new HashSet<>();
    this.rand = new Random();


    this.plugin = plugin;
  }

  @EventHandler(ignoreCancelled = true)
  public void onResourceBreak(BlockBreakEvent e) {
    if (e.getPlayer().getWorld().getName().equalsIgnoreCase("lobby") && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
      e.setCancelled(true);

      return;
    }

    if (GameUtils.tooClose(e.getBlock().getLocation())
            && e.getBlock().getType() != Material.MELON_BLOCK
            && e.getBlock().getType() != Material.QUARTZ_ORE
            && e.getBlock().getType() != Material.LOG
            && e.getBlock().getType() != Material.LOG_2) {
      e.setCancelled(true);

      return;
    }

    if (ResourceManager.containsResource(e.getBlock().getType())) {
      e.setCancelled(true);
      breakResource(e.getPlayer(), e.getBlock());
      e.getBlock().getWorld().playEffect(e.getBlock().getLocation(), Effect.STEP_SOUND, e.getBlock().getTypeId());
    } else if (this.queue.contains(e.getBlock().getLocation())) {
      e.setCancelled(true);
    }
  }

  @EventHandler(ignoreCancelled = true)
  public void placeResource(BlockPlaceEvent e) {
    if (ResourceManager.containsResource(e.getBlock().getType()))
      e.setCancelled(true);
  }

  private void breakResource(Player player, Block block) {
    int qty;
    Material dropType;
    ItemStack sack;
    ItemStack[] drops;
    Material type = block.getType();
    Resource resource = ResourceManager.getResource(type);

    switch (type) {
      case GRAVEL:
        drops = getGravelDrops();
        for (ItemStack stack : drops) {
          if (stack.getAmount() > 0) {
            player.getInventory().addItem(stack);
          }
        }
        break;
      case LAPIS_ORE:
        sack = new ItemStack(Material.INK_SACK.getId(), 5, (short) 0, (byte) 4);
        player.getInventory().addItem(sack);
        break;
      default:
        dropType = resource.getDrop();
        qty = getDropQuantity(type);


        if ((dropType == Material.DIAMOND
                 || dropType == Material.COAL
                 || dropType == Material.EMERALD
                 || dropType == Material.REDSTONE)
                && player.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
          player.getInventory().addItem(new ItemStack(dropType, qty * 2));
          break;
        }
        player.getInventory().addItem(new ItemStack(dropType, qty));
        break;
    }

    if (resource.getXp() > 0) {
      GamePlayer gamePlayer = PlayerManager.getGamePlayer(player);


      player.giveExp(resource.getXp());
      player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, this.rand.nextFloat() * 0.2F + 0.9F);
    }

    queueRespawn(block);
  }

  private void queueRespawn(final Block block) {
    final Material type = block.getType();
    final byte data = block.getData();
    block.setType(getRespawnMaterial(type));
    this.queue.add(block.getLocation());

    this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
      block.setType(type);
      block.setData(data);
      ResourceListener.this.queue.remove(block.getLocation());
      block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
    }, ResourceManager.getResource(type).getDelay() * 20L);
  }

  private Material getRespawnMaterial(Material type) {
    switch (type) {
      case LOG:
      case LOG_2:
      case MELON_BLOCK:
        return Material.AIR;
    }
    return Material.COBBLESTONE;
  }


  private ItemStack[] getGravelDrops() {
    ItemStack arrows = new ItemStack(Material.ARROW, Math.max(this.rand
                                                                  .nextInt(5) - 2, 0));
    ItemStack flint = new ItemStack(Material.FLINT, Math.max(this.rand
                                                                 .nextInt(4) - 2, 0));
    ItemStack feathers = new ItemStack(Material.FEATHER, Math.max(this.rand
                                                                      .nextInt(4) - 2, 0));
    ItemStack string = new ItemStack(Material.STRING, Math.max(this.rand
                                                                   .nextInt(5) - 3, 0));
    ItemStack bones = new ItemStack(Material.BONE, Math.max(this.rand
                                                                .nextInt(4) - 2, 0));
    return new ItemStack[]{arrows, flint, feathers, string, bones};
  }

  private int getDropQuantity(Material type) {
    switch (type) {
      case MELON_BLOCK:
        return 3 + this.rand.nextInt(5);
      case REDSTONE_ORE:
      case GLOWING_REDSTONE_ORE:
        return 4 + (this.rand.nextBoolean() ? 1 : 0);
    }
    return 1;
  }
}

package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Resource;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Manager.ResourceManager;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collection;
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

  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
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
    PlayerInventory inventory = player.getInventory();
    ItemStack itemInHand = inventory.getItemInMainHand();

    Material type = block.getType();
    Resource resource = ResourceManager.getResource(type);
    GamePlayer gamePlayer = PlayerManager.getGamePlayer(player);

    ItemStack[] drops;

    switch (type) {
      case GRAVEL:
        drops = getGravelDrops();
        break;
      case LAPIS_ORE:
        ItemStack sack = new ItemStack(Material.INK_SACK, 5, (short) 0, (byte) 4);
        drops = new ItemStack[]{sack};
        break;
      default:
        Material dropType = resource.getDrop();
        int qty = getDropQuantity(type);
        int dropMultiplier = gamePlayer.getKit().getKit().getMaterialDropMultiplier();

        if ((dropType == Material.DIAMOND
                || dropType == Material.COAL
                || dropType == Material.EMERALD
                || dropType == Material.REDSTONE)
                && itemInHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {

          qty = quantityDroppedWithBonus(type, itemInHand);
        }

        drops = new ItemStack[]{
                new ItemStack(dropType, qty * dropMultiplier)
        };
        break;
    }

    gamePlayer.giveOreDrops(drops);
    gamePlayer.giveOreXP(resource.getXp());

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
        return 3 + rand.nextInt(5);
      case REDSTONE_ORE:
      case GLOWING_REDSTONE_ORE:
        return 4 + (rand.nextBoolean() ? 1 : 0);
      default:
        return 1;
    }
  }

  private int quantityDroppedWithBonus(Material type, ItemStack itemInHand) {
    int fortune = itemInHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
    Random random = new Random();

    if (fortune < 1) {
      return getDropQuantity(type);
    }

    int i = random.nextInt(fortune + 2) - 1;
    if (i < 0) {
      i = 0;
    }
    return getDropQuantity(type) * (i + 1);
  }
}

package com.hyuchiha.Annihilation.Listener;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Resource;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Manager.ResourceManager;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

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
        && e.getBlock().getType() != XMaterial.MELON.get()
        && e.getBlock().getType() != XMaterial.NETHER_QUARTZ_ORE.get()
        && e.getBlock().getType() != XMaterial.BIRCH_LOG.get()
        && e.getBlock().getType() != XMaterial.JUNGLE_LOG.get()
        && e.getBlock().getType() != XMaterial.OAK_LOG.get()
        && e.getBlock().getType() != XMaterial.SPRUCE_LOG.get()
        && e.getBlock().getType() != XMaterial.ACACIA_LOG.get()
        && e.getBlock().getType() != XMaterial.DARK_OAK_LOG.get()
        && e.getBlock().getType() != XMaterial.STRIPPED_ACACIA_LOG.get()
        && e.getBlock().getType() != XMaterial.STRIPPED_BIRCH_LOG.get()
        && e.getBlock().getType() != XMaterial.STRIPPED_DARK_OAK_LOG.get()
        && e.getBlock().getType() != XMaterial.STRIPPED_JUNGLE_LOG.get()
        && e.getBlock().getType() != XMaterial.STRIPPED_OAK_LOG.get()
        && e.getBlock().getType() != XMaterial.STRIPPED_SPRUCE_LOG.get()) {
      e.setCancelled(true);

      ItemStack itemInHand = e.getPlayer().getInventory().getItemInMainHand();

      Output.log("Item in hand: " + itemInHand);

      if (itemInHand != null) {
        GameUtils.damageItem(itemInHand, 4);

        e.getPlayer().updateInventory(); // Update the inventory to reflect changes
      }

      return;
    }

    if (ResourceManager.containsResource(e.getBlock().getType())) {
      e.setCancelled(true);
      breakResource(e.getPlayer(), e.getBlock());
      e.getBlock().getWorld().playEffect(e.getBlock().getLocation(), Effect.STEP_SOUND, e.getBlock().getType(), 10);

      ItemStack itemInHand = e.getPlayer().getInventory().getItemInMainHand();

      if (itemInHand != null) {
        GameUtils.damageItem(itemInHand, 4);

        e.getPlayer().updateInventory(); // Update the inventory to reflect changes
      }

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
        ItemStack sack = new ItemStack(XMaterial.INK_SAC.get(), 5, (short) 0, (byte) 4);
        drops = new ItemStack[]{sack};
        break;
      default:
        Material dropType = resource.getDrop();

        boolean isOre = dropType == Material.DIAMOND_ORE
                || dropType == Material.EMERALD_ORE
                || dropType == Material.GOLD_ORE
                || dropType == Material.IRON_ORE
                || dropType == Material.COAL_ORE
                || dropType == Material.REDSTONE_ORE;

        int qty = getDropQuantity(type);
        int dropMultiplier = isOre ? gamePlayer.getKit().getKit().getMaterialDropMultiplier() : 1;

        if (isOre && itemInHand.containsEnchantment(XEnchantment.LOOTING.get())) {
          qty = quantityDroppedWithBonus(type, itemInHand);
        }

        drops = new ItemStack[]{
            new ItemStack(dropType, qty * dropMultiplier)
        };
        break;
    }

    gamePlayer.giveOreDrops(drops);
    gamePlayer.giveOreXP(resource.getXp());


    // Restore item durability after breaking the block
    ItemMeta meta = itemInHand.getItemMeta();

    if (meta instanceof Damageable) { // Check if the item is damageable
      Damageable damageable = (Damageable) meta;

      int currentDamage = damageable.getDamage(); // Get the current damage
      if (currentDamage > 0) {
        damageable.setDamage(currentDamage - 1); // Reduce damage by 1 point
        itemInHand.setItemMeta(meta); // Apply the changes to the item
        player.updateInventory(); // Update the inventory to reflect changes
      }
    }

    queueRespawn(block);
  }

  private void queueRespawn(final Block block) {
    final Material type = block.getType();

    block.setType(getRespawnMaterial(type));
    this.queue.add(block.getLocation());

    this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
      block.setType(type);

      this.queue.remove(block.getLocation());

      block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType(), 10);
    }, ResourceManager.getResource(type).getDelay() * 20L);
  }

  private Material getRespawnMaterial(Material type) {
    XMaterial materialType = XMaterial.matchXMaterial(type);
    switch (materialType) {
      case ACACIA_LOG:
      case BIRCH_LOG:
      case JUNGLE_LOG:
      case OAK_LOG:
      case SPRUCE_LOG:
      case DARK_OAK_LOG:
      case STRIPPED_ACACIA_LOG:
      case STRIPPED_BIRCH_LOG:
      case STRIPPED_DARK_OAK_LOG:
      case STRIPPED_JUNGLE_LOG:
      case STRIPPED_OAK_LOG:
      case STRIPPED_SPRUCE_LOG:
      case MELON:
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
    XMaterial materialType = XMaterial.matchXMaterial(type);
    switch (materialType) {
      case MELON:
        return 3 + rand.nextInt(5);
      case REDSTONE_ORE:
        return 4 + (rand.nextBoolean() ? 1 : 0);
      default:
        return 1;
    }
  }

  private int quantityDroppedWithBonus(Material type, ItemStack itemInHand) {
    int fortune = itemInHand.getEnchantmentLevel(XEnchantment.LOOTING.get());
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

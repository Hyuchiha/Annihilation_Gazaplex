package com.hyuchiha.Annihilation.Kits.Implementations;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XSound;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import com.hyuchiha.Annihilation.Utils.KitUtils;
import com.hyuchiha.Annihilation.Utils.LocationUtils;
import com.hyuchiha.Annihilation.Utils.TimersUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Builder extends BaseKit {
  private final List<ItemStack> drops;
  private HashMap<UUID, List<Block>> delayBlocks;
  private final Random random = new Random();

  // https://wiki.shotbow.net/Builder
  public Builder(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);

    drops = new ArrayList<>();
    delayBlocks = new HashMap<>();

    fillDrops();
  }

  private void fillDrops() {
    drops.add(new ItemStack(XMaterial.GLASS.get(), 20));
    drops.add(new ItemStack(XMaterial.SPRUCE_STAIRS.get(), 20));
    drops.add(new ItemStack(XMaterial.BIRCH_PLANKS.get(), 70));
    drops.add(new ItemStack(XMaterial.SPRUCE_FENCE.get(), 10));
    drops.add(new ItemStack(XMaterial.TORCH.get(), 5));
    drops.add(new ItemStack(XMaterial.BRICK.get(), 40));
    drops.add(new ItemStack(XMaterial.STONE.get(), 50));
    drops.add(new ItemStack(XMaterial.WHITE_WOOL.get(), 30));
    drops.add(new ItemStack(XMaterial.DIRT.get(), 60));
    drops.add(new ItemStack(XMaterial.IRON_BARS.get(), 10));
  }

  @Override
  protected void setupSpawnItems() {
    spawnItems.add(XMaterial.WOODEN_SWORD.parseItem());
    spawnItems.add(XMaterial.WOODEN_PICKAXE.parseItem());
    spawnItems.add(XMaterial.WOODEN_AXE.parseItem());
    spawnItems.add(XMaterial.WOODEN_SHOVEL.parseItem());

    ItemStack book = new ItemStack(Material.BOOK, 1);
    ItemMeta bookMeta = book.getItemMeta();
    bookMeta.setDisplayName(Translator.getColoredString("KITS.BUILDER.ITEM"));
    bookMeta.setLore(Translator.getMultiMessage("KITS.BUILDER.DESC"));
    book.setItemMeta(bookMeta);
    spawnItems.add(book);

    spawnItems.add(getDelayingBlocks(2));
  }

  @Override
  protected void giveSpecialPotions(Player recipient) {
    // Nope, not needed
  }

  @Override
  protected void giveExtraHearts(Player recipient) {
    // Still not needed
  }

  @Override
  protected void extraConfiguration(Player recipient) {
    // I think no
  }

  @Override
  public void removePlayer(Player recipient) {
    // We remove delaying blocks
    List<Block> playerBlocks = delayBlocks.get(recipient.getUniqueId());
    if (playerBlocks != null) {
      for (Block block : playerBlocks) {
        block.setType(Material.STONE);
      }

      delayBlocks.remove(recipient.getUniqueId());
    }
  }

  @Override
  public void resetData() {
    // Reset delaying blocks
    delayBlocks.clear();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBuilderBookInteract(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);
    Action action = e.getAction();

    EquipmentSlot handUser = e.getHand();
    if (handUser != EquipmentSlot.HAND) {
      return;
    }

    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
      PlayerInventory inventory = player.getInventory();
      ItemStack handItem = inventory.getItemInMainHand();

      if (handItem != null && KitUtils.isKitItem(handItem, "KITS.BUILDER.ITEM")
          && gPlayer.getKit() == Kit.BUILDER) {

        if (TimersUtils.hasExpired(player, Kit.BUILDER)) {
          List<ItemStack> drops = getRandomDrops();

          if (random.nextInt(100) < 30) {
            drops.add(getDelayingBlocks(1));
          }

          createInventoryMenu(player, drops);

          TimersUtils.addDelay(player, Kit.BUILDER, 90, TimeUnit.SECONDS);
        } else {
          KitUtils.showKitItemDelay(player, gPlayer.getKit());
        }
      }
    }
  }

  @EventHandler()
  public void onPlaceBlock(BlockPlaceEvent event) {
    Player p = event.getPlayer();
    GamePlayer gPlayer = PlayerManager.getGamePlayer(p);

    if (gPlayer.getKit() == Kit.BUILDER) {
      p.giveExp(1);

      if (event.getBlock().getType() == Material.SEA_LANTERN && event.getItemInHand().hasItemMeta()) {
        ItemMeta blockMeta = event.getItemInHand().getItemMeta();

        if (blockMeta.getDisplayName().equals(Translator.getColoredString("KITS.DELAYING_BLOCK.ITEM"))) {
          List<Block> blocks = delayBlocks.get(p.getUniqueId());

          if (blocks == null) {
            blocks = new ArrayList<>();
          }

          blocks.add(event.getBlockPlaced());

          delayBlocks.put(p.getUniqueId(), blocks);
        }
      }

    }

  }

  @EventHandler()
  public void onBlockBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();
    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

    Block block = event.getBlock();

//    if (block.getType() != Material.SEA_LANTERN) {
//      return;
//    }

    // Get all players with builder class
    for (UUID uuid : delayBlocks.keySet()) {
      Player blockPlayer = Bukkit.getPlayer(uuid);
      GamePlayer gpBlock = PlayerManager.getGamePlayer(blockPlayer);

      // Here store a delaying block if near to one
      Block blockFound = null;

      // Get the list of block from player
      List<Block> blocks = delayBlocks.get(uuid);
      // Iterate over the player blocks
      for (Block playerBlock : blocks) {
        // Check if the block destroyed is near to a player block
        if (GameUtils.blockNearBlock(block, playerBlock, 5)) {
          blockFound = playerBlock;
          Output.log("Block found");
          break;
        }
      }

      // After iterate if the variable has some value we check other things
      if (blockFound != null) {
        Output.log("Checking who break it");

        // Check if the breaker and the block owner are from same team
        if (gpBlock.getTeam() == gPlayer.getTeam()) {

          if (LocationUtils.isSameBlock(blockFound, block)) {
            // if the breaker is different from the owner of the block, the event is cancelled
            if (player.getUniqueId() != uuid) {
              Output.log("Another user broke the block");
              event.setCancelled(true);
            } else {
              Output.log("Cancelled");
              event.setCancelled(true);
              block.setType(Material.AIR);

              blocks.remove(blockFound);
            }

          }

        } else {
          // if is from other team, we play the delaying block effect
          playDelayingBlockEffect(player);
        }

        break;
      }

    }

  }

  @EventHandler()
  public void onInventoryMoveEvent(InventoryMoveItemEvent event) {
    Inventory source = event.getSource();
    Inventory destination = event.getDestination();

    ItemStack item = event.getItem();
    if (source.getType() == InventoryType.PLAYER
        && destination.getType() != InventoryType.PLAYER
        && KitUtils.isKitItem(item, "KITS.BUILDER.ITEM")) {
      event.setCancelled(true);
    }
  }

  private void playDelayingBlockEffect(Player player) {
    Location location = player.getLocation();
    XSound.ENTITY_GHAST_HURT.play(location, 10, 1);

    player.addPotionEffect(XPotion.SLOWNESS.buildPotionEffect(20 * 10, 1));
    player.addPotionEffect(XPotion.MINING_FATIGUE.buildPotionEffect(20 * 10, 2));
  }

  private List<ItemStack> getRandomDrops() {
    LinkedList<ItemStack> shuffledMaterials = new LinkedList<>(drops);
    Collections.shuffle(shuffledMaterials);
    return shuffledMaterials.subList(0, Math.min(5, 8));
  }

  public void createInventoryMenu(Player player, List<ItemStack> drops) {
    int size = 9 * (int) Math.ceil(drops.size() / 9.0D);
    Inventory inv = Bukkit.createInventory(player, size, Translator.getColoredString("KITS.BUILDER.MENU"));

    for (ItemStack drop : drops) {
      inv.addItem(drop);
    }

    player.openInventory(inv);
  }

  private ItemStack getDelayingBlocks(int quantity) {
    ItemStack delayingBlock = new ItemStack(Material.SEA_LANTERN, quantity);
    ItemMeta delayingBlockMeta = delayingBlock.getItemMeta();
    delayingBlockMeta.setDisplayName(Translator.getColoredString("KITS.DELAYING_BLOCK.ITEM"));
    delayingBlockMeta.setLore(Translator.getMultiMessage("KITS.DELAYING_BLOCK.DESC"));
    delayingBlock.setItemMeta(delayingBlockMeta);
    return delayingBlock;
  }
}

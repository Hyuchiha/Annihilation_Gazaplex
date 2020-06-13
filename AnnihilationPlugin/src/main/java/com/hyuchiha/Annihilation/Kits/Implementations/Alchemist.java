package com.hyuchiha.Annihilation.Kits.Implementations;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Utils.KitUtils;
import com.hyuchiha.Annihilation.Utils.TimersUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Alchemist extends BaseKit {
  private final HashMap<UUID, Location> personalBrewingLocations;
  private final HashMap<Integer, List<ItemStack>> drops;
  private final List<Integer> probs;
  private final Random random = new Random();

  // https://shotbow.net/forum/wiki/anni-alchemist/
  public Alchemist(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);

    personalBrewingLocations = new HashMap<>();
    drops = new HashMap<>();
    probs = new ArrayList<>();

    fillDrops();
    fillProbs();
  }

  private void fillDrops() {
    // 32% drops
    List<ItemStack> drops32 = new ArrayList<>();
    drops32.add(new ItemStack(Material.FERMENTED_SPIDER_EYE));
    drops32.add(new ItemStack(Material.NETHER_WARTS));
    drops.put(32, drops32);

    // 28% drops
    List<ItemStack> drop28 = new ArrayList<>();
    drop28.add(new ItemStack(Material.SPECKLED_MELON));
    drop28.add(new ItemStack(Material.GOLDEN_CARROT));
    drop28.add(new ItemStack(Material.SUGAR));
    drop28.add(new ItemStack(Material.SPIDER_EYE));
    drop28.add(new ItemStack(Material.MAGMA_CREAM));
    drops.put(28, drop28);

    // 15% drops
    List<ItemStack> drop15 = new ArrayList<>();
    drop15.add(new ItemStack(Material.GLOWSTONE_DUST));
    drop15.add(new ItemStack(Material.GOLDEN_CARROT));
    drop15.add(new ItemStack(Material.GOLDEN_APPLE));
    drops.put(15, drop15);

    // 15% drops (bad drops)
    List<ItemStack> drop15Bad = new ArrayList<>();
    drop15Bad.add(new ItemStack(Material.ROTTEN_FLESH));
    drop15Bad.add(new ItemStack(Material.POISONOUS_POTATO));
    drop15Bad.add(new ItemStack(Material.SNOW_BALL));
    drop15Bad.add(new ItemStack(Material.STRING));
    drops.put(-15, drop15Bad);

    // 7% drops
    List<ItemStack> drop7 = new ArrayList<>();
    drop7.add(new ItemStack(Material.GHAST_TEAR));
    drops.put(7, drop7);

    // 3% drops
    List<ItemStack> drop3 = new ArrayList<>();
    drop3.add(new ItemStack(Material.BLAZE_POWDER));
    drop3.add(new ItemStack(Material.SULPHUR));
    drops.put(3, drop3);

  }

  private void fillProbs() {
    for (int key : drops.keySet()) {
      int percent = Math.abs(key);

      for (int i = 0; i < percent; i++) {
        probs.add(key);
      }
    }
  }

  @Override
  protected void setupSpawnItems() {
    spawnItems.add(new ItemStack(Material.WOOD_SWORD));
    spawnItems.add(new ItemStack(Material.WOOD_PICKAXE));
    spawnItems.add(new ItemStack(Material.WOOD_AXE));

    spawnItems.add(new ItemStack(Material.BREWING_STAND_ITEM));

    ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
    ItemMeta bookMeta = book.getItemMeta();
    bookMeta.setDisplayName(Translator.getColoredString("KITS.ALCHEMIST_BOOK"));
    book.setItemMeta(bookMeta);
    spawnItems.add(book);
  }

  @Override
  protected void giveSpecialPotions(Player recipient) {
    // Nope, this kit dont need that
  }

  @Override
  protected void giveExtraHearts(Player recipient) {
    // Nope
  }

  @Override
  protected void extraConfiguration(Player recipient) {
    // I thing nope
  }

  @Override
  public void removePlayer(Player recipient) {
    removeBrewing(recipient);
  }

  @Override
  public void resetData() {
    this.personalBrewingLocations.clear();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBrewingPlace(BlockPlaceEvent event) {
    Player player = event.getPlayer();
    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

    if (gPlayer.getKit() == Kit.ALCHEMIST && event.getBlock().getType() == Material.BREWING_STAND) {
      addBrewing(player, event.getBlock().getLocation());
    }
  }

  @EventHandler
  public void onBrewingInteract(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    Block b = event.getClickedBlock();
    if (b.getType() != Material.BREWING_STAND) {
      return;
    }

    Location loc = b.getLocation();
    Player player = event.getPlayer();

    if (isBrewingFromAlchemist(loc)) {
      UUID owner = KitUtils.getBlockOwner(loc.getBlock());

      if (owner != null && !owner.equals(player.getUniqueId())) {
        event.setCancelled(true);
        player.sendMessage(Translator.getColoredString("ERROR.DONT_OWN_BREWING"));
      }
    }
  }

  @EventHandler
  public void onBrewingBreak(BlockBreakEvent e) {
    Block b = e.getBlock();
    if (b.getType() != Material.BREWING_STAND) {
      return;
    }

    Location loc = b.getLocation();
    Player player = e.getPlayer();
    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

    if (isBrewingFromAlchemist(loc)) {
      UUID owner = KitUtils.getBlockOwner(b);

      if (owner == null) {
        return;
      }

      if (owner.equals(player.getUniqueId())) {
        // Is your brewing you cant break it
        removeBrewing(player);
      } else {
        // Well is wasn't your brewin
        Player ownerPlayer = Bukkit.getPlayer(owner);

        // Uff you dont exist, shameeeee!!!
        if (ownerPlayer == null) {
          return;
        }

        // Ok, the owner exist
        GamePlayer gPlayerOwner = PlayerManager.getGamePlayer(ownerPlayer);
        // But the breaker is from same team so...
        if (gPlayerOwner.getTeam() != gPlayer.getTeam()) {
          // You loose your pots, shameeee
          removeBrewing(ownerPlayer);
        } else {
          // No it was from your team, no shame
          e.setCancelled(true);
        }
      }
    }
  }

  @EventHandler
  public void onAlchemistBookInteract(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);
    Action action = e.getAction();

    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
      PlayerInventory inventory = player.getInventory();
      ItemStack handItem = inventory.getItemInMainHand();

      if (handItem != null && KitUtils.isKitItem(handItem, "KITS.ALCHEMIST_BOOK")
          && gPlayer.getKit() == Kit.ALCHEMIST) {

        if (TimersUtils.hasExpired(player, Kit.ALCHEMIST)) {
          giveRandomDrop(player);
          TimersUtils.addDelay(player, Kit.ALCHEMIST, 90, TimeUnit.SECONDS);
        } else {
          KitUtils.showKitItemDelay(player, gPlayer.getKit());
        }

      }
    }

  }

  private boolean isBrewingFromAlchemist(Location location) {
    for (Location brewing : this.personalBrewingLocations.values()) {
      if (brewing.equals(location)) {
        return true;
      }
    }
    return false;
  }

  public void addBrewing(Player player, Location location) {
    this.personalBrewingLocations.put(player.getUniqueId(), location);
    KitUtils.setBlockOwner(location.getBlock(), player.getUniqueId());
  }

  public void removeBrewing(Player player) {
    UUID identifier = player.getUniqueId();

    Location location = this.personalBrewingLocations.get(identifier);
    if (location != null) {
      location.getBlock().setType(Material.AIR);
    }

    this.personalBrewingLocations.remove(identifier);
  }

  private void giveRandomDrop(Player player) {
    int position = random.nextInt(99);

    int probability = probs.get(position);

    List<ItemStack> percentDrops = drops.get(probability);
    int dropPosition = random.nextInt(percentDrops.size() - 1);
    player.getInventory().addItem(percentDrops.get(dropPosition));
  }

}

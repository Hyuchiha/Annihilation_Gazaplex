package com.hyuchiha.Annihilation.Kits.Implementations;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Utils.KitUtils;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class Alchemist extends BaseKit {
  private final HashMap<UUID, Location> personalBrewingLocations;

  // https://shotbow.net/forum/wiki/anni-alchemist/
  public Alchemist(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);

    personalBrewingLocations = new HashMap<>();
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
      this.personalBrewingLocations.put(player.getUniqueId(), event.getBlock().getLocation());
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

  private boolean isBrewingFromAlchemist(Location location) {
    for (Location brewing: this.personalBrewingLocations.values()) {
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

}

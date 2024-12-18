package com.hyuchiha.Annihilation.Kits.Implementations;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.hyuchiha.Annihilation.Arena.Nexus;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Object.Loc;
import com.hyuchiha.Annihilation.Object.Teleporter;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import com.hyuchiha.Annihilation.Utils.KitUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class Transporter extends BaseKit {
  private HashMap<UUID, Teleporter> teleports;

  public Transporter(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);

    teleports = new HashMap<>();
  }

  @Override
  protected void setupSpawnItems() {
    spawnItems.add(XMaterial.STONE_SWORD.parseItem());
    spawnItems.add(XMaterial.WOODEN_PICKAXE.parseItem());
    spawnItems.add(XMaterial.WOODEN_AXE.parseItem());

    ItemStack portal = new ItemStack(Material.QUARTZ);
    ItemMeta m = portal.getItemMeta();
    m.setDisplayName(Translator.getColoredString("KITS.TRANSPORTER_ITEM"));
    portal.setItemMeta(m);
    spawnItems.add(portal);
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
    // Not for now
  }

  @Override
  public void removePlayer(Player recipient) {
    removeTeleport(recipient);
  }

  @Override
  public void resetData() {

  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

    if (gPlayer.getKit() == Kit.TRANSPORTER) {
      removeTeleport(player);
    }
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onTeleportItemUsage(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    GamePlayer eventPlayer = PlayerManager.getGamePlayer(player);
    Block block = event.getClickedBlock();

    if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getGameMode() != GameMode.CREATIVE) {

      if (block.getType() == XMaterial.NETHER_QUARTZ_ORE.get()) {
        event.setCancelled(true);
        UUID owner = KitUtils.getBlockOwner(block);
        if (owner != null) {
          Teleporter tele = teleports.get(owner);
          if (tele != null) {
            if (player.getUniqueId().equals(owner)) {
              tele.clear();
              return;
            }
          }
        }
      }

      PlayerInventory inventory = player.getInventory();
      ItemStack handItem = inventory.getItemInMainHand();

      if (eventPlayer.getKit() == Kit.TRANSPORTER && handItem != null && KitUtils.isKitItem(handItem, "KITS.TRANSPORTER_ITEM")) {

        Block clickedBlock = block.getRelative(BlockFace.UP);
        Block other2 = clickedBlock.getRelative(BlockFace.UP);

        if (clickedBlock.getType() == Material.AIR && other2.getType() == Material.AIR && canPlace(block.getType())) {
          for (GameTeam team : GameTeam.teams()) {
            Loc loc = new Loc(block.getLocation());
            Nexus nexus = team.getNexus();
            if (nexus != null) {
              if (loc.isEqual(new Loc(nexus.getLocation()))) {
                player.sendMessage(ChatColor.RED + Translator.getColoredString("ERRORS.PORTAL_NEXUS"));
                event.setCancelled(true);
                return;
              }
            }
          }

          if (GameUtils.tooClose(block.getLocation())) {
            player.sendMessage(ChatColor.RED + Translator.getColoredString("ERRORS.CANNOT_BUILD_HERE"));
            event.setCancelled(true);
            return;
          }

          Teleporter tele = teleports.get(player.getUniqueId());
          if (tele == null) {
            teleports.put(player.getUniqueId(), new Teleporter(player));
            tele = teleports.get(player.getUniqueId());
          }

          if (tele.isLinked()) {
            tele.clear();
            tele.setLoc1(block.getLocation(), block.getState());
          } else if (tele.hasLoc1()) {
            tele.setLoc2(block.getLocation(), block.getState());
          } else {
            tele.setLoc1(block.getLocation(), block.getState());
          }

          Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            block.setType(XMaterial.NETHER_QUARTZ_ORE.get());
            block.getChunk().unload();
            block.getChunk().load();
            KitUtils.setBlockOwner(block, player.getUniqueId());
            XSound.ENTITY_BLAZE_AMBIENT.play(player, 1.0F, 1.9F);
          });

          event.setCancelled(true);
        } else {
          player.sendMessage(ChatColor.RED + Translator.getColoredString("ERRORS.CANNOT_DO_ACTION"));
        }

      }

    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onTeleport(PlayerToggleSneakEvent event) {
    if (event.isSneaking()) {
      Player player = event.getPlayer();
      Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
      if (block.getType() == XMaterial.NETHER_QUARTZ_ORE.get()) {
        UUID owner = KitUtils.getBlockOwner(block);
        if (owner != null) {
          GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

          Teleporter tele = teleports.get(owner);
          if (tele.isLinked() && tele.getOwner().getTeam() == gPlayer.getTeam() && tele.canUse()) {
            Location loc;
            if (new Loc(block.getLocation()).isEqual(tele.getLoc1())) {
              loc = tele.getLoc2().toLocation();
            } else {
              loc = tele.getLoc1().toLocation();
            }
            loc.setY(loc.getY() + 1.0D);
            player.teleport(Loc.getMiddle(loc));
            loc.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 1);
            XSound.ENTITY_ENDERMAN_TELEPORT.play(tele.getLoc1().toLocation(), 1.0F, (float) Math.random());
            XSound.ENTITY_ENDERMAN_TELEPORT.play(tele.getLoc2().toLocation(), 1.0F, (float) Math.random());
            tele.delay();
            event.setCancelled(true);
          }

        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void MoveListeners(PlayerMoveEvent event) {
    ///block under your feet
    Block to = event.getTo().getBlock().getRelative(BlockFace.DOWN);
    if (to.getType() == XMaterial.NETHER_QUARTZ_ORE.get()) {
      Location x = event.getTo();
      Location y = event.getFrom();
      if (x.getBlockX() != y.getBlockX() || x.getBlockY() != y.getBlockY() || x.getBlockZ() != y.getBlockZ()) {
        GamePlayer user = PlayerManager.getGamePlayer(event.getPlayer());
        UUID owner = KitUtils.getBlockOwner(to);
        if (owner != null) {
          Teleporter tele = teleports.get(owner);
          if (tele != null && tele.isLinked() && tele.getOwner().getTeam() == user.getTeam()) {
            String message = Translator.getColoredString("KITS.TELEPORT_HELP").replace("%OWNER%", ChatColor.WHITE + tele.getOwner().getPlayer().getName());
            event.getPlayer().sendMessage(message);
          }
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void TeleporterProtect(BlockBreakEvent event) {
    Player player = event.getPlayer();
    Block block = event.getBlock();
    if (block.getType() == XMaterial.NETHER_QUARTZ_ORE.get()) {
      event.setCancelled(true);
      GamePlayer eventPlayer = PlayerManager.getGamePlayer(player);

      UUID owner = KitUtils.getBlockOwner(block);
      if (owner == null) {
        return;
      }
      Teleporter tele = teleports.get(owner);
      if (player.getName().equalsIgnoreCase(tele.getOwner().getPlayer().getName())) {
        tele.clear();
      } else if (eventPlayer.getTeam() != tele.getOwner().getTeam()) {
        tele.clear();
        String message = Translator.getColoredString("KITS.TELEPORT_DESTROY").replace("%PLAYER%", player.getName());
        tele.getOwner().getPlayer().sendMessage(message);
      }

    }
  }

  private void removeTeleport(Player recipient) {
    Teleporter teleport = teleports.get(recipient.getUniqueId());
    if (teleport != null) {
      teleport.clear();
    }
  }

  private boolean canPlace(Material type) {
    //This tells if a transporter block can be placed at this type of block
    XMaterial parsedType = XMaterial.matchXMaterial(type);
    switch (parsedType) {
      default:
        return true;

      case BEDROCK:
      case OBSIDIAN:
      case CHEST:
      case TRAPPED_CHEST:
      case FURNACE:
      case DISPENSER:
      case DROPPER:
      case CRAFTING_TABLE:
      case HOPPER:
      case BEACON:
      case ANVIL:
      case SPRUCE_WALL_SIGN:
      case ACACIA_WALL_SIGN:
      case BIRCH_WALL_SIGN:
      case DARK_OAK_WALL_SIGN:
      case JUNGLE_WALL_SIGN:
      case OAK_WALL_SIGN:
      case END_PORTAL:
      case NETHER_QUARTZ_ORE:
        return false;
    }
  }
}

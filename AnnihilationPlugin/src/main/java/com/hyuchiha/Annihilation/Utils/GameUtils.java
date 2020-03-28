package com.hyuchiha.Annihilation.Utils;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Dye;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class GameUtils {
  public static void giveEffect(final GamePlayer player) {
    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 80)), 20L);
  }


  public static boolean tooClose(Location loc) {
    double x = loc.getX();
    double y = loc.getY();
    double z = loc.getZ();

    for (GameTeam team : GameTeam.teams()) {
      Location nexusLoc = team.getNexus().getLocation();
      double nX = nexusLoc.getX();
      double nZ = nexusLoc.getZ();
      if (Math.abs(nX - x) <= getBuildDistance() && Math.abs(nZ - z) <= getBuildDistance()) {
        return true;
      }
    }
    return false;
  }

  public static void playSounds(Player p) {
    for (int i = 0; i < 4; i++) {
      p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, i, i);
    }
  }

  public static boolean hasSignAttached(Block block) {
    for (BlockFace attachedOn : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.SELF}) {
      BlockState state = block.getRelative(attachedOn).getState();
      if (state instanceof Sign) {
        BlockFace attachedTo = ((org.bukkit.material.Sign) state.getData()).getAttachedFace();
        switch (attachedOn) {
          case SELF:
            return true;
          case NORTH:
            return (attachedTo == BlockFace.SOUTH);
          case SOUTH:
            return (attachedTo == BlockFace.NORTH);
          case WEST:
            return (attachedTo == BlockFace.EAST);
          case EAST:
            return (attachedTo == BlockFace.WEST);
          case UP:
            return (attachedTo == BlockFace.DOWN);
        }
      }
    }
    return false;
  }

  public static boolean isShopSignAttached(Block block) {
    for (BlockFace attachedOn : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.SELF}) {
      BlockState state = block.getRelative(attachedOn).getState();
      if (state instanceof Sign) {
        BlockFace attachedTo = ((org.bukkit.material.Sign) state.getData()).getAttachedFace();
        switch (attachedOn) {
          case SELF:
            return true;
          case NORTH:
            if (attachedTo == BlockFace.SOUTH) {
              Sign s = (Sign) state;
              String st = s.getLine(0);
              if (st.contains("Shop")) {
                return true;
              }
            }
          case SOUTH:
            if (attachedTo == BlockFace.NORTH) {
              Sign s = (Sign) state;
              String st = s.getLine(0);
              if (st.contains("Shop")) {
                return true;
              }
            }
          case WEST:
            if (attachedTo == BlockFace.EAST) {
              Sign s = (Sign) state;
              String st = s.getLine(0);
              if (st.contains("Shop")) {
                return true;
              }
            }
          case EAST:
            if (attachedTo == BlockFace.WEST) {
              Sign s = (Sign) state;
              String st = s.getLine(0);
              if (st.startsWith("Shop")) {
                return true;
              }
            }
          case UP:
            if (attachedTo == BlockFace.DOWN) {
              Sign s = (Sign) state;
              String st = s.getLine(0);
              if (st.startsWith("Shop"))
                return true;
            }
            break;
        }
      }
    }
    return false;
  }

  public static boolean isFallingToVoid(Player p) {
    Location loc = p.getLocation();
    int a = p.getLocation().getBlockY();
    for (int i = a; i >= 0; i--) {
      loc.add(0.0D, -1.0D, 0.0D);
      if (Bukkit.getWorld(loc.getWorld().getName()).getBlockAt(loc).getType() != Material.AIR) {
        return false;
      }
    }
    return true;
  }

  public static boolean isBlockTeam(GameTeam team) {
    for (Map.Entry<String, Block> bo : GameManager.getCurrentGame().getCrafting().entrySet()) {
      String player = (String) bo.getKey();
      GamePlayer meta = PlayerManager.getGamePlayer(Bukkit.getPlayer(player));
      if (meta.getTeam() == team) {
        return true;
      }
    }
    return false;
  }

  public static boolean isVip(Player player) {
    return (player.hasPermission("annihilation.vip.diamond")
                || player.hasPermission("annihilation.vip.gold")
                || player.hasPermission("annihilation.vip.iron"));
  }

  private static int getBuildDistance() {
    return Main.getInstance().getConfig("config.yml").getInt("build");
  }

  public static ItemStack getDyeColor(DyeColor color) {
    ItemStack stack = new ItemStack(Material.INK_SACK);
    Dye dye = new Dye();
    dye.setColor(color);
    ItemStack tempStack = dye.toItemStack();
    stack.setDurability(tempStack.getDurability());
    stack.setData(dye);
    return stack;
  }

  public static ItemStack getPlayerHead(String playerName) {
    ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
    SkullMeta meta = (SkullMeta) head.getItemMeta();
    meta.setOwner(playerName);
    head.setItemMeta(meta);
    return head;
  }

  public static boolean blockNearBlock(Block a, Block b, double distance) {
    Location aLoc = a.getLocation();
    Location bLoc = b.getLocation();

    double ax = aLoc.getX(), ay = aLoc.getY(), az = aLoc.getZ();
    double bx = bLoc.getX(), by = bLoc.getY(), bz = bLoc.getZ();

    return Math.abs(ax - bx) <= distance
            && Math.abs(ay - by) <= distance
            && Math.abs(az - bz) <= distance;
  }
}

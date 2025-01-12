package com.hyuchiha.Annihilation.Utils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XSound;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Dye;
import org.inventivetalent.reflection.minecraft.Minecraft;

import java.util.Map;

public class GameUtils {
  public static void giveEffect(final GamePlayer player) {
    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> player.getPlayer().addPotionEffect(XPotion.RESISTANCE.buildPotionEffect(100, 80)), 20L);
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

  public static boolean isWallSign(Block block) {
    if (Minecraft.Version.getVersion().olderThan(Minecraft.Version.v1_13_R1)) {
      Material clickedType = block.getType();
      return  clickedType == XMaterial.ACACIA_WALL_SIGN.get()
          || clickedType == XMaterial.SPRUCE_WALL_SIGN.get()
          || clickedType == XMaterial.BIRCH_WALL_SIGN.get()
          || clickedType == XMaterial.DARK_OAK_WALL_SIGN.get()
          || clickedType == XMaterial.JUNGLE_WALL_SIGN.get()
          || clickedType == XMaterial.OAK_WALL_SIGN.get();
    } else {
      return BaseUtils.isANewVersionSign(block);
    }
  }

  public static ItemStack getDyeGlassPane(DyeColor color) {
    String name = color.name().toUpperCase() + "_STAINED_GLASS_PANE";
    return XMaterial.valueOf(name).parseItem();
  }

  public static void playSounds(Player p) {
    for (int i = 0; i < 4; i++) {
      XSound.BLOCK_NOTE_BLOCK_PLING.play(p, i, i);
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
    return (PermissionUtils.hasPermission(player, "annihilation.vip.diamond")
        || PermissionUtils.hasPermission(player, "annihilation.vip.gold")
        || PermissionUtils.hasPermission(player, "annihilation.vip.iron"));
  }

  private static int getBuildDistance() {
    return Main.getInstance().getConfig("config.yml").getInt("build");
  }

  public static ItemStack getDyeColor(DyeColor color) {
    ItemStack stack = XMaterial.INK_SAC.parseItem();
    Dye dye = new Dye();
    dye.setColor(color);
    ItemStack tempStack = dye.toItemStack();
    stack.setDurability(tempStack.getDurability());
    stack.setData(dye);
    return stack;
  }

  public static ItemStack getPlayerHead(String playerName) {
    ItemStack head = XMaterial.PLAYER_HEAD.parseItem();
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

  public static boolean nearLocation(Location a, Location b, double distance) {
    double ax = a.getX(), ay = a.getY(), az = a.getZ();
    double bx = b.getX(), by = b.getY(), bz = b.getZ();

    return Math.abs(ax - bx) <= distance
        && Math.abs(ay - by) <= distance
        && Math.abs(az - bz) <= distance;
  }

}

package com.hyuchiha.Annihilation.Object;

import com.cryptomorin.xseries.XSound;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public class HookTracer implements Runnable {

  private final GamePlayer owner;
  private final Item item;
  private final int maxTicks;

  public HookTracer(Item item, GamePlayer owner, int maxTicks) {
    this.item = item;
    this.owner = owner;
    this.maxTicks = maxTicks;
  }

  @Override
  public void run() {
    //maxTicks--;
    if (maxTicks <= 0 || owner.getKit() != Kit.SCORPIO) {
      item.remove();
      return;
    }

    for (Entity entity : item.getNearbyEntities(1, 1, 1)) {
      if (entity.getType() == EntityType.PLAYER) {
        Player target = (Player) entity;
        GamePlayer p = PlayerManager.getGamePlayer(target);
        if (!p.getPlayerUUID().equals(owner.getPlayerUUID())) {
          Player user = owner.getPlayer();
          if (user != null) {
            if (owner.getTeam() == p.getTeam()) {
              Location loc1 = user.getLocation();
              Location loc2 = target.getLocation();
              if (loc2.getY() >= loc1.getY()) {
                XSound.BLOCK_WOODEN_DOOR_OPEN.play(target.getLocation(), 1F, 0.1F);
                XSound.BLOCK_WOODEN_DOOR_CLOSE.play(user.getLocation(), 1F, 0.1F);
                loc2.setY(loc1.getY());
                Vector vec = loc2.toVector().subtract(loc1.toVector()).setY(.08D).multiply(7);
                user.setVelocity(vec);
              }
            } else {
              XSound.BLOCK_WOODEN_DOOR_OPEN.play(target.getLocation(), 1F, 0.1F);
              XSound.BLOCK_WOODEN_DOOR_CLOSE.play(user.getLocation(), 1F, 0.1F);

              // Stop player damage
              DamageControl.addTempImmunity(target, EntityDamageEvent.DamageCause.FALL, System.currentTimeMillis() + 8000); //8 second fall immunity

              Location loc = user.getLocation();
              Location tele;
              Direction dec = Direction.getDirection(loc.getDirection());
              if (dec == Direction.North)
                tele = loc.getBlock().getRelative(BlockFace.NORTH).getLocation();
              else if (dec == Direction.South)
                tele = loc.getBlock().getRelative(BlockFace.SOUTH).getLocation();
              else if (dec == Direction.East)
                tele = loc.getBlock().getRelative(BlockFace.EAST).getLocation();
              else if (dec == Direction.West)
                tele = loc.getBlock().getRelative(BlockFace.WEST).getLocation();
              else if (dec == Direction.NorthWest)
                tele = loc.getBlock().getRelative(BlockFace.NORTH_WEST).getLocation();
              else if (dec == Direction.NorthEast)
                tele = loc.getBlock().getRelative(BlockFace.NORTH_EAST).getLocation();
              else if (dec == Direction.SouthEast)
                tele = loc.getBlock().getRelative(BlockFace.SOUTH_EAST).getLocation();
              else tele = loc.getBlock().getRelative(BlockFace.SOUTH_WEST).getLocation();
              tele.setPitch(0);
              tele.setYaw(loc.getYaw() + 180);
              target.teleport(tele);
            }
          }
          item.remove();
          return;
        }
      }
    }

    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new HookTracer(item, owner, maxTicks - 1), 1);
  }
}

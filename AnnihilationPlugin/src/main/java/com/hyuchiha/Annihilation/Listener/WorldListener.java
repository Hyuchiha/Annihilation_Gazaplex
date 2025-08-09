package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import com.hyuchiha.Annihilation.Utils.LocationUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;


public class WorldListener implements Listener {
  private static final Set<EntityType> hostileEntityTypes = new HashSet<EntityType>() {
    private static final long serialVersionUID = 42L;

    {
      add(EntityType.SKELETON);
      add(EntityType.CREEPER);
      add(EntityType.SPIDER);
      add(EntityType.BAT);
      add(EntityType.ENDERMAN);
      add(EntityType.SLIME);
      add(EntityType.WITCH);
      add(EntityType.ZOMBIE);
      add(EntityType.WITHER);
    }
  };


  @EventHandler
  public void onPistonMove(BlockPistonExtendEvent e) {
    for (Block b : e.getBlocks()) {
      if (GameUtils.tooClose(b.getLocation())) {
        e.setCancelled(true);
        b.setType(Material.AIR);
      }
    }
  }

  @EventHandler
  public void onWaterFlow(BlockFromToEvent e) {
    if (LocationUtils.isEmptyColumn(e.getToBlock().getLocation())) {
      e.setCancelled(true);
    }
  }


  @EventHandler
  public void LeafDecay(LeavesDecayEvent e) {
    e.setCancelled(true);
  }


  @EventHandler
  public void onBucketUse(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      ItemStack material = player.getInventory().getItemInMainHand();
      if (material.getType() == Material.WATER_BUCKET &&
          GameUtils.tooClose(event.getClickedBlock().getLocation())) {
        event.setCancelled(true);

        return;
      }

      if (material.getType() == Material.LAVA_BUCKET) {
        event.setCancelled(true);
      }
    }
  }


  @EventHandler(priority = EventPriority.MONITOR)
  public void onInventoryClick(InventoryClickEvent e) {
    if (!e.isCancelled()) {
      HumanEntity ent = e.getWhoClicked();

      if (ent instanceof Player) {
        Inventory inv = e.getInventory();

        if (inv instanceof org.bukkit.inventory.AnvilInventory) {
          InventoryView view = e.getView();
          int rawSlot = e.getRawSlot();

          if (rawSlot == view.convertSlot(rawSlot)) {


            if (rawSlot == 2) {


              ItemStack item = e.getCurrentItem();


              if (item != null) {
                ItemMeta meta = item.getItemMeta();


                if (meta != null) {
                  if (meta.hasDisplayName() &&
                      item.getType() == Material.GOLD_INGOT) {
                    e.setCancelled(true);
                  }
                }
              }
            }
          }
        }
      }
    }
  }


  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onSpawn(CreatureSpawnEvent e) {
    //Output.log("Spawn Entity Event: " + e.getEntityType().toString());
    if (isHostile(e.getEntityType())) {
      //Output.log("Spawn Reason: " + e.getSpawnReason().toString());
      //Output.log("Result: " + (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM));
      if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
        // Output.log("Spawned Mob");
        return;
      }
      e.setCancelled(true);
    }
  }


  private boolean isHostile(EntityType entityType) {
    return hostileEntityTypes.contains(entityType);
  }
}

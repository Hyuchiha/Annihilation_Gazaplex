package com.hyuchiha.Annihilation.Kits.Implementations;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Utils.KitUtils;
import com.hyuchiha.Annihilation.Utils.TimersUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Bloodmage extends BaseKit {
  private final HashMap<UUID, List<Integer>> delaysHearts = new HashMap<>();
  private final Random random = new Random();

  // https://shotbow.net/forum/wiki/anni-bloodmage/
  public Bloodmage(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);


    Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {

      for (List<Integer> delays: delaysHearts.values()) {

        // Check if the PID is running, if not then we need to remove the PID
        List<Integer> expired = new ArrayList<>();
        for (int delayPID : delays) {
          if (!Bukkit.getScheduler().isCurrentlyRunning(delayPID)) {
            expired.add(delayPID);
          }
        }

        // Now we remove the PID from the array
        delays.removeAll(expired);
      }

    }, 20, 20);

  }

  @Override
  protected void setupSpawnItems() {
    spawnItems.add(new ItemStack(Material.STONE_SWORD));
    spawnItems.add(new ItemStack(Material.WOOD_PICKAXE));
    spawnItems.add(new ItemStack(Material.WOOD_AXE));

    ItemStack abilityItem = new ItemStack(Material.FERMENTED_SPIDER_EYE, 1);
    ItemMeta meta = abilityItem.getItemMeta();
    meta.setDisplayName(Translator.getColoredString("KITS.BLOODMAGE_ITEM"));
    abilityItem.setItemMeta(meta);
    spawnItems.add(abilityItem);
  }

  @Override
  protected void giveSpecialPotions(Player recipient) {
    // This kit dont need pots
  }

  @Override
  protected void giveExtraHearts(Player recipient) {
    // Meh, this is not needed
  }

  @Override
  protected void extraConfiguration(Player recipient) {
    // Noup
  }

  @Override
  public void removePlayer(Player recipient) {
    // mmmmmmm.... posible but for now is not needed :)
  }

  @Override
  public void resetData() {
    // There are not items that require reset data
    this.delaysHearts.clear();
  }

  @EventHandler()
  public void onBloodMageDamage(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
      Player entity = (Player) event.getEntity();
      Player damager = (Player) event.getDamager();

      GamePlayer gpDamager = PlayerManager.getGamePlayer(damager);

      if (gpDamager.getKit() == Kit.BLOODMAGE && random.nextInt(3) == 0) {
        entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 1));
      }
    }
  }

  @EventHandler()
  public void onBloodMageItemInteract(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);
    Action action = e.getAction();

    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
      PlayerInventory inventory = player.getInventory();
      ItemStack handItem = inventory.getItemInMainHand();


      if (handItem != null && KitUtils.isKitItem(handItem, "KITS.BLOODMAGE_ITEM")
              && gPlayer.getKit() == Kit.BLOODMAGE) {

        if (TimersUtils.hasExpired(player, Kit.BLOODMAGE)) {
          Player target = KitUtils.getTarget(player, 6, true);

          if (target != null) {
            UUID targetId = target.getUniqueId();

            target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 1));

            // This validators are for stack ability hearts remove, only 2 times
            if (delaysHearts.containsKey(targetId)) {
              List<Integer> delays = delaysHearts.get(targetId);

              // The user only has reduced this hearts 2 times
              if (delays.size() <= 2) {
                target.setMaxHealth(target.getHealth() - 2.0D);

                int PID = scheduleHeartRestorer(target);
                delays.add(PID);
              }
            } else {
              // First time with the effect, we create the list and save it
              List<Integer> delays = new ArrayList<>();
              target.setMaxHealth(target.getHealth() - 2.0D);

              int PID = scheduleHeartRestorer(target);
              delays.add(PID);

              delaysHearts.put(targetId, delays);
            }

            TimersUtils.addDelay(player, Kit.BLOODMAGE, 60, TimeUnit.SECONDS);
          }

        } else {
          KitUtils.showKitItemDelay(player, gPlayer.getKit());
        }

      }

    }

  }

  @EventHandler()
  public void onPlayerDeath(PlayerDeathEvent e) {
    Player player = e.getEntity();
    UUID playerId = player.getUniqueId();

    if (delaysHearts.containsKey(playerId)) {
      List<Integer> playerDelays = delaysHearts.get(playerId);
      playerDelays.clear();
    }

  }

  private int scheduleHeartRestorer(Player target) {
    return Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
      double maxHealth = target.getMaxHealth();
      double health = target.getHealth();

      target.setMaxHealth(maxHealth + 2.0D);
      target.setHealth(health + 2.0D);
    }, 5 * 20);
  }

}

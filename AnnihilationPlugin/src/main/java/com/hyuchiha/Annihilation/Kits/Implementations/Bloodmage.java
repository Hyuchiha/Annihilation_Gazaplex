package com.hyuchiha.Annihilation.Kits.Implementations;

import com.cryptomorin.xseries.XMaterial;
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
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
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

      for (List<Integer> delays : delaysHearts.values()) {

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
    spawnItems.add(XMaterial.STONE_SWORD.parseItem());
    spawnItems.add(XMaterial.WOODEN_PICKAXE.parseItem());
    spawnItems.add(XMaterial.WOODEN_AXE.parseItem());

    ItemStack abilityItem = new ItemStack(Material.FERMENTED_SPIDER_EYE, 1);
    ItemMeta meta = abilityItem.getItemMeta();
    meta.setDisplayName(Translator.getColoredString("KITS.BLOODMAGE.ITEM"));
    meta.setLore(Translator.getMultiMessage("KITS.BLOODMAGE.DESC"));
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
    if (e.getHand() != EquipmentSlot.HAND) {
      return;
    }

    Player player = e.getPlayer();
    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

    if ((e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) ||
            gPlayer.getKit() != Kit.BLOODMAGE) {
      return;
    }

      ItemStack handItem = player.getInventory().getItemInMainHand();
    if (handItem == null || KitUtils.isKitItem(handItem, "KITS.BLOODMAGE.ITEM")) {
      return;
    }

    if (!TimersUtils.hasExpired(player, Kit.BLOODMAGE)) {
      KitUtils.showKitItemDelay(player, gPlayer.getKit());
    }

    Player target = KitUtils.getTarget(player, 6, true);
    if (target == null) {
      return;
    }

    target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 1));

    UUID targetId = target.getUniqueId();
    delaysHearts.putIfAbsent(targetId, new ArrayList<>());
    List<Integer> delays = delaysHearts.get(targetId);

    if (delays.size() < 2) {
      AttributeInstance attribute = target.getAttribute(Attribute.MAX_HEALTH);
      double newHealth = Math.max(2.0, target.getHealth() - 2.0);

      attribute.setBaseValue(attribute.getBaseValue() - 2.0);
      target.setHealth(newHealth);

      int PID = scheduleHeartRestorer(target);
      delays.add(PID);
    }

    TimersUtils.addDelay(player, Kit.BLOODMAGE, 60, TimeUnit.SECONDS);
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
      AttributeInstance attribute = target.getAttribute(Attribute.MAX_HEALTH);

      double maxHealth = attribute.getValue();
      double health = target.getHealth();

      attribute.setBaseValue(maxHealth + 2.0D);
      target.setHealth(health + 2.0D);
    }, 5 * 20);
  }

}

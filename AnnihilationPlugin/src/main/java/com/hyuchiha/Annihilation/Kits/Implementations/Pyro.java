package com.hyuchiha.Annihilation.Kits.Implementations;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Utils.KitUtils;
import com.hyuchiha.Annihilation.Utils.PotionUtils;
import com.hyuchiha.Annihilation.Utils.TimersUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Pyro extends BaseKit {
  private Random random = new Random();

  // https://wiki.shotbow.net/Pyro
  public Pyro(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);
  }

  @Override
  protected void setupSpawnItems() {
    spawnItems.add(XMaterial.STONE_SWORD.parseItem());
    spawnItems.add(XMaterial.WOODEN_PICKAXE.parseItem());
    spawnItems.add(XMaterial.WOODEN_AXE.parseItem());

    ItemStack potion = PotionUtils.getBasePotionType(XPotion.INSTANT_HEALTH.getPotionType(), 1);
    spawnItems.add(potion);

    ItemStack flameItem = XMaterial.FIRE_CHARGE.parseItem();
    assert flameItem != null;
    ItemMeta itemMeta = flameItem.getItemMeta();
    itemMeta.setDisplayName(Translator.getColoredString("KITS.PYRO.ITEM"));
    itemMeta.setLore(Translator.getMultiMessage("KITS.PYRO.DESC"));
    flameItem.setItemMeta(itemMeta);
    spawnItems.add(flameItem);
  }

  @Override
  protected void giveSpecialPotions(Player recipient) {
    // Nope
  }

  @Override
  protected void giveExtraHearts(Player recipient) {
    // Nope
  }

  @Override
  protected void extraConfiguration(Player recipient) {
    // Nope
  }

  @Override
  public void removePlayer(Player recipient) {
    // Nope
  }

  @Override
  public void resetData() {
    // Nope
  }

  @EventHandler()
  public void onFireDamage(EntityDamageEvent e) {
    if (!(e.getEntity() instanceof Player)) {
      return;
    }

    Player player = (Player) e.getEntity();
    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

    if (gPlayer.getKit() == Kit.PYRO &&
        (
            e.getCause() == EntityDamageEvent.DamageCause.FIRE ||
                e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK ||
                e.getCause() == EntityDamageEvent.DamageCause.LAVA
        )
    ) {
      e.setCancelled(true);
    }
  }

  @EventHandler()
  public void onPyroItemInteract(PlayerInteractEvent e) {
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

      if (handItem != null && KitUtils.isKitItem(handItem, "KITS.PYRO.ITEM")
          && gPlayer.getKit() == Kit.PYRO) {

        if (TimersUtils.hasExpired(player, Kit.PYRO)) {
          List<Entity> entities = player.getNearbyEntities(10, 10, 10);

          if (entities != null) {
            for (Entity attack : entities) {
              if (attack.getType() == EntityType.PLAYER) {
                Player attacked = (Player) attack;
                GamePlayer meta = PlayerManager.getGamePlayer(attacked);
                if (meta.getTeam() != gPlayer.getTeam()) {
                  attack.setFireTicks(30);
                }
              }
            }
          }

          TimersUtils.addDelay(player, Kit.PYRO, 40, TimeUnit.SECONDS);
        } else {
          KitUtils.showKitItemDelay(player, gPlayer.getKit());
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPyroDamage(EntityDamageByEntityEvent event) {
    Entity entityAttacker = event.getDamager();
    Entity entityAttacked = event.getEntity();

    if ((entityAttacker.getType() == EntityType.PLAYER) && (entityAttacked.getType() == EntityType.PLAYER)) {
      Player damager = (Player) entityAttacker;
      Player attacked = (Player) entityAttacked;

      GamePlayer gpDamager = PlayerManager.getGamePlayer(damager);

      if (gpDamager.getKit() == Kit.PYRO && random.nextInt(100) <= 40) {
        attacked.setFireTicks(50);
      }
    }
  }

  @EventHandler
  public void onArrowDamageByPyro(EntityShootBowEvent e) {
    Player player = (Player) e.getEntity();
    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);
    if (gPlayer.getKit() == Kit.PYRO) {
      e.getProjectile().setFireTicks(60);
    }
  }
}

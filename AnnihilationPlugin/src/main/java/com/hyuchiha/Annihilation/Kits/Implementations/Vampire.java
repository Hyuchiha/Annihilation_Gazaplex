package com.hyuchiha.Annihilation.Kits.Implementations;

import com.cryptomorin.xseries.XMaterial;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Random;

public class Vampire extends BaseKit {
  public Random random = new Random();

  public Vampire(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);

    Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {

      if (GameManager.hasCurrentGame() && GameManager.getCurrentGame().isInGame()) {

        for (Player player : Bukkit.getOnlinePlayers()) {
          GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

          if (gPlayer.getKit() == Kit.VAMPIRE) {
            applyPots(player);
          }
        }
      }

    }, 20, 20);
  }

  @Override
  protected void setupSpawnItems() {
    spawnItems.add(XMaterial.WOODEN_AXE.parseItem());
    spawnItems.add(XMaterial.WOODEN_PICKAXE.parseItem());
    spawnItems.add(XMaterial.STONE_SWORD.parseItem());

    ItemStack potion = new ItemStack(Material.POTION, 1);
    PotionMeta meta = (PotionMeta) potion.getItemMeta();
    meta.setBasePotionData(new PotionData(PotionType.NIGHT_VISION, false, false));
    potion.setItemMeta(meta);
    spawnItems.add(potion);
  }

  @Override
  protected void giveSpecialPotions(Player recipient) {
    applyPots(recipient);
  }

  @Override
  protected void giveExtraHearts(Player recipient) {
    // Not needed for this kits
  }

  @Override
  protected void extraConfiguration(Player recipient) {
    // Nope
  }

  @Override
  public void removePlayer(Player recipient) {
    // I think no
  }

  @Override
  public void resetData() {
    // Not for now
  }

  @EventHandler()
  public void onVampireDamage(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
      Player entity = (Player) event.getEntity();
      Player damager = (Player) event.getDamager();

      GamePlayer gpDamager = PlayerManager.getGamePlayer(damager);

      if (gpDamager.getKit() == Kit.VAMPIRE && isNight(damager) && random.nextInt(5) <= 2) {
        double health = damager.getHealth();
        damager.setHealth(health + (event.getDamage() / 2));
      }

    }
  }

  private void applyPots(Player player) {
    if (isNight(player)) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 3, 1));
    } else {
      player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 3, 1));
    }
  }

  private boolean isNight(Player p) {
    long time = p.getWorld().getTime();

    return time >= 12500;
  }

}

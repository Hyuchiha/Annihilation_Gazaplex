package com.hyuchiha.Annihilation.Kits.Implementations;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Manager.ResourceManager;
import com.hyuchiha.Annihilation.Utils.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Enchanter extends BaseKit {
  private Random random;

  // https://wiki.shotbow.net/Enchanter
  public Enchanter(String name, ItemStack icon, ConfigurationSection section) {
    super(name, icon, section);

    this.random = new Random();
  }

  @Override
  protected void setupSpawnItems() {
    spawnItems.add(XMaterial.GOLDEN_SWORD.parseItem());
    spawnItems.add(XMaterial.WOODEN_PICKAXE.parseItem());
    spawnItems.add(XMaterial.WOODEN_AXE.parseItem());

    Material expBottleType = XMaterial.EXPERIENCE_BOTTLE.parseMaterial();
    assert expBottleType != null;
    spawnItems.add(new ItemStack(expBottleType, 5));
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
    // not neededd
  }

  @Override
  public void removePlayer(Player recipient) {
    // noup
  }

  @Override
  public void resetData() {
    // noup
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
  public void onBreakResource(BlockBreakEvent e) {
    Player player = e.getPlayer();
    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

    Material type = e.getBlock().getType();

    if (GameManager.hasCurrentGame()
        && GameManager.getCurrentGame().isInGame()
        && ResourceManager.containsResource(type)
        && gPlayer.getKit() == Kit.ENCHANTER
    ) {
      if (random.nextInt(100) < 25) {
        Material expType = XMaterial.EXPERIENCE_BOTTLE.parseMaterial();
        player.getInventory().addItem(new ItemStack(expType, 1));
      }

    }
  }

  @Override
  public int getXpMultiplier() {
    return 2;
  }
}

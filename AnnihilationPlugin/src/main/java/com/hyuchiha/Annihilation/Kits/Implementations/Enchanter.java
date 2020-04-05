package com.hyuchiha.Annihilation.Kits.Implementations;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Game.Resource;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Manager.ResourceManager;
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
        spawnItems.add(new ItemStack(Material.GOLD_SWORD));
        spawnItems.add(new ItemStack(Material.WOOD_PICKAXE));
        spawnItems.add(new ItemStack(Material.WOOD_AXE));
        spawnItems.add(new ItemStack(Material.EXP_BOTTLE, 5));
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
                player.getInventory().addItem(new ItemStack(Material.EXP_BOTTLE, 1));
            }

        }
    }

    @Override
    public int getXpMultiplier() {
        return 2;
    }
}

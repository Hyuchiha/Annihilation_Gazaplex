package com.hyuchiha.Annihilation.Kits.Implementations;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Utils.TimersUtils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

public class Scout extends BaseKit {
    public Scout(String name, ItemStack icon, ConfigurationSection section) {
        super(name, icon, section);
    }

    @Override
    protected void setupSpawnItems() {
        spawnItems.add(XMaterial.GOLDEN_SWORD.parseItem());
        spawnItems.add(XMaterial.WOODEN_PICKAXE.parseItem());
        spawnItems.add(XMaterial.WOODEN_AXE.parseItem());

        ItemStack grapple = new ItemStack(Material.FISHING_ROD);
        ItemMeta meta = grapple.getItemMeta();
        meta.setDisplayName(Translator.getColoredString("KITS.SCOUT.ITEM"));
        meta.setLore(Translator.getMultiMessage("KITS.SCOUT.DESC"));
        grapple.setItemMeta(meta);
        spawnItems.add(grapple);
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
        // Not needed
    }

    @Override
    public void removePlayer(Player recipient) {
        // Not needed
    }

    @Override
    public void resetData() {
        // Not needed
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGrapple(PlayerFishEvent event) {
        Player player = event.getPlayer();
        GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

        if (gPlayer.getKit() == Kit.SCOUT) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            ItemMeta meta = itemInHand.getItemMeta();
            if (meta instanceof Damageable) {
                Damageable damageable = (Damageable) meta;

                int currentDamage = damageable.getDamage(); // Get the current damage
                if (currentDamage > 0) {
                    damageable.setDamage(currentDamage - 10); // Reduce damage by 1 point
                    itemInHand.setItemMeta(meta); // Apply the changes to the item
                    player.updateInventory(); // Update the inventory to reflect changes
                }
            }

            if (TimersUtils.hasExpired(player, Kit.SCOUT)) {
                TimersUtils.addDelay(player, Kit.SCOUT, 1, TimeUnit.SECONDS);
                Location hookLoc = event.getHook().getLocation();
                Location playerLoc = player.getLocation();

                double hookX = (int) hookLoc.getX();
                double hookY = (int) hookLoc.getY();
                double hookZ = (int) hookLoc.getZ();

                World hookW = Bukkit.getWorld(hookLoc.getWorld().getName());
                Location hookLoc2 = new Location(hookW, hookX, hookY, hookZ);
                hookLoc2.add(0, -1, 0);
                Material inType = hookLoc.getWorld().getBlockAt(hookLoc2).getType();
                if (inType == Material.AIR || inType == Material.WATER || inType == XMaterial.WATER.get()) {
                    return;
                }
                Location l1 = playerLoc.clone();
                l1.add(0.0, 0.5, 0.0);
                player.teleport(l1);

                Vector diff = hookLoc.toVector().subtract(playerLoc.toVector());
                Vector vel = new Vector();
                double d = hookLoc.distance(playerLoc);
                vel.setX((1.0 + 0.07 * d) * diff.getX() / d);
                vel.setY((1.0 + 0.03 * d) * diff.getY() / d + 0.04 * d);
                vel.setZ((1.0 + 0.07 * d) * diff.getZ() / d);

                player.setVelocity(vel);
                XSound.ENTITY_PLAYER_ATTACK_SWEEP.play(player, 1.0F, 1.0F);
            }
        }
    }
}

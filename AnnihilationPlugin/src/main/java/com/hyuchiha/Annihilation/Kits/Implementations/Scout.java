package com.hyuchiha.Annihilation.Kits.Implementations;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

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
        meta.setDisplayName(ChatColor.GREEN + "Grapple");
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

        if (event.getState() == PlayerFishEvent.State.IN_GROUND &&
                gPlayer.getKit() == Kit.SCOUT) {

            Location hookLoc = event.getHook().getLocation();
            Location playerLoc = player.getLocation();

            double hookX = (int) hookLoc.getX();
            double hookY = (int) hookLoc.getY();
            double hookZ = (int) hookLoc.getZ();

            Material inType = hookLoc.getWorld().getBlockAt(hookLoc).getType();
            if (inType == Material.AIR || inType == Material.WATER || inType == Material.LAVA) {
                Material belowType = hookLoc.getWorld().getBlockAt((int) hookX, (int) (hookY - 0.1), (int) hookZ).getType();
                if (belowType == Material.AIR || belowType == Material.WATER || belowType == Material.LAVA) {
                    return;
                }
            }

            playerLoc.setY(playerLoc.getY() + 0.5);
            player.teleport(playerLoc);

            Vector diff = hookLoc.toVector().subtract(playerLoc.toVector());
            Vector vel = new Vector();
            double d = hookLoc.distance(playerLoc);
            vel.setX((1.0 + 0.07 * d) * diff.getX() / d);
            vel.setY((1.0 + 0.03 * d) * diff.getY() / d + 0.04 * d);
            vel.setZ((1.0 + 0.07 * d) * diff.getZ() / d);
            player.setVelocity(vel);

            // Play the initial left-click sound when throwing the grappling hook
            XSound.ENTITY_PLAYER_ATTACK_SWEEP.play(player, 1.0F, 1.0F);
        } else if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY &&
                gPlayer.getKit() == Kit.SCOUT) {

            // Play the sound when the hook is grabbed
            XSound.ENTITY_ENDER_PEARL_THROW.play(player, 1.0F, 1.0F);
        }
    }
}

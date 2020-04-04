package com.hyuchiha.Annihilation.Kits.Implementations;

import com.hyuchiha.Annihilation.Arena.Nexus;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Listener.SoulboundListener;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import com.hyuchiha.Annihilation.Utils.KitUtils;
import com.hyuchiha.Annihilation.Utils.TimersUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Defender extends BaseKit {
    // https://wiki.shotbow.net/Defender
    public Defender(String name, ItemStack icon, ConfigurationSection section) {
        super(name, icon, section);


        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {

            if (GameManager.hasCurrentGame() && GameManager.getCurrentGame().isInGame()) {

                for (Player player: Bukkit.getOnlinePlayers()) {
                    GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

                    if (gPlayer.getKit() == Kit.DEFENDER) {
                        applyHearts(player);
                    }
                }

            }

        }, 20, 20);
    }

    @Override
    protected void setupSpawnItems() {
        spawnItems.add(new ItemStack(Material.WOOD_SWORD));
        spawnItems.add(new ItemStack(Material.WOOD_PICKAXE));
        spawnItems.add(new ItemStack(Material.WOOD_AXE));
        spawnItems.add(new ItemStack(Material.WOOD_SPADE));

        ItemStack dye = GameUtils.getDyeColor(DyeColor.LIME);
        ItemMeta dyeMeta = dye.getItemMeta();
        dyeMeta.setDisplayName(Translator.getColoredString("KITS.DEFENDER_ITEM"));
        dye.setItemMeta(dyeMeta);
        spawnItems.add(dye);
    }

    @Override
    protected void giveSpecialPotions(Player recipient) {
        // Not needed
    }

    @Override
    protected void giveExtraHearts(Player recipient) {
        // Here comes the defender
        applyHearts(recipient);
    }

    @Override
    protected void extraConfiguration(Player recipient) {
        ItemStack chest = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        SoulboundListener.soulbind(chest);

        PlayerInventory inventory = recipient.getInventory();
        inventory.setChestplate(chest);
    }

    @Override
    public void removePlayer(Player recipient) {
        // Nothing to do here
    }

    @Override
    public void resetData() {

    }

    @EventHandler()
    public void onDefenderWaspInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        GamePlayer gPlayer = PlayerManager.getGamePlayer(player);
        Action action = e.getAction();

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            PlayerInventory inventory = player.getInventory();
            ItemStack handItem = inventory.getItemInMainHand();

            if (handItem != null && KitUtils.isKitItem(handItem, "KITS.DEFENDER_ITEM")
                    && gPlayer.getKit() == Kit.DEFENDER) {

                if (TimersUtils.hasExpired(player, Kit.DEFENDER)) {
                    Nexus nexus = gPlayer.getTeam().getNexus();
                    Location nexusLocation = nexus.getLocation().clone();
                    nexusLocation.add(1,0, 1);
                    player.teleport(nexusLocation);

                    TimersUtils.addDelay(player, Kit.DEFENDER, 20, TimeUnit.SECONDS);
                } else {
                    KitUtils.showKitItemDelay(player, gPlayer.getKit());
                }
            }
        }
    }

    private void applyHearts(Player player) {
        GamePlayer gPlayer = PlayerManager.getGamePlayer(player);
        Nexus nexus = gPlayer.getTeam().getNexus();

        if (GameUtils.nearLocation(nexus.getLocation(), player.getLocation(), 50)) {
            int health = nexus.getHealth();
            int position = health / 10;

            double playerMaxHealth = player.getMaxHealth();
            double playerHealth = player.getHealth();

            switch (position) {
                case 7:
                    player.setMaxHealth(playerMaxHealth + 1);
                    player.setHealth(playerHealth + 1);
                    break;
                case 6:
                    player.setMaxHealth(playerMaxHealth + 2);
                    player.setHealth(playerHealth + 2);
                    break;
                case 5:
                    player.setMaxHealth(playerMaxHealth + 3);
                    player.setHealth(playerHealth + 3);
                    break;
                case 4:
                    player.setMaxHealth(playerMaxHealth + 4);
                    player.setHealth(playerHealth + 4);
                    break;
                case 3:
                    player.setMaxHealth(playerMaxHealth + 5);
                    player.setHealth(playerHealth + 5);
                    break;
                case 2:
                    player.setMaxHealth(playerMaxHealth + 6);
                    player.setHealth(playerHealth + 6);
                    break;
                case 1:
                    player.setMaxHealth(playerMaxHealth + 7);
                    player.setHealth(playerHealth + 7);
                    break;
                case 0:
                    player.setMaxHealth(playerMaxHealth + 8);
                    player.setHealth(playerHealth + 8);
                    break;

            }


            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 3));
        }

    }

}

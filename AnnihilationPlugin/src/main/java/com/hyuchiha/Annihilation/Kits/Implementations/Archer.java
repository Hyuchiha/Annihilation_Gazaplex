package com.hyuchiha.Annihilation.Kits.Implementations;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Listener.SoulboundListener;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Utils.KitUtils;
import com.hyuchiha.Annihilation.Utils.TimersUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;

public class Archer extends BaseKit {
    public Archer(String name, ItemStack icon, ConfigurationSection section) {
        super(name, icon, section);

        ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(Material.ARROW,3)).addIngredient(Material.FLINT).addIngredient(Material.STICK);
        Bukkit.addRecipe(recipe);
    }

    // TODO add item for clain arrows after 45 sec
    // https://shotbow.net/forum/wiki/anni-archer/

    @Override
    protected void setupSpawnItems() {
        spawnItems.add(new ItemStack(Material.WOOD_SWORD));
        spawnItems.add(new ItemStack(Material.WOOD_PICKAXE));
        spawnItems.add(new ItemStack(Material.WOOD_AXE));
        spawnItems.add(new ItemStack(Material.WOOD_SPADE));

        ItemStack bow = new ItemStack(Material.BOW);
        bow.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
        spawnItems.add(bow);

        spawnItems.add(new ItemStack(Material.ARROW, 16));

        ItemStack potion = new ItemStack(Material.POTION, 1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL, false, false));
        potion.setItemMeta(meta);
        spawnItems.add(potion);

        ItemStack book = new ItemStack(Material.BOOK, 1);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.setDisplayName(Translator.getColoredString("KITS.ARCHER_BOOK"));
        book.setItemMeta(bookMeta);
        spawnItems.add(book);
    }

    @Override
    protected void giveSpecialPotions(Player recipient) {
        // Noup this kits doesn't have special potions
    }

    @Override
    protected void giveExtraHearts(Player recipient) {
        // Noup
    }

    @Override
    public void removePlayer(Player recipient) {
        // I dont thing this is needed, come back later
    }

    private void getAdditionalArrows(Player player) {
        PlayerInventory inventory = player.getInventory();

        ItemStack arrows = new ItemStack(Material.ARROW, 32);
        SoulboundListener.soulbind(arrows);

        inventory.addItem(arrows);
    }

    //Stops non-archers from crafting arrows using the archer recipe
    @EventHandler(priority = EventPriority.HIGHEST)
    public void arrowCraftingStopper(CraftItemEvent event)
    {
        if(event.getRecipe().getResult().getType() == Material.ARROW && event.getRecipe().getResult().getAmount() == 3) {
            Player player = (Player) event.getWhoClicked();
            GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

            if (gPlayer.getKit() != Kit.ARCHER) {
                event.setCancelled(true);
            }
        }
    }

    //Adds the +1 arrow damage
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void damageListener(final EntityDamageByEntityEvent event)
    {
        if(event.getDamager().getType() == EntityType.ARROW)
        {
            ProjectileSource s = ((Projectile)event.getDamager()).getShooter();
            if(s instanceof Player)
            {
                GamePlayer shooter = PlayerManager.getGamePlayer((Player) s);
                if (shooter.getKit() == Kit.ARCHER) {
                    event.setDamage(event.getDamage() + 1);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onArcherBookInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        GamePlayer gPlayer = PlayerManager.getGamePlayer(player);
        Action action = e.getAction();

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            PlayerInventory inventory = player.getInventory();
            ItemStack handItem = inventory.getItemInMainHand();

            if (handItem != null && KitUtils.isKitItem(handItem, "KITS.ARCHER_BOOK")) {

                if (TimersUtils.hasExpired(player.getUniqueId().toString(), gPlayer.getKit())) {
                    getAdditionalArrows(player);
                } else {
                    KitUtils.showKitItemDelay(player, gPlayer.getKit());
                }
            }
        }
    }
}

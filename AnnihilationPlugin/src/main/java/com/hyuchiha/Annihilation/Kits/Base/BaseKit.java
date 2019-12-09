package com.hyuchiha.Annihilation.Kits.Base;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Listener.SoulboundListener;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseKit implements Listener{

    private String name;
    private final ItemStack icon;
    protected ItemStack[] armorItems;
    protected final List<ItemStack> spawnItems = new ArrayList<>();

    public BaseKit(String name, ItemStack icon, ConfigurationSection section){
        this.name = name;
        this.icon = icon;

        setupLore(section);
        setupArmorItems();
        setupSpawnItems();

        registerListener();
    }

    private void registerListener() {
        Plugin plugin = Main.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void setupLore(ConfigurationSection section) {
        List<String> lore = new ArrayList<>();

        for(String line: section.getStringList("lore")){
            line = line.replaceAll("(&([a-fk-or0-9]))", "§$2");
            lore.add(line);
        }

        ItemMeta meta = icon.getItemMeta();
        meta.setLore(lore);
        icon.setItemMeta(meta);
    }

    protected void setupArmorItems() {
        ItemStack[] spawnArmor = new ItemStack[]{
            new ItemStack(Material.LEATHER_BOOTS),
            new ItemStack(Material.LEATHER_LEGGINGS),
            new ItemStack(Material.LEATHER_CHESTPLATE),
            new ItemStack(Material.LEATHER_HELMET)
        };

        for(ItemStack stack: spawnArmor){
            SoulboundListener.soulbind(stack);
        }

        this.armorItems = spawnArmor;
    }

    protected abstract void setupSpawnItems();

    public void giveKitItems(Player recipient){
        GamePlayer gPlayer = PlayerManager.getGamePlayer(recipient);
        PlayerInventory inv = recipient.getInventory();
        inv.clear();

        for (ItemStack item : spawnItems) {
            ItemStack toGive = item.clone();
            SoulboundListener.soulbind(toGive);
            inv.addItem(toGive);
        }

        GameTeam team = gPlayer.getTeam();

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(team.color() + Translator.getColoredString("COMPASS_FOCUS").replace("%TEAM%", team.toString()));
        compass.setItemMeta(compassMeta);
        SoulboundListener.soulbind(compass);

        inv.addItem(compass);
        recipient.setCompassTarget(team.getNexus().getLocation());

        ItemStack[] armor = armorItems.clone();
        colorizeArmor(team.getColor(), armor);
        inv.setArmorContents(armor);

        giveSpecialPotions(recipient);
        giveExtraHearts(recipient);
    }

    private void colorizeArmor(Color color, ItemStack[] armor) {
        for (ItemStack item : armor) {
            if (item.getItemMeta() instanceof LeatherArmorMeta) {
                LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                meta.setColor(color);
                item.setItemMeta(meta);
            }
        }
    }

    protected abstract void giveSpecialPotions(Player recipient);
    protected abstract void giveExtraHearts(Player recipient);
    public abstract void removePlayer(Player recipient);

    public ItemStack getIcon(){
        return icon;
    }

    public String getName() {
        return name.substring(0, 1) + name.substring(1).toLowerCase();
    }
}

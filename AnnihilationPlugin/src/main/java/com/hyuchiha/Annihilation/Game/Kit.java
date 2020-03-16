package com.hyuchiha.Annihilation.Game;

import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Kits.Implementations.*;
import com.hyuchiha.Annihilation.Main;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public enum Kit {
  CIVILIAN("CIVILIAN", Material.WORKBENCH),
  ACROBAT("ACROBAT", Material.FEATHER),
  ALCHEMIST("ALCHEMIST", Material.BREWING_STAND_ITEM),
  ARCHER("ARCHER", Material.BOW),
  BERSERKER("BERSERKER", Material.CHAINMAIL_CHESTPLATE),
  BLOODMAGE("BLOODMAGE", Material.RECORD_3),
  BUILDER("BUILDER", Material.CLAY_BRICK);

  private HashMap<String, BaseKit> kits = new HashMap<>();

  Kit(String name, Material m) {

    ItemStack icon = new ItemStack(m);
    ItemMeta meta = icon.getItemMeta();
    meta.setDisplayName(name.substring(0, 1) + name.substring(1).toLowerCase());
    icon.setItemMeta(meta);

    Configuration configuration = Main.getInstance().getConfig("kits.yml");

    loadKit(name, icon, configuration.getConfigurationSection("Kits." + name().toUpperCase()));
  }

  private void loadKit(String name, ItemStack icon, ConfigurationSection configurationSection) {
    String kitName = configurationSection.getString("name");
    switch (name) {
      case "CIVILIAN":
        kits.put(name, new Civilian(kitName, icon, configurationSection));
        break;
      case "ACROBAT":
        kits.put(name, new Acrobat(kitName, icon, configurationSection));
        break;
      case "ALCHEMIST":
        kits.put(name, new Alchemist(kitName, icon, configurationSection));
        break;
      case "ARCHER":
        kits.put(name, new Archer(kitName, icon, configurationSection));
        break;
      case "BERSERKER":
        kits.put(name, new Berserker(kitName, icon, configurationSection));
        break;
      case "BLOODMAGE":
        kits.put(name, new Bloodmage(kitName, icon, configurationSection));
        break;
      case "BUILDER":

        break;
    }
  }

  public BaseKit getKit() {
    return kits.get(name());
  }

  public String getName() {
    return name().substring(0, 1) + name().substring(1).toLowerCase();
  }

  public boolean isOwnedBy(Player p) {
    Account account = Main.getInstance().getMainDatabase().getAccount(p.getUniqueId().toString(), p.getName());

    return p.isOp()
               || this == CIVILIAN
               || p.hasPermission("annihilation.class." + getName().toLowerCase())
               || (account != null && account.hasKit(this));
  }

  public void resetKit() {

  }
}

package com.hyuchiha.Annihilation.Game;

import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Kits.Implementations.Civilian;
import com.hyuchiha.Annihilation.Main;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public enum Kit {
  CIVILIAN("CIVILIAN", Material.WORKBENCH);

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
    switch (name) {
      case "CIVILIAN":
        kits.put(name, new Civilian(name, icon, configurationSection));
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
               || p.hasPermission("annihilation.class." + getName().toLowerCase());
               // || (account != null && account.hasKit(this));
  }
}
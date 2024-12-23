package com.hyuchiha.Annihilation.Game;

import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Kits.Implementations.*;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Utils.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public enum Kit {
  CIVILIAN("CIVILIAN", XMaterial.CRAFTING_TABLE.parseMaterial()),
  ACROBAT("ACROBAT", XMaterial.FEATHER.parseMaterial()),
  ALCHEMIST("ALCHEMIST", XMaterial.BREWING_STAND.parseMaterial()),
  ARCHER("ARCHER", XMaterial.BOW.parseMaterial()),
  BERSERKER("BERSERKER", XMaterial.CHAINMAIL_CHESTPLATE.parseMaterial()),
  BLOODMAGE("BLOODMAGE", XMaterial.MUSIC_DISC_BLOCKS.parseMaterial()),
  BUILDER("BUILDER", XMaterial.BRICK.parseMaterial()),
  DEFENDER("DEFENDER", XMaterial.SHIELD.parseMaterial()),
  ENCHANTER("ENCHANTER", XMaterial.ENCHANTING_TABLE.parseMaterial()),
  MINER("MINER", XMaterial.STONE_PICKAXE.parseMaterial()),
  PYRO("PYRO", XMaterial.FLINT_AND_STEEL.parseMaterial()),
  SCORPIO("SCORPIO", XMaterial.NETHER_STAR.parseMaterial()),
  SCOUT("SCOUT", XMaterial.FISHING_ROD.parseMaterial()),
  TRANSPORTER("TRANSPORTER", XMaterial.QUARTZ.parseMaterial()),
  VAMPIRE("VAMPIRE", XMaterial.REDSTONE.parseMaterial()),
  WARRIOR("WARRIOR", XMaterial.STONE_SWORD.parseMaterial());

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
        kits.put(name, new Builder(kitName, icon, configurationSection));
        break;
      case "DEFENDER":
        kits.put(name, new Defender(kitName, icon, configurationSection));
        break;
      case "ENCHANTER":
        kits.put(name, new Enchanter(kitName, icon, configurationSection));
        break;
      case "MINER":
        kits.put(name, new Miner(kitName, icon, configurationSection));
        break;
      case "PYRO":
        kits.put(name, new Pyro(kitName, icon, configurationSection));
        break;
      case "SCORPIO":
        kits.put(name, new Scorpio(kitName, icon, configurationSection));
        break;
      case "SCOUT":
        kits.put(name, new Scout(kitName, icon, configurationSection));
        break;
      case "TRANSPORTER":
        kits.put(name, new Transporter(kitName, icon, configurationSection));
        break;
      case "VAMPIRE":
        kits.put(name, new Vampire(kitName, icon, configurationSection));
        break;
      case "WARRIOR":
        kits.put(name, new Warrior(kitName, icon, configurationSection));
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

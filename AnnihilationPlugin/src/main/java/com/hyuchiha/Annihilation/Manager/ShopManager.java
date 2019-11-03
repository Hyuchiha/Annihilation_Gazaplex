package com.hyuchiha.Annihilation.Manager;

import com.hyuchiha.Annihilation.Game.Shop;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.configuration.Configuration;

public class ShopManager {
  private static final String Weapon = "Weapon";
  private static final String Brewing = "Brewing";

  private static Shop weaponShop;
  private static Shop brewingShop;

  public static void initShops() {
    Output.log("Iniciando los Shops");
    Configuration config = Main.getInstance().getConfig("shops.yml");
    weaponShop = new Shop(Main.getInstance(), Weapon, config);
    brewingShop = new Shop(Main.getInstance(), Brewing, config);
  }
}

package com.hyuchiha.Annihilation.Game;

import org.bukkit.inventory.ItemStack;

public class BossStarItem {
  private ItemStack item;
  private int position;

  public BossStarItem(ItemStack item, int position) {
    this.item = item;
    this.position = position;
  }

  public int getPosition() {
    return position;
  }

  public ItemStack getItem() {
    return item;
  }
}

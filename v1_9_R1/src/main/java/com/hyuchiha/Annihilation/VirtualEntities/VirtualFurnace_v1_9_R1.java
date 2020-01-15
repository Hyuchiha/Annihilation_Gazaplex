package com.hyuchiha.Annihilation.VirtualEntities;

import net.minecraft.server.v1_9_R1.EntityHuman;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.ItemStack;
import net.minecraft.server.v1_9_R1.TileEntityFurnace;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftInventoryFurnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class VirtualFurnace_v1_9_R1 extends TileEntityFurnace implements VirtualFurnace {

  private EntityPlayer handle;

  public VirtualFurnace_v1_9_R1(Player player) {
    this.handle = ((CraftPlayer) player).getHandle();
    this.world = handle.getWorld();
  }

  @Override
  public boolean canCook() {
    return getItem(0) != null && getItem(1) != null;
  }

  @Override
  public void cook() {
    this.c();
  }

  @Override
  public int a(ItemStack itemstack) {
    return 100;
  }

  @Override
  public boolean a(EntityHuman entity) {
    return true;
  }

  @Override
  public InventoryHolder getOwner() {
    return () -> new CraftInventoryFurnace(this);
  }

  @Override
  public void openFurnace() {
    handle.openContainer(this);
  }
}

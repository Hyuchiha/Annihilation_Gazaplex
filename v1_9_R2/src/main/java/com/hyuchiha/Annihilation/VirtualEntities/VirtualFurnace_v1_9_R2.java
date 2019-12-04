package com.hyuchiha.Annihilation.VirtualEntities;

import net.minecraft.server.v1_9_R2.EntityHuman;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.ItemStack;
import net.minecraft.server.v1_9_R2.TileEntityFurnace;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftInventoryFurnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class VirtualFurnace_v1_9_R2 extends TileEntityFurnace implements VirtualFurnace {

  private EntityPlayer handle;

  public VirtualFurnace_v1_9_R2(Player player) {
    this.handle = ((CraftPlayer) player).getHandle();
    this.world = handle.getWorld();
  }

  @Override
  public boolean canCook() {
    return getItem(0) != null && getItem(1) != null;
  }

  @Override
  public void cook() {
    if (!isBurning()) {
      burn();
    }
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

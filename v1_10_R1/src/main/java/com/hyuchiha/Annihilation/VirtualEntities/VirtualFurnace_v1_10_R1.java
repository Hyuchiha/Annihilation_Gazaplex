package com.hyuchiha.Annihilation.VirtualEntities;

import net.minecraft.server.v1_10_R1.EntityHuman;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.ItemStack;
import net.minecraft.server.v1_10_R1.TileEntityFurnace;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftInventoryBrewer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class VirtualFurnace_v1_10_R1 extends TileEntityFurnace implements VirtualFurnace {

  private EntityPlayer handle;

  public VirtualFurnace_v1_10_R1(Player player) {
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
    return () -> new CraftInventoryBrewer(VirtualFurnace_v1_10_R1.this);
  }

  @Override
  public void openFurnace() {
    handle.openContainer(this);
  }
}

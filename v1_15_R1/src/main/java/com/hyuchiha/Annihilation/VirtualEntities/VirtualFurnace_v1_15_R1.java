package com.hyuchiha.Annihilation.VirtualEntities;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftInventoryFurnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class VirtualFurnace_v1_15_R1 extends TileEntityFurnace implements VirtualFurnace {

  public EntityPlayer handle;

  public VirtualFurnace_v1_15_R1(Player player) {
    super(TileEntityTypes.BLAST_FURNACE, Recipes.BLASTING);

    this.handle = ((CraftPlayer) player).getHandle();
    this.world = handle.getWorld();
  }

  @Override
  public boolean canCook() {
    return !getItem(0).isEmpty() && (!getItem(1).isEmpty() || this.b.getProperty(1) > 0);
  }

  @Override
  public void cook() {
    tick();
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

  @Override
  protected IChatBaseComponent getContainerName() {
    return new ChatMessage("container.blast_furnace", new Object[0]);
  }

  @Override
  protected int fuelTime(ItemStack var0) {
    int fuelTime = super.fuelTime(var0);

    return fuelTime / 3;
  }

  @Override
  protected int getRecipeCookingTime() {
    return super.getRecipeCookingTime() / 4;
  }

  @Override
  protected Container createContainer(int i, PlayerInventory playerInventory) {
    return new ContainerBlastFurnace(i, playerInventory, this, this.b);
  }
}

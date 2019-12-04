package com.hyuchiha.Annihilation.VirtualEntities;

import net.minecraft.server.v1_13_R1.*;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftInventoryBrewer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class VirtualBrewingStand_v1_13_R1 extends TileEntityBrewingStand implements VirtualBrewingStand {

  private EntityPlayer handle;

  public VirtualBrewingStand_v1_13_R1(Player player) {
    this.handle = ((CraftPlayer) player).getHandle();
    this.world = handle.getWorld();
    setItem(4, new ItemStack(Items.BLAZE_POWDER, 64));
  }

  @Override
  public boolean canMakePotions() {
    return getProperty(1) <= 0
               && getContents().get(4) != null && getContents().get(4).getItem() == Items.BLAZE_POWDER
               && getContents().get(0) != null &&
               (getContents().get(1) != null
                    || getContents().get(2) != null
                    || getContents().get(3) != null);
  }

  @Override
  public void makePotions() {
    this.Y_();
  }

  @Override
  public boolean a(EntityHuman entity) {
    return true;
  }

  @Override
  public InventoryHolder getOwner() {
    return () -> new CraftInventoryBrewer(this);
  }


  @Override
  public void openBrewingStand() {
    handle.openContainer(this);
  }
}

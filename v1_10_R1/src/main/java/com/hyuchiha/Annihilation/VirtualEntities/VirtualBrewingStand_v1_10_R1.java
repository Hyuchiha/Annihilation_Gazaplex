package com.hyuchiha.Annihilation.VirtualEntities;

import net.minecraft.server.v1_10_R1.*;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftInventoryBrewer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class VirtualBrewingStand_v1_10_R1 extends TileEntityBrewingStand implements VirtualBrewingStand {

    private EntityPlayer handle;

    public VirtualBrewingStand_v1_10_R1(Player player) {
        this.handle = ((CraftPlayer) player).getHandle();
        this.world = handle.getWorld();
        setItem(4, new ItemStack(Items.BLAZE_POWDER, 64));
    }

    @Override
    public boolean canMakePotions() {
        return getProperty(1) <= 0
                && getContents()[4] != null && getContents()[4].getItem() == Items.BLAZE_POWDER
                && getContents()[0] != null &&
                (getContents()[1] != null
                        || getContents()[2] != null
                        || getContents()[3] != null);
    }

    @Override
    public void makePotions() {
        this.E_();
    }

    @Override
    public boolean a(EntityHuman entity) {
        return true;
    }

    @Override
    public InventoryHolder getOwner() {
        return () -> new CraftInventoryBrewer(VirtualBrewingStand_v1_10_R1.this);
    }

    @Override
    public void openBrewingStand() {
        handle.openContainer(this);
    }
}

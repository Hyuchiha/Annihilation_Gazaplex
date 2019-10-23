package com.hyuchiha.Annihilation.VirtualEntities;

import net.minecraft.server.v1_9_R2.*;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftInventoryBrewer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class VirtualBrewingStand_v1_9_R2 extends TileEntityBrewingStand implements VirtualBrewingStand {

    private EntityPlayer handle;

    public VirtualBrewingStand_v1_9_R2(Player player) {
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
        this.c();
    }

    @Override
    public boolean a(EntityHuman entity) {
        return true;
    }

    @Override
    public InventoryHolder getOwner() {
        return () -> new CraftInventoryBrewer(VirtualBrewingStand_v1_9_R2.this);
    }

    @Override
    public void openBrewingStand() {
        handle.openContainer(this);
    }
}

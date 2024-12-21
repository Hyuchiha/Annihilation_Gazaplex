package com.hyuchiha.Annihilation.VirtualEntities;

import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntityBrewingStand;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftInventoryBrewer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class VirtualBrewingStand_v1_21_R1 extends TileEntityBrewingStand implements VirtualBrewingStand {

    private EntityPlayer handle;

    public VirtualBrewingStand_v1_21_R1(Player player) {
        super(BlockPosition.c, Blocks.fs.o());

        this.handle = ((CraftPlayer) player).getHandle();
        this.n = this.handle.cN();

        b(4, new ItemStack(Items.so, 64));
    }

    @Override
    public boolean canMakePotions() {
        return this.f.a(1) <= 0
                && !getContents().get(4).e() && getContents().get(4).g() == Items.so
                && !getContents().get(0).e() &&
                (!getContents().get(1).e()
                    || !getContents().get(2).e()
                    || !getContents().get(3).e());
    }

    @Override
    public void makePotions() {
        TileEntityBrewingStand.a(this.n, BlockPosition.c, Blocks.fs.o(), this);
    }

    @Override
    public InventoryHolder getOwner() {
        return () -> new CraftInventoryBrewer(this);
    }

    @Override
    public void openBrewingStand() {
        handle.a(this);
    }
}

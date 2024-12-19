package com.hyuchiha.Annihilation.VirtualEntities;

import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntityBrewingStand;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftInventoryBrewer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class VirtualBrewingStand_v1_18_R1 extends TileEntityBrewingStand implements VirtualBrewingStand {

    private EntityPlayer handle;

    public VirtualBrewingStand_v1_18_R1(Player player) {
        super(BlockPosition.b, Blocks.ej.n());

        this.handle = ((CraftPlayer) player).getHandle();
        this.n = handle.cA();

        a(4, new ItemStack(Items.pJ, 64));
    }

    @Override
    public boolean canMakePotions() {
        return this.f.a(1) <= 0
                && !getContents().get(4).b() && getContents().get(4).c() == Items.pJ
                && !getContents().get(0).b() &&
                (!getContents().get(1).b()
                    || !getContents().get(2).b()
                    || !getContents().get(3).b());
    }

    @Override
    public void makePotions() {
        TileEntityBrewingStand.a(this.n, BlockPosition.b, Blocks.mi.n(), this);
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
        handle.a(this);
    }
}

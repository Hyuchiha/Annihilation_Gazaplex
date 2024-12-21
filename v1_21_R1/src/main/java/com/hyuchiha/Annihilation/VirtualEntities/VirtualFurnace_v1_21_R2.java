package com.hyuchiha.Annihilation.VirtualEntities;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerBlastFurnace;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntityFurnace;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftInventoryFurnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class VirtualFurnace_v1_21_R2 extends TileEntityFurnace implements VirtualFurnace {
    public EntityPlayer handle;

    public VirtualFurnace_v1_21_R2(Player player) {
        super(TileEntityTypes.D, BlockPosition.c, Blocks.nW.o(), Recipes.c);

        this.handle = ((CraftPlayer) player).getHandle();
        this.n = handle.cN();
    }

    @Override
    public boolean canCook() {
        ItemStack slot0 = getContents().get(0);
        ItemStack slot1 = getContents().get(1);
        return slot0.e() && (slot1.e() || this.m.a(1) > 0);
    }

    @Override
    public void cook() {
        TileEntityFurnace.a(this.n, BlockPosition.c, Blocks.nW.o(), this);
    }

    @Override
    public InventoryHolder getOwner() {
        return () -> new CraftInventoryFurnace(this);
    }

    @Override
    public void openFurnace() {
        handle.a(this);
    }

    @Override
    protected IChatBaseComponent k() {
        return IChatBaseComponent.c("container.blast_furnace");
    }

    @Override
    protected int b(ItemStack var0) {
        int fuelTime = super.b(var0);

        return fuelTime / 3;
    }

    @Override
    public void a(int i, ItemStack itemStack) {
        super.a(i, itemStack);

        ItemStack itemstack1 = (ItemStack)this.l.get(i);
        boolean flag = !itemStack.e() && ItemStack.c(itemstack1, itemStack);

        if (i == 0 && !flag) {
            this.w = this.w / 4;
            this.v = 0;
            this.e();
        }
    }

    @Override
    protected Container a(int i, PlayerInventory playerInventory) {
        return new ContainerBlastFurnace(i, playerInventory, this, this.m);
    }
}

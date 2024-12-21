package com.hyuchiha.Annihilation.VirtualEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftInventoryFurnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class VirtualFurnace_v1_21_R2 extends AbstractFurnaceBlockEntity implements VirtualFurnace {

    private ServerPlayer handle;

    public VirtualFurnace_v1_21_R2(Player player) {
        super(BlockEntityType.BLAST_FURNACE, BlockPos.ZERO, null, RecipeType.SMELTING);

        this.handle = ((CraftPlayer) player).getHandle();
        this.level = this.handle.level();
    }

    @Override
    public boolean canCook() {
        return !getItem(0).isEmpty() && (!getItem(1).isEmpty() || this.dataAccess.get(1) > 0);
    }

    @Override
    public void cook() {
        AbstractFurnaceBlockEntity.serverTick(this.level, this.worldPosition, null, this);
    }

    @Override
    public InventoryHolder getOwner() {
        return () -> new CraftInventoryFurnace(this);
    }

    @Override
    public void openFurnace() {
        handle.openMenu(this);
    }

    @Override
    protected int getBurnDuration(ItemStack itemstack) {
        int burnTime = super.getBurnDuration(itemstack);

        return burnTime / 3;
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        super.setItem(i, itemstack);

        ItemStack itemstack1 = (ItemStack)this.items.get(i);
        boolean flag = !itemstack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack1, itemstack);

        if (i == 0 && !flag) {
            this.cookingTotalTime = this.cookingTotalTime / 4;
            this.cookingProgress = 0;
            this.setChanged();
        }
    }


// New Methods

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.brewing");
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new BlastFurnaceMenu(i, inventory, this, this.dataAccess);
    }
}

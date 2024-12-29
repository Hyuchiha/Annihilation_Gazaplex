package com.hyuchiha.Annihilation.VirtualEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FuelValues;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftInventoryFurnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class VirtualFurnace_v1_21_R2 extends AbstractFurnaceBlockEntity implements VirtualFurnace {
    private ServerPlayer handle;

    public VirtualFurnace_v1_21_R2(Player player) {
        super(BlockEntityType.BLAST_FURNACE, BlockPos.ZERO, Blocks.BLAST_FURNACE.defaultBlockState(), RecipeType.BLASTING);

        this.handle = ((CraftPlayer) player).getHandle();
        this.level = this.handle.level();
    }

    @Override
    public boolean canCook() {
        return !getItem(0).isEmpty() && (!getItem(1).isEmpty() || this.dataAccess.get(1) > 0);
    }

    @Override
    public void cook() {
        AbstractFurnaceBlockEntity.serverTick((ServerLevel) this.getLevel(), BlockPos.ZERO, Blocks.BLAST_FURNACE.defaultBlockState(), this);
    }

    @Override
    public boolean stillValid(net.minecraft.world.entity.player.Player player) {
        return true; // Allow player to interact with the furnace
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
    protected int getBurnDuration(FuelValues values, ItemStack itemstack) {
        int burnTime = super.getBurnDuration(values, itemstack);

        return burnTime / 3;
    }

    @Override
    public void setItem(int i, ItemStack itemstack) {
        super.setItem(i, itemstack);

        ItemStack itemstack1 = this.items.get(i);
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

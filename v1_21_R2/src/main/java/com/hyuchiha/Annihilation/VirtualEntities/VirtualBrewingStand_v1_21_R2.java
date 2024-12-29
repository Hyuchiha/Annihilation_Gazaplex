package com.hyuchiha.Annihilation.VirtualEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftInventoryBrewer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class VirtualBrewingStand_v1_21_R2 extends BrewingStandBlockEntity implements VirtualBrewingStand {

    private ServerPlayer handle;

    public VirtualBrewingStand_v1_21_R2(Player player) {
        super(BlockPos.ZERO, Blocks.BREWING_STAND.defaultBlockState());

        this.handle = ((CraftPlayer) player).getHandle();
        this.level = this.handle.level();

        ItemStack blazePower = new ItemStack(Items.BLAZE_POWDER, 64);
        setItem(4, blazePower);
    }

    @Override
    public boolean canMakePotions() {
        return this.dataAccess.get(1) <= 0
                && !getContents().get(4).isEmpty() && getContents().get(4).is(Items.BLAZE_POWDER)
                && !getContents().get(0).isEmpty() &&
                (!getContents().get(1).isEmpty()
                        || !getContents().get(2).isEmpty()
                        || !getContents().get(3).isEmpty());
    }

    @Override
    public void makePotions() {
        BrewingStandBlockEntity.serverTick(this.getLevel(), BlockPos.ZERO, Blocks.BREWING_STAND.defaultBlockState(), this);
    }

    @Override
    public InventoryHolder getOwner() {
        return () -> new CraftInventoryBrewer(this);
    }

    @Override
    public void openBrewingStand() {
        handle.openMenu(this);
    }
}

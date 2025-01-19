package com.hyuchiha.Annihilation.VirtualEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftInventoryBrewer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class VirtualBrewingStand_v1_20_R1 extends BrewingStandBlockEntity implements VirtualBrewingStand {
    private ServerPlayer handle;

    public VirtualBrewingStand_v1_20_R1(Player player) {
        super(BlockPos.ZERO, Blocks.BREWING_STAND.defaultBlockState());

        this.handle = ((CraftPlayer) player).getHandle();
        this.level = this.handle.level();

        ItemStack blazePower = new ItemStack(Items.BLAZE_POWDER, 64);
        setItem(4, blazePower);
    }

    @Override
    public boolean canMakePotions() {
        return !getContents().get(3).isEmpty();
    }

    @Override
    public void makePotions() {
        BrewingStandBlockEntity.serverTick(this.getLevel(), BlockPos.ZERO, Blocks.BREWING_STAND.defaultBlockState(), this);
    }

    @Override
    public boolean stillValid(net.minecraft.world.entity.player.Player player) {
        return true; // Allow player to interact with the furnace
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

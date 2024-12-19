package com.hyuchiha.Annihilation.VirtualEntities;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerBlastFurnace;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntityFurnace;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftInventoryFurnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class VirtualFurnace_v1_18_R1 extends TileEntityFurnace implements VirtualFurnace {

    public EntityPlayer handle;

    protected VirtualFurnace_v1_18_R1(Player player) {
        super(TileEntityTypes.B, BlockPosition.b, Blocks.mi.n(), Recipes.c);

        this.handle = ((CraftPlayer) player).getHandle();
        this.n = handle.cA();
    }

    @Override
    public boolean canCook() {
        ItemStack slot0 = a(0);
        ItemStack slot1 = a(1);
        return a(0).b() && (!a(1).b() || this.m.a(1) > 0);
    }

    @Override
    public void cook() {
        TileEntityFurnace.a(this.n, BlockPosition.b, Blocks.mi.n(), this);
    }

    @Override
    public boolean a(EntityHuman entity) {
        return true;
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
    protected IChatBaseComponent g() {
        return new ChatMessage("container.furnace");
    }

    @Override
    protected int a(ItemStack var0) {
        int fuelTime = super.a(var0);

        return fuelTime / 3;
    }

    @Override
    public void a(int i, ItemStack itemStack) {
        super.a(i, itemStack);

        ItemStack itemstack1 = (ItemStack)this.l.get(i);
        boolean flag = !itemStack.b() && itemStack.a(itemstack1) && ItemStack.a(itemStack, itemstack1);

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

package com.hyuchiha.Annihilation.Base;

import net.minecraft.network.protocol.game.PacketPlayInClientCommand;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Respawner_v1_21_R1 implements Respawner{
    @Override
    public void respawn(Player player) {
        PacketPlayInClientCommand in = new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.a);
        EntityPlayer cPlayer = ((CraftPlayer) player).getHandle();
        cPlayer.c.a(in);
    }
}

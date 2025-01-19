package com.hyuchiha.Annihilation.Base;

import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Respawner_v1_20_R3 implements Respawner {

    @Override
    public void respawn(Player player) {
        ServerboundClientCommandPacket in = new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN);
        ServerPlayer cPlayer = ((CraftPlayer) player).getHandle();
        cPlayer.connection.handleClientCommand(in);
    }
}

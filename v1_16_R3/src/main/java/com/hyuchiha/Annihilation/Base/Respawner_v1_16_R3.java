package com.hyuchiha.Annihilation.Base;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayInClientCommand;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Respawner_v1_16_R3 implements Respawner{
  @Override
  public void respawn(Player player) {
    PacketPlayInClientCommand in = new PacketPlayInClientCommand(
        PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN);
    EntityPlayer cPlayer = ((CraftPlayer) player).getHandle();
    cPlayer.playerConnection.a(in);
  }
}

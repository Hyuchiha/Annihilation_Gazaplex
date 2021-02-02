package com.hyuchiha.Annihilation.Base;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkHelper_v1_9_R2 implements ChunkHelper{

  @Override
  public void forceChunkLoad(World world, Chunk chunk) {
    // Nothing to do
  }

  @Override
  public void cancelChunkUnload(ChunkUnloadEvent event) {
    event.setCancelled(true);
  }
}

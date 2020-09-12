package com.hyuchiha.Annihilation.Base;

import org.bukkit.Chunk;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkHelper_v1_9_R1 implements ChunkHelper{
  @Override
  public void forceChunkLoad(Chunk chunk) {
    // Nothing to do
  }

  @Override
  public void cancelChunkUnload(ChunkUnloadEvent event) {
    event.setCancelled(true);
  }
}

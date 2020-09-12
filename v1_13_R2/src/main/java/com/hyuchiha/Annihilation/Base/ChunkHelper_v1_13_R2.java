package com.hyuchiha.Annihilation.Base;

import org.bukkit.Chunk;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkHelper_v1_13_R2 implements ChunkHelper {
  @Override
  public void forceChunkLoad(Chunk chunk) {
    chunk.setForceLoaded(true);
  }

  @Override
  public void cancelChunkUnload(ChunkUnloadEvent event) {
    // Need to check if this still works in version 1_13_R2
    // event.setCancelled(true);
  }
}

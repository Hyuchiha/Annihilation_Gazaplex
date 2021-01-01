package com.hyuchiha.Annihilation.Base;

import org.bukkit.Chunk;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkHelper_v1_15_R1  implements ChunkHelper{
  @Override
  public void forceChunkLoad(Chunk chunk) {
    chunk.setForceLoaded(true);
  }

  @Override
  public void cancelChunkUnload(ChunkUnloadEvent event) {
    // Nothing to do
  }
}

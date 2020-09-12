package com.hyuchiha.Annihilation.Base;

import org.bukkit.Chunk;
import org.bukkit.event.world.ChunkUnloadEvent;

public interface ChunkHelper {
  void forceChunkLoad(Chunk chunk);
  void cancelChunkUnload(ChunkUnloadEvent event);
}

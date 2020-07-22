package com.hyuchiha.Annihilation.Utils;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.world.ChunkUnloadEvent;

public class BaseUtils {

  public static boolean isANewVersionSign(Block block) {
    BlockData data = block.getBlockData();
    return data instanceof Sign || data instanceof WallSign;
  }

  public static void forceChunkLoad(Chunk chunk) {
    chunk.setForceLoaded(true);
  }

}

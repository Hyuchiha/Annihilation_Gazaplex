package com.hyuchiha.Annihilation.Base;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkHelper_v1_19_R2 implements ChunkHelper{
    @Override
    public void forceChunkLoad(World world, Chunk chunk) {
        world.setChunkForceLoaded(chunk.getX(), chunk.getZ(), true);
        chunk.setForceLoaded(true);
    }

    @Override
    public void cancelChunkUnload(ChunkUnloadEvent event) {
        // Nothing to do
    }
}

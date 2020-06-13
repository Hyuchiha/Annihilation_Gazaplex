package com.hyuchiha.Annihilation.Maps;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class VoidGenerator extends ChunkGenerator {
  @Override
  public Location getFixedSpawnLocation(@NotNull World world, @NotNull Random rand) {
    return new Location(world, 0, world.getHighestBlockYAt(0, 0), 0);
  }

  @Override
  public ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int chunkX, int chunkZ, @NotNull BiomeGrid biome) {
    ChunkData chunkData = super.createChunkData(world);

    // Set biome.
    for (int x = 0; x < 16; x++) {
      for (int z = 0; z < 16; z++) {
        biome.setBiome(x, z, Biome.PLAINS);
      }
    }

    // Return the new chunk data.
    return chunkData;
  }
}

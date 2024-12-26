package com.hyuchiha.Annihilation.Maps;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VoidGenerator extends ChunkGenerator {
  @Override
  public Location getFixedSpawnLocation(@NotNull World world, @NotNull Random rand) {
    return new Location(world, 0, world.getHighestBlockYAt(0, 0), 0);
  }

  @Override
  public List<BlockPopulator> getDefaultPopulators(World world) {
    return new ArrayList<BlockPopulator>();
  }

  @Override
  public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ,
                            @NotNull ChunkData chunkData) {
    // No need to generate noise, we want an empty world
  }
  @Override
  public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ,
                              @NotNull ChunkData chunkData) {
    // No need to generate surface, we want an empty world
  }
  @Override
  public void generateBedrock(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ,
                              @NotNull ChunkData chunkData) {
    // No need to generate bedrock, we want an empty world
  }
  @Override
  public void generateCaves(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ,
                            @NotNull ChunkData chunkData) {
    // No need to generate caves, we want an empty world
  }

  @Override
  public boolean shouldGenerateStructures() {
    return false; // Disable structures
  }

  @Override
  public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int chunkX, int chunkZ, @NotNull BiomeGrid biome) {
    ChunkData chunkData = Bukkit.getServer().createChunkData(world);

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

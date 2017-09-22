package com.adrien.games.blocks.world.terrain.generator;

import com.adrien.games.blocks.utils.PerlinNoise;
import com.adrien.games.blocks.world.Chunk;
import com.adrien.games.blocks.world.World;
import com.adrien.games.blocks.world.block.Block;
import com.adrien.games.blocks.world.block.BlockType;
import com.adrien.games.blocks.world.terrain.biome.Biome;
import com.adrien.games.blocks.world.terrain.biome.GroundZeroBiome;
import com.adrien.games.blocks.world.terrain.biome.OceanBiome;
import com.adrien.games.blocks.world.terrain.biome.WeirdBiome;

public class NoiseTerrainGenerator implements TerrainGenerator {

    @Override
    public Block[] generatorChunkBlocks(final int chunkX, final int chunkY, final int chunkZ) {
        // TODO: get something nice...
        final Block[] blocks = new Block[World.BLOCK_PER_CHUNK];
        final float noiseStep = 0.05f;
        final double noise = PerlinNoise.noise((float) chunkX * noiseStep, 0, (float) chunkZ * noiseStep) * 2 - 1;
        final Biome biome = this.getBiome(noise);

        for (int x = 0; x < World.CHUNK_WIDTH; x++) {
            for (int z = 0; z < World.CHUNK_DEPTH; z++) {
                for (int y = 0; y < World.CHUNK_HEIGHT; y++) {
                    final int worldX = x + World.CHUNK_WIDTH * chunkX;
                    final int worldY = y + World.CHUNK_HEIGHT * chunkY;
                    final int worldZ = z + World.CHUNK_DEPTH * chunkZ;
                    final BlockType blockType = biome.computeBlockType(worldX, worldY, worldZ);
                    blocks[Chunk.indexFromPosition(x, y, z)] = new Block(x, y, z, worldX, worldY, worldZ, blockType);
                }
            }
        }
        return blocks;
    }

    private Biome getBiome(final double noiseValue) {
        if (noiseValue < -0.2) {
            return new OceanBiome();
        } else if (noiseValue < 0.1) {
            return new GroundZeroBiome();
        }
        return new WeirdBiome();
    }

}

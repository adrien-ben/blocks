package com.adrien.games.blocks.world.terrain.generator;

import com.adrien.games.bagl.core.math.Noise;
import com.adrien.games.blocks.world.Chunk;
import com.adrien.games.blocks.world.World;
import com.adrien.games.blocks.world.block.Block;
import com.adrien.games.blocks.world.block.BlockType;
import com.adrien.games.blocks.world.terrain.biome.*;

public class HeightMapTerrainGenerator implements TerrainGenerator {

    @Override
    public void generateChunkBlocks(final int chunkX, final int chunkY, final int chunkZ, final Block[] blocks) {
        // TODO: get something nice...
        for (int x = 0; x < World.CHUNK_WIDTH; x++) {
            for (int z = 0; z < World.CHUNK_DEPTH; z++) {

                final int worldX = x + World.CHUNK_WIDTH * chunkX;
                final int worldZ = z + World.CHUNK_DEPTH * chunkZ;
                final float noiseStep = 0.006f;
                final float noise = Noise.perlin((float) worldX * noiseStep, 0, (float) worldZ * noiseStep, 5, 0.3f);
                final float elevation = (float) Math.pow(noise, 0.8) * World.CHUNK_HEIGHT;
                final Biome biome = this.getBiome(elevation);

                for (int y = 0; y < World.CHUNK_HEIGHT; y++) {
                    final int worldY = y + World.CHUNK_HEIGHT * chunkY;
                    final BlockType blockType = this.getBlockType(worldY, (int) elevation, biome);
                    blocks[Chunk.indexFromPosition(x, y, z)] = new Block(x, y, z, worldX, worldY, worldZ, blockType);
                }
            }
        }
    }

    private BlockType getBlockType(final int worldY, final int height, final Biome biome) {
        if (worldY > height) {
            if (worldY < World.WATER_LEVEL) {
                return BlockType.WATER;
            } else {
                return BlockType.AIR;
            }
        } else {
            return biome.computeBlockType(worldY, height);
        }
    }

    private Biome getBiome(final double elevation) {
        if (elevation < World.WATER_LEVEL - 2) {
            return new OceanBiome();
        } else if (elevation < World.WATER_LEVEL + 8) {
            return new BeachBiome();
        } else if (elevation < World.WATER_LEVEL + 16) {
            return new PlainBiome();
        } else {
            return new MountainBiome();
        }
    }

}

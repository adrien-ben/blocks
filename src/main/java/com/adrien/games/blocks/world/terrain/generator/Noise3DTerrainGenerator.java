package com.adrien.games.blocks.world.terrain.generator;

import com.adrien.games.blocks.world.Chunk;
import com.adrien.games.blocks.world.World;
import com.adrien.games.blocks.world.block.Block;
import com.adrien.games.blocks.world.block.BlockType;
import com.adrienben.games.bagl.core.math.Noise;

public class Noise3DTerrainGenerator implements TerrainGenerator {

    @Override
    public void generateChunkBlocks(final int chunkX, final int chunkY, final int chunkZ, final Block[] blocks) {
        final float frequency = 0.02f;
        for (int x = 0; x < World.CHUNK_WIDTH; x++) {
            for (int y = 0; y < World.CHUNK_HEIGHT; y++) {
                for (int z = 0; z < World.CHUNK_DEPTH; z++) {
                    final int worldX = chunkX * World.CHUNK_WIDTH + x;
                    final int worldY = chunkY * World.CHUNK_HEIGHT + y;
                    final int worldZ = chunkZ * World.CHUNK_DEPTH + z;
                    final double noise = Noise.perlin(frequency * worldX, frequency * worldY, frequency * worldZ);
                    final BlockType type = this.getBlockType(noise, worldY);
                    blocks[Chunk.indexFromPosition(x, y, z)] = new Block(x, y, z, worldX, worldY, worldZ, type);
                }
            }
        }
    }

    private BlockType getBlockType(final double noise, final int worldY) {
        if (noise < 0.6) {
            return BlockType.STONE;
        } else {
            if (worldY < World.WATER_LEVEL) {
                return BlockType.WATER;
            }
            return BlockType.AIR;
        }
    }

}

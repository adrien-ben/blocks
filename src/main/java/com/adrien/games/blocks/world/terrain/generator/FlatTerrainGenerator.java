package com.adrien.games.blocks.world.terrain.generator;

import com.adrien.games.blocks.world.Chunk;
import com.adrien.games.blocks.world.World;
import com.adrien.games.blocks.world.block.Block;
import com.adrien.games.blocks.world.block.BlockType;

public class FlatTerrainGenerator implements TerrainGenerator {

    @Override
    public void generateChunkBlocks(final int chunkX, final int chunkY, final int chunkZ, final Block[] blocks) {
        for (int x = 0; x < World.CHUNK_WIDTH; x++) {
            for (int z = 0; z < World.CHUNK_DEPTH; z++) {
                for (int y = 0; y < World.CHUNK_HEIGHT; y++) {
                    final int worldX = x + World.CHUNK_WIDTH * chunkX;
                    final int worldY = y + World.CHUNK_HEIGHT * chunkY;
                    final int worldZ = z + World.CHUNK_DEPTH * chunkZ;
                    blocks[Chunk.indexFromPosition(x, y, z)] = new Block(x, y, z, worldX, worldY, worldZ, this.getBlockType(worldY));
                }
            }
        }
    }

    private BlockType getBlockType(final int worldY) {
        if (worldY < World.WATER_LEVEL - 1) {
            return BlockType.DIRT;
        } else if (worldY < World.WATER_LEVEL) {
            return BlockType.GRASS;
        } else {
            return BlockType.AIR;
        }
    }

}

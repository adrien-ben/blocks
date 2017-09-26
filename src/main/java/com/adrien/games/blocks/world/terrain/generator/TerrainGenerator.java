package com.adrien.games.blocks.world.terrain.generator;

import com.adrien.games.blocks.world.block.Block;

public interface TerrainGenerator {

    void generateChunkBlocks(int chunkX, int chunkY, int chunkZ, Block[] blocks);

}

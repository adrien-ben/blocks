package com.adrien.games.blocks.world.terrain.generator;

import com.adrien.games.blocks.world.block.Block;

public interface TerrainGenerator {

    Block[] generatorChunkBlocks(int chunkX, int chunkY, int chunkZ);

}

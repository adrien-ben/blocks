package com.adrien.games.blocks.world.terrain.biome;

import com.adrien.games.blocks.world.block.BlockType;

public interface Biome {

    BlockType computeBlockType(int worldX, int worldY, int worldZ);

}

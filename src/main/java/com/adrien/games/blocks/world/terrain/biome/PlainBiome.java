package com.adrien.games.blocks.world.terrain.biome;

import com.adrien.games.blocks.world.block.BlockType;

public class PlainBiome implements Biome {

    @Override
    public BlockType computeBlockType(final int worldY, final int maxHeight) {
        if (worldY == maxHeight) {
            return BlockType.GRASS;
        }
        return BlockType.DIRT;
    }

}

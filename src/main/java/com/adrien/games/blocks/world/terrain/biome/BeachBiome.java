package com.adrien.games.blocks.world.terrain.biome;

import com.adrien.games.blocks.world.block.BlockType;

public class BeachBiome implements Biome {

    @Override
    public BlockType computeBlockType(final int worldY, final int maxHeight) {
        return BlockType.SAND;
    }

}

package com.adrien.games.blocks.world.terrain.biome;

import com.adrien.games.blocks.world.World;
import com.adrien.games.blocks.world.block.BlockType;

public class GroundZeroBiome implements Biome {

    @Override
    public BlockType computeBlockType(int worldX, int worldY, int worldZ) {
        if (worldY < World.WATER_LEVEL - 1) {
            return BlockType.DIRT;
        } else if (worldY < World.WATER_LEVEL) {
            return BlockType.GRASS;
        }
        return BlockType.AIR;
    }

}

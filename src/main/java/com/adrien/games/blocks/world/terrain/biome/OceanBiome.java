package com.adrien.games.blocks.world.terrain.biome;

import com.adrien.games.blocks.world.World;
import com.adrien.games.blocks.world.block.BlockType;

public class OceanBiome implements Biome {

    @Override
    public BlockType computeBlockType(int worldX, int worldY, int worldZ) {
        if (worldY == 0) {
            return BlockType.STONE;
        } else if (worldY < World.WATER_LEVEL) {
            return BlockType.WATER;
        }
        return BlockType.AIR;

    }
}

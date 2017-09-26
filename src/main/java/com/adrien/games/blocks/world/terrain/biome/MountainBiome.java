package com.adrien.games.blocks.world.terrain.biome;

import com.adrien.games.blocks.world.World;
import com.adrien.games.blocks.world.block.BlockType;

public class MountainBiome implements Biome {

    @Override
    public BlockType computeBlockType(int worldY, int maxHeight) {
        if (worldY > World.WATER_LEVEL + 30 && worldY == maxHeight) {
            return BlockType.SNOW;
        }
        return BlockType.STONE;
    }

}

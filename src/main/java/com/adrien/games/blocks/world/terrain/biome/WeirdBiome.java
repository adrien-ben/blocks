package com.adrien.games.blocks.world.terrain.biome;

import com.adrien.games.bagl.utils.MathUtils;
import com.adrien.games.blocks.utils.PerlinNoise;
import com.adrien.games.blocks.world.World;
import com.adrien.games.blocks.world.block.BlockType;

public class WeirdBiome implements Biome {

    @Override
    public BlockType computeBlockType(int worldX, int worldY, int worldZ) {

        final float noiseStep = 0.025f;
        final float maxHeight = 1f;
        final float maxDepth = -0.5f;
        final double noiseValue = PerlinNoise.noise((float) worldX * noiseStep, 0, (float) worldZ * noiseStep, 5, 0.2) * 2 - 1;
        final double weight = 1 - (noiseValue >= 0 ? noiseValue / maxHeight : noiseValue / maxDepth);
        final double height = (MathUtils.clamp((float) (noiseValue * weight), -1.0f, 1.0f) * 0.5 + 0.5) * World.WORLD_MAX_HEIGHT * World.CHUNK_HEIGHT;

        if (worldY > height) {
            if (worldY < World.WATER_LEVEL) {
                return BlockType.WATER;
            }
            return BlockType.AIR;
        }
        return BlockType.DIRT;
    }

}

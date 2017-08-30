package com.adrien.games.blocks.world.block;

public enum BlockType {

    AIR(Constants.NONE, Constants.NONE),
    DIRT(0, 0),
    STONE(1, 0),
    GRASS(2, 0);

    private final float leftUV;
    private final float topUV;
    private final float rightUV;
    private final float bottomUV;

    BlockType(final int xPosition, final int yPosition) {
        this.leftUV = (float) xPosition / Constants.ATLAS_WIDTH + Constants.HALF_PIXEL_X;
        this.bottomUV = (float) yPosition / Constants.ATLAS_HEIGHT + Constants.HALF_PIXEL_Y;
        this.rightUV = (float) (xPosition + 1) / Constants.ATLAS_WIDTH - Constants.HALF_PIXEL_X;
        this.topUV = (float) (yPosition + 1) / Constants.ATLAS_HEIGHT - Constants.HALF_PIXEL_Y;
    }

    public float getLeftUV() {
        return leftUV;
    }

    public float getTopUV() {
        return topUV;
    }

    public float getRightUV() {
        return rightUV;
    }

    public float getBottomUV() {
        return bottomUV;
    }

    private static final class Constants {
        private static final int NONE = -1;
        private static final float REGION_SIZE = 32f;
        private static final int ATLAS_WIDTH = 3;
        private static final int ATLAS_HEIGHT = 1;
        private static final float HALF_PIXEL_X = 1f / REGION_SIZE * ATLAS_WIDTH;
        private static final float HALF_PIXEL_Y = 1f / REGION_SIZE * ATLAS_HEIGHT;
    }

}

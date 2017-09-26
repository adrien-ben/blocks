package com.adrien.games.blocks.world.block;

public enum BlockType {

    AIR(Constants.NONE, Constants.NONE),
    WATER(Constants.NONE, Constants.NONE),
    DIRT(0, 0),
    STONE(1, 0),
    GRASS(2, 0),
    SAND(3, 0),
    SNOW(4, 0);

    private final byte leftUV;
    private final byte bottomUV;
    private final byte rightUV;
    private final byte topUV;

    BlockType(final int xPosition, final int yPosition) {
        this.leftUV = this.normalizeValue((float) xPosition / Constants.REGIONS_PER_ROW + Constants.HALF_PIXEL_X);
        this.bottomUV = this.normalizeValue((float) yPosition / Constants.REGIONS_PER_COLUMN + Constants.HALF_PIXEL_Y);
        this.rightUV = this.normalizeValue((float) (xPosition + 1) / Constants.REGIONS_PER_ROW - Constants.HALF_PIXEL_X);
        this.topUV = this.normalizeValue((float) (yPosition + 1) / Constants.REGIONS_PER_COLUMN - Constants.HALF_PIXEL_Y);
    }

    private byte normalizeValue(final float value) {
        return (byte) (value * 256 - 128);
    }

    public byte getLeftUV() {
        return leftUV;
    }

    public byte getTopUV() {
        return topUV;
    }

    public byte getRightUV() {
        return rightUV;
    }

    public byte getBottomUV() {
        return bottomUV;
    }

    private static final class Constants {
        private static final int NONE = -1;
        private static final int REGIONS_PER_ROW = 5;
        private static final int REGIONS_PER_COLUMN = 1;
        private static final int REGION_SIZE_IN_PIXELS = 32;
        private static final int ATLAS_WIDTH_IN_PIXELS = REGIONS_PER_ROW * REGION_SIZE_IN_PIXELS;
        private static final int ATLAS_HEIGHT_IN_PIXELS = REGIONS_PER_COLUMN * REGION_SIZE_IN_PIXELS;
        private static final float HALF_PIXEL_X = 0.5f / ATLAS_WIDTH_IN_PIXELS;
        private static final float HALF_PIXEL_Y = 0.5f / ATLAS_HEIGHT_IN_PIXELS;
    }

}

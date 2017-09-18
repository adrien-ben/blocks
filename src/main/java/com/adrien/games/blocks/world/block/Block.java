package com.adrien.games.blocks.world.block;

public class Block {

    private final int chunkX;
    private final int chunkY;
    private final int chunkZ;
    private final int worldX;
    private final int worldY;
    private final int worldZ;
    private BlockType type;

    public Block(int chunkX, int chunkY, int chunkZ, int worldX, int worldY, int worldZ, BlockType type) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.chunkZ = chunkZ;
        this.worldX = worldX;
        this.worldY = worldY;
        this.worldZ = worldZ;
        this.type = type;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkY() {
        return chunkY;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public int getWorldX() {
        return worldX;
    }

    public int getWorldY() {
        return worldY;
    }

    public int getWorldZ() {
        return worldZ;
    }

    public BlockType getType() {
        return type;
    }

    public void setType(BlockType type) {
        this.type = type;
    }

    public boolean isAir() {
        return BlockType.AIR.equals(this.type);
    }

    public boolean isNotAir() {
        return !this.isAir();
    }

    public boolean isInvisible() {
        return BlockType.AIR.equals(this.type) || BlockType.WATER.equals(this.type);
    }

    public boolean isVisible() {
        return !isInvisible();
    }

}

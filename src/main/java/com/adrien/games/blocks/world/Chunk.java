package com.adrien.games.blocks.world;

import com.adrien.games.blocks.rendering.chunk.ChunkMesh;
import com.adrien.games.blocks.rendering.chunk.ChunkMeshPool;
import com.adrien.games.blocks.world.block.Block;
import com.adrien.games.blocks.world.block.BlockType;

import java.util.stream.Stream;

public class Chunk {

    private final int x;
    private final int y;
    private final int z;
    private Block[] blocks;
    private ChunkMesh mesh;
    private boolean loaded;

    public Chunk(final int x, final int y, final int z, final ChunkMeshPool pool) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blocks = null;
        this.mesh = pool.getChunkMesh();
        this.loaded = false;
    }

    public void load() {
        if (!this.loaded) {
            this.blocks = this.initBlocks();
            this.mesh.update(this);
            this.loaded = true;
        } else {
            this.mesh.update(this);
        }
    }

    public boolean addBlock(final int x, final int y, final int z, final BlockType type) {
        if (!this.loaded) {
            return false;
        }
        if (x < 0 || x >= World.CHUNK_WIDTH || y < 0 || y >= World.CHUNK_HEIGHT || z < 0 || z >= World.CHUNK_DEPTH) {
            throw new IllegalArgumentException("Impossible to add block outside of chunk's bounds (" + x + ", " + y + ", " + z + ")");
        }
        final Block block = this.blocks[this.indexFromPosition(x, y, z)];
        if (type.equals(block.getType()) || !(block.isAir() || BlockType.AIR.equals(type))) {
            return false;
        }
        block.setType(type);
        return true;
    }

    private Block[] initBlocks() {
        final Block[] blocks = new Block[World.BLOCK_PER_CHUNK];
        for (int x = 0; x < World.CHUNK_WIDTH; x++) {
            for (int y = 0; y < World.CHUNK_HEIGHT; y++) {
                for (int z = 0; z < World.CHUNK_DEPTH; z++) {
                    blocks[this.indexFromPosition(x, y, z)] = this.createBlock(x, y, z);
                }
            }
        }
        return blocks;
    }

    private Block createBlock(final int x, final int y, final int z) {
        final BlockType type = this.getBlockType(y + World.CHUNK_HEIGHT * this.y);
        return new Block(x, y, z, x + World.CHUNK_WIDTH * this.x, y + World.CHUNK_HEIGHT * this.y, z + World.CHUNK_DEPTH * this.z, type);
    }

    private BlockType getBlockType(final int y) {
        if (y < (World.WORLD_MAX_HEIGHT * World.CHUNK_HEIGHT - 1) * 0.8) {
            return BlockType.STONE;
        }
        if (y == World.WORLD_MAX_HEIGHT * World.CHUNK_HEIGHT - 1) {
            return BlockType.GRASS;
        }
        return BlockType.DIRT;
    }

    private int indexFromPosition(int x, int y, int z) {
        return x * World.CHUNK_HEIGHT * World.CHUNK_DEPTH + y * World.CHUNK_DEPTH + z;
    }

    public Block getBlock(final int x, final int y, final int z) {
        final int index = this.indexFromPosition(x, y, z);
        if (index < 0 || index >= World.CHUNK_WIDTH * World.CHUNK_HEIGHT * World.CHUNK_DEPTH) {
            throw new IndexOutOfBoundsException("Cannot access block at position " + x + " " + y + " " + z);
        }
        return this.blocks[index];
    }

    @Override
    public String toString() {
        return "Chunk{x=" + x + ", y=" + y + ", z=" + z + "}";
    }

    public Stream<Block> getBlocks() {
        return Stream.of(this.blocks);
    }

    public ChunkMesh getMesh() {
        return mesh;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

}

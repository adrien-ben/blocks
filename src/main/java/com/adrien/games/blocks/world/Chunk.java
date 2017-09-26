package com.adrien.games.blocks.world;

import com.adrien.games.blocks.rendering.chunk.ChunkMesh;
import com.adrien.games.blocks.rendering.chunk.ChunkMeshPool;
import com.adrien.games.blocks.world.block.Block;
import com.adrien.games.blocks.world.block.BlockType;
import com.adrien.games.blocks.world.terrain.generator.TerrainGenerator;

import java.util.Objects;
import java.util.stream.Stream;

public class Chunk {

    private final int x;
    private final int y;
    private final int z;
    private Block[] blocks;
    private TerrainGenerator generator;
    private ChunkMesh mesh;
    private boolean loaded;

    public Chunk(final int x, final int y, final int z, final ChunkMeshPool pool, final TerrainGenerator generator) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blocks = null;
        this.generator = generator;
        this.mesh = pool.getChunkMesh();
        this.loaded = false;
    }

    public void load() {
        if (!this.loaded) {
            this.blocks = new Block[World.BLOCK_PER_CHUNK];
            this.generator.generateChunkBlocks(this.x, this.y, this.z, this.blocks);
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
        final Block block = this.blocks[indexFromPosition(x, y, z)];
        if (type.equals(block.getType()) || BlockType.WATER.equals(type) || !(block.isAir() || BlockType.AIR.equals(type))) {
            return false;
        }
        block.setType(type);
        return true;
    }

    public static int indexFromPosition(int x, int y, int z) {
        return x * World.CHUNK_HEIGHT * World.CHUNK_DEPTH + y * World.CHUNK_DEPTH + z;
    }

    public Block getBlock(final int x, final int y, final int z) {
        if (Objects.isNull(this.blocks)) {
            return null;
        }
        final int index = indexFromPosition(x, y, z);
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

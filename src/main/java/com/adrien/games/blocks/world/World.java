package com.adrien.games.blocks.world;

import com.adrien.games.bagl.core.math.Vector3;
import com.adrien.games.blocks.rendering.chunk.ChunkMeshPool;
import com.adrien.games.blocks.utils.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.stream.Stream;

public class World {

    private static final Logger LOG = LogManager.getLogger(World.class);

    public static final int WORLD_MAX_WIDTH = 4;
    public static final int WORLD_MAX_DEPTH = 4;
    public static final int WORLD_MAX_HEIGHT = 1;
    public static final int CHUNK_WIDTH = 16;
    public static final int CHUNK_HEIGHT = 16;
    public static final int CHUNK_DEPTH = 16;
    public static final int BLOCK_PER_CHUNK = CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_DEPTH;

    private final ChunkMeshPool chunkMeshPool;
    private Chunk[] chunks;
    private Chunk[] buffer;
    private final Point marker;
    private final Loader loader;

    public World() {
        this.chunkMeshPool = new ChunkMeshPool(World.WORLD_MAX_WIDTH * World.WORLD_MAX_DEPTH * 2);
        this.chunks = new Chunk[WORLD_MAX_WIDTH * WORLD_MAX_DEPTH];
        this.buffer = new Chunk[WORLD_MAX_WIDTH * WORLD_MAX_DEPTH];
        this.marker = new Point();
        this.loader = new Loader();
        this.loader.start();

        this.refreshWorld(0, 0);
    }

    public void update(final Vector3 position) {
        final int x = Math.round(position.getX()) / CHUNK_WIDTH;
        final int z = Math.round(position.getZ()) / CHUNK_DEPTH;
        if (this.marker.getX() != x || this.marker.getY() != z) {
            final int stepX = x - this.marker.getX();
            final int stepZ = z - this.marker.getY();
            this.marker.setXY(x, z);
            this.refreshWorld(stepX, stepZ);
        }
    }

    private void refreshWorld(final int stepX, final int stepZ) {
        LOG.trace("Loading world around {}", this.marker);
        for (int x = 0; x < WORLD_MAX_WIDTH; x++) {
            for (int z = 0; z < WORLD_MAX_DEPTH; z++) {
                final int oldX = x + stepX;
                final int oldZ = z + stepZ;

                final int newX = x - stepX;
                final int newZ = z - stepZ;

                final int index = this.indexFromPosition(x, z);
                final int oldIndex = this.indexFromPosition(oldX, oldZ);

                if (!this.isInBound(newX, newZ) && Objects.nonNull(this.chunks[index])) {
                    this.chunkMeshPool.releaseChunkMesh(this.chunks[index].getMesh());
                }

                if (this.isInBound(oldX, oldZ) && Objects.nonNull(this.chunks[oldIndex])) {
                    this.buffer[index] = this.chunks[oldIndex];
                } else {
                    final Chunk newChunk = new Chunk(x + this.marker.getX() - WORLD_MAX_WIDTH / 2, 0,
                            z + this.marker.getY() - WORLD_MAX_DEPTH / 2, this.chunkMeshPool);
                    this.buffer[index] = newChunk;
                    this.loader.load(newChunk);
                }
            }
        }
        this.swapBuffers();
    }

    private boolean isInBound(final int x, final int z) {
        return x >= 0 && x < WORLD_MAX_WIDTH && z >= 0 && z < WORLD_MAX_DEPTH;
    }

    private void swapBuffers() {
        final Chunk[] tmp = this.buffer;
        this.buffer = this.chunks;
        this.chunks = tmp;
    }

    private int indexFromPosition(int x, int z) {
        return x * WORLD_MAX_DEPTH + z;
    }

    public void destroy() {
        this.loader.end();
        this.chunkMeshPool.destroy();
    }

    public Stream<Chunk> getChunks() {
        return Stream.of(this.chunks).filter(Objects::nonNull).filter(Chunk::isLoaded);
    }

}

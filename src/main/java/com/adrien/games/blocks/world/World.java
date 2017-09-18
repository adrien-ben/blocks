package com.adrien.games.blocks.world;

import com.adrien.games.bagl.core.math.Vector3;
import com.adrien.games.blocks.rendering.chunk.ChunkMeshPool;
import com.adrien.games.blocks.utils.Point;
import com.adrien.games.blocks.world.block.Block;
import com.adrien.games.blocks.world.block.BlockType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class World {

    private static final Logger LOG = LogManager.getLogger(World.class);

    public static final int WORLD_MAX_WIDTH = 10;
    public static final int WORLD_MAX_DEPTH = 10;
    public static final int WORLD_MAX_HEIGHT = 1;
    public static final int CHUNK_WIDTH = 16;
    public static final int CHUNK_HEIGHT = 128;
    public static final int CHUNK_DEPTH = 16;
    public static final int BLOCK_PER_CHUNK = CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_DEPTH;
    public static final int WATER_LEVEL = WORLD_MAX_HEIGHT * CHUNK_HEIGHT / 2;

    private final ChunkMeshPool chunkMeshPool;
    private Chunk[] chunks;
    private Chunk[] buffer;
    private final Point marker;
    private final Loader loader;

    private int left;
    private int right;
    private int close;
    private int far;

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

    public boolean removeBlock(final Vector3 position) {
        return this.addBlock(position, BlockType.AIR);
    }

    public boolean addBlock(final Vector3 position, final BlockType type) {
        final int x = (int) Math.floor(position.getX());
        final int y = (int) Math.floor(position.getY());
        final int z = (int) Math.floor(position.getZ());

        final int chunkX = (int) Math.floor((float) x / CHUNK_WIDTH);
        final int chunkY = (int) Math.floor((float) y / CHUNK_HEIGHT);
        final int chunkZ = (int) Math.floor((float) z / CHUNK_DEPTH);
        if (chunkX < this.left || chunkX >= this.right || chunkY != 0 || chunkZ < this.close || chunkZ >= this.far) {
            return false;
        }

        final Chunk chunk = this.chunks[this.indexFromPosition(chunkX - this.left, chunkZ - this.close)];
        if (chunk.addBlock(x - chunkX * CHUNK_WIDTH, y - chunkY * CHUNK_HEIGHT, z - chunkZ * CHUNK_DEPTH, type)) {
            this.loader.load(chunk);
            return true;
        }
        return false;
    }

    public Optional<Block> getBlock(final Vector3 position, final Vector3 direction, final float maxDistance, final Predicate<Block> predicate) {
        float distance = 0;
        final float step = 0.25f;
        direction.normalise().scale(step);
        Optional<Block> block = Optional.empty();
        while (distance <= maxDistance && !(block = this.getBlockIfMatches(position, predicate)).isPresent()) {
            position.add(direction);
            distance += step;
        }
        return block;
    }

    private Optional<Block> getBlockIfMatches(final Vector3 position, final Predicate<Block> predicate) {
        final int x = (int) Math.floor(position.getX());
        final int y = (int) Math.floor(position.getY());
        final int z = (int) Math.floor(position.getZ());

        final int chunkX = (int) Math.floor((float) x / CHUNK_WIDTH);
        final int chunkY = (int) Math.floor((float) y / CHUNK_HEIGHT);
        final int chunkZ = (int) Math.floor((float) z / CHUNK_DEPTH);
        if (chunkX < this.left || chunkX >= this.right || chunkY != 0 || chunkZ < this.close || chunkZ >= this.far) {
            return Optional.empty();
        }
        final Chunk chunk = this.chunks[this.indexFromPosition(chunkX - left, chunkZ - close)];
        final Block block = chunk.getBlock(x - chunkX * CHUNK_WIDTH, y - chunkY * CHUNK_HEIGHT, z - chunkZ * CHUNK_DEPTH);

        if (Objects.isNull(block) || (Objects.nonNull(predicate) && !predicate.test(block))) {
            return Optional.empty();
        }
        return Optional.of(block);
    }

    private void refreshWorld(final int stepX, final int stepZ) {
        LOG.trace("Loading world around {}", this.marker);

        this.left = this.marker.getX() - WORLD_MAX_WIDTH / 2;
        this.right = this.left + WORLD_MAX_WIDTH;
        this.close = this.marker.getY() - WORLD_MAX_DEPTH / 2;
        this.far = this.close + WORLD_MAX_DEPTH;

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
                    final Chunk newChunk = new Chunk(this.left + x, 0, this.close + z, this.chunkMeshPool);
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

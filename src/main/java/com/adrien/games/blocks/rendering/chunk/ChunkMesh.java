package com.adrien.games.blocks.rendering.chunk;

import com.adrien.games.blocks.utils.Timer;
import com.adrien.games.blocks.world.Chunk;
import com.adrien.games.blocks.world.World;
import com.adrien.games.blocks.world.block.Block;
import com.adrien.games.blocks.world.block.BlockType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class ChunkMesh {

    private static final Logger LOG = LogManager.getLogger(ChunkMesh.class);

    private static final int MAX_FACE_PER_BLOCK = 6;
    public static final int VERTICES_PER_FACE = 4;
    private static final int ELEMENT_PER_POSITION = 3;
    private static final int ELEMENT_PER_COORDINATES = 2;
    private static final int BYTE_SIZE_IN_BYTES = Byte.SIZE / 8;
    private static final int SHORT_SIZE_IN_BYTES = Short.SIZE / 8;
    public static final int INDICES_PER_FACE = 6;
    private static final int MAX_VISIBLE_BLOCK_PER_CHUNK = World.BLOCK_PER_CHUNK / 2 + 1;
    public static final int MAX_FACE_COUNT = MAX_VISIBLE_BLOCK_PER_CHUNK * MAX_FACE_PER_BLOCK;
    private static final int MAX_VERTEX_COUNT = MAX_FACE_COUNT * VERTICES_PER_FACE;
    public static final int MAX_INDEX_COUNT = MAX_FACE_COUNT * INDICES_PER_FACE;

    private final ShortBuffer positions;
    private final ByteBuffer coordinates;

    private int vao;
    private int pbo;
    private int cbo;
    private int faceCount;

    private boolean ready;
    private boolean uploaded;

    public ChunkMesh() {
        LOG.trace("Initializing OpenGL buffers for chunk mesh");

        this.positions = MemoryUtil.memAllocShort(MAX_VERTEX_COUNT * ELEMENT_PER_POSITION);
        this.coordinates = MemoryUtil.memAlloc(MAX_VERTEX_COUNT * ELEMENT_PER_COORDINATES);

        this.vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(this.vao);

        this.pbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.pbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.positions, GL15.GL_DYNAMIC_DRAW);

        GL20.glEnableVertexAttribArray(0);
        GL30.glVertexAttribIPointer(0, ELEMENT_PER_POSITION, GL11.GL_SHORT, ELEMENT_PER_POSITION * SHORT_SIZE_IN_BYTES, 0);

        this.cbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.cbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.coordinates, GL15.GL_DYNAMIC_DRAW);

        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(1, ELEMENT_PER_COORDINATES, GL11.GL_BYTE, true, ELEMENT_PER_COORDINATES * BYTE_SIZE_IN_BYTES, 0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        this.faceCount = 0;
        this.ready = false;
        this.uploaded = false;
    }

    public void reset() {
        this.faceCount = 0;
        this.ready = false;
        this.uploaded = false;
    }

    public void update(final Chunk chunk) {
        LOG.trace("Updating mesh for chunk {}", chunk);
        this.faceCount = 0;
        this.positions.limit(MAX_VERTEX_COUNT * ELEMENT_PER_POSITION);
        this.coordinates.limit(MAX_VERTEX_COUNT * ELEMENT_PER_COORDINATES);
        chunk.getBlocks()
                .filter(Block::isNotAir)
                .forEach(block -> this.computeBlockFaces(chunk, block));
        this.uploaded = false;
        this.ready = true;
    }

    public void uploadMesh() {
        if (!this.ready) {
            throw new IllegalStateException("A chunk mesh cannot be uploaded if it has not been updated at least once");
        }

        if (this.uploaded) {
            return;
        }

        final Timer timer = new Timer();

        LOG.trace("Uploading mesh data to GPU");
        this.positions.limit(this.faceCount * VERTICES_PER_FACE * ELEMENT_PER_POSITION);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.pbo);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, this.positions);

        this.coordinates.limit(this.faceCount * VERTICES_PER_FACE * ELEMENT_PER_COORDINATES);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.cbo);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, this.coordinates);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        LOG.debug("Time to upload chuck : {}", timer.top());
        this.uploaded = true;
    }

    private void computeBlockFaces(final Chunk chunk, final Block block) {
        final int indexX = block.getChunkX();
        final int indexY = block.getChunkY();
        final int indexZ = block.getChunkZ();
        final int worldX = block.getWorldX();
        final int worldY = block.getWorldY();
        final int worldZ = block.getWorldZ();
        final BlockType type = block.getType();
        if (indexX == 0 || chunk.getBlock(indexX - 1, indexY, indexZ).isAir()) {
            this.addLeftFace(worldX, worldY, worldZ, type);
            this.faceCount++;
        }
        if (indexX == World.CHUNK_WIDTH - 1 || chunk.getBlock(indexX + 1, indexY, indexZ).isAir()) {
            this.addRightFace(worldX, worldY, worldZ, type);
            this.faceCount++;
        }
        if (indexY == 0 || chunk.getBlock(indexX, indexY - 1, indexZ).isAir()) {
            this.addBottomFace(worldX, worldY, worldZ, type);
            this.faceCount++;
        }
        if (indexY == World.CHUNK_HEIGHT - 1 || chunk.getBlock(indexX, indexY + 1, indexZ).isAir()) {
            this.addTopFace(worldX, worldY, worldZ, type);
            this.faceCount++;
        }
        if (indexZ == 0 || chunk.getBlock(indexX, indexY, indexZ - 1).isAir()) {
            this.addBackFace(worldX, worldY, worldZ, type);
            this.faceCount++;
        }
        if (indexZ == World.CHUNK_DEPTH - 1 || chunk.getBlock(indexX, indexY, indexZ + 1).isAir()) {
            this.addFrontFace(worldX, worldY, worldZ, type);
            this.faceCount++;
        }
    }

    private void addLeftFace(final int x, final int y, final int z, final BlockType type) {
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE, x, y, z, type.getLeftUV(), type.getBottomUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 1, x, y, z + 1, type.getRightUV(), type.getBottomUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 2, x, y + 1, z, type.getLeftUV(), type.getTopUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 3, x, y + 1, z + 1, type.getRightUV(), type.getTopUV());
    }

    private void addRightFace(final int x, final int y, final int z, final BlockType type) {
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE, x + 1, y, z + 1, type.getLeftUV(), type.getBottomUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 1, x + 1, y, z, type.getRightUV(), type.getBottomUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 2, x + 1, y + 1, z + 1, type.getLeftUV(), type.getTopUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 3, x + 1, y + 1, z, type.getRightUV(), type.getTopUV());
    }

    private void addBottomFace(final int x, final int y, final int z, final BlockType type) {
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE, x, y, z, type.getLeftUV(), type.getBottomUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 1, x + 1, y, z, type.getRightUV(), type.getBottomUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 2, x, y, z + 1, type.getLeftUV(), type.getTopUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 3, x + 1, y, z + 1, type.getRightUV(), type.getTopUV());
    }

    private void addTopFace(final int x, final int y, final int z, final BlockType type) {
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE, x, y + 1, z + 1, type.getLeftUV(), type.getBottomUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 1, x + 1, y + 1, z + 1, type.getRightUV(), type.getBottomUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 2, x, y + 1, z, type.getLeftUV(), type.getTopUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 3, x + 1, y + 1, z, type.getRightUV(), type.getTopUV());
    }

    private void addBackFace(final int x, final int y, final int z, final BlockType type) {
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE, x + 1, y, z, type.getLeftUV(), type.getBottomUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 1, x, y, z, type.getRightUV(), type.getBottomUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 2, x + 1, y + 1, z, type.getLeftUV(), type.getTopUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 3, x, y + 1, z, type.getRightUV(), type.getTopUV());
    }

    private void addFrontFace(final int x, final int y, final int z, final BlockType type) {
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE, x, y, z + 1, type.getLeftUV(), type.getBottomUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 1, x + 1, y, z + 1, type.getRightUV(), type.getBottomUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 2, x, y + 1, z + 1, type.getLeftUV(), type.getTopUV());
        this.setVertexValues(this.faceCount * VERTICES_PER_FACE + 3, x + 1, y + 1, z + 1, type.getRightUV(), type.getTopUV());
    }

    private void setVertexValues(final int index, final int x, final int y, final int z, final byte u, final byte v) {
        this.positions.put(index * ELEMENT_PER_POSITION, (short) x);
        this.positions.put(index * ELEMENT_PER_POSITION + 1, (short) y);
        this.positions.put(index * ELEMENT_PER_POSITION + 2, (short) z);
        this.coordinates.put(index * ELEMENT_PER_COORDINATES, u);
        this.coordinates.put(index * ELEMENT_PER_COORDINATES + 1, v);
    }

    public void destroy() {
        LOG.trace("Destroying chunk mesh");
        GL30.glBindVertexArray(0);
        GL15.glDeleteBuffers(this.pbo);
        GL15.glDeleteBuffers(this.cbo);
        GL30.glDeleteVertexArrays(this.vao);
        MemoryUtil.memFree(this.positions);
        MemoryUtil.memFree(this.coordinates);
    }

    public void bind() {
        GL30.glBindVertexArray(this.vao);
    }

    public void unbind() {
        GL30.glBindVertexArray(0);
    }

    public int getFaceCount() {
        return this.faceCount;
    }

}


package com.adrien.games.blocks.rendering.chunk;

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

public class ChunkMesh {

    private static final Logger LOG = LogManager.getLogger(ChunkMesh.class);

    private static final int MAX_FACE_PER_BLOCK = 6;
    public static final int VERTICES_PER_FACE = 4;
    private static final int ELEMENT_PER_VERTEX = 5;
    private static final int FLOAT_SIZE_IN_BYTES = Float.SIZE / 8;
    public static final int INDICES_PER_FACE = 6;
    private static final int MAX_VERTEX_COUNT = World.BLOCK_PER_CHUNK * MAX_FACE_PER_BLOCK * VERTICES_PER_FACE;
    public static final int MAX_INDEX_COUNT = World.BLOCK_PER_CHUNK * MAX_FACE_PER_BLOCK * INDICES_PER_FACE;

    private float[] vertices;
    private int vao;
    private int vbo;
    private int faceCount;

    private boolean ready;
    private boolean uploaded;

    public ChunkMesh() {
        LOG.trace("Initializing OpenGL buffers for chunk mesh");
        this.vertices = new float[MAX_VERTEX_COUNT * ELEMENT_PER_VERTEX];

        this.vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(this.vao);

        this.vbo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.vertices, GL15.GL_DYNAMIC_DRAW);

        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, ELEMENT_PER_VERTEX * FLOAT_SIZE_IN_BYTES, 0);

        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, ELEMENT_PER_VERTEX * FLOAT_SIZE_IN_BYTES, 3 * FLOAT_SIZE_IN_BYTES);

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
        this.uploaded = false;
        this.faceCount = 0;
        chunk.getBlocks()
                .filter(block -> !BlockType.AIR.equals(block.getType()))
                .forEach(block -> this.computeBlockFaces(chunk, block));
        this.ready = true;
    }

    public void uploadMesh() {
        if (!this.ready) {
            throw new IllegalStateException("A chunk mesh cannot be uploaded if it has not been updated at least once");
        }

        if (this.uploaded) {
            return;
        }

        LOG.trace("Uploading mesh data to GPU");

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, this.vertices);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
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
        if (indexX == 0 || BlockType.AIR.equals(chunk.getBlock(indexX - 1, indexY, indexZ).getType())) {
            this.addLeftFace(worldX, worldY, worldZ, type);
            this.faceCount++;
        }
        if (indexX == World.CHUNK_WIDTH - 1 || BlockType.AIR.equals(chunk.getBlock(indexX + 1, indexY, indexZ).getType())) {
            this.addRightFace(worldX, worldY, worldZ, type);
            this.faceCount++;
        }
        if (indexY == 0 || BlockType.AIR.equals(chunk.getBlock(indexX, indexY - 1, indexZ).getType())) {
            this.addBottomFace(worldX, worldY, worldZ, type);
            this.faceCount++;
        }
        if (indexY == World.CHUNK_HEIGHT - 1 || BlockType.AIR.equals(chunk.getBlock(indexX, indexY + 1, indexZ).getType())) {
            this.addTopFace(worldX, worldY, worldZ, type);
            this.faceCount++;
        }
        if (indexZ == 0 || BlockType.AIR.equals(chunk.getBlock(indexX, indexY, indexZ - 1).getType())) {
            this.addBackFace(worldX, worldY, worldZ, type);
            this.faceCount++;
        }
        if (indexZ == World.CHUNK_DEPTH - 1 || BlockType.AIR.equals(chunk.getBlock(indexX, indexY, indexZ + 1).getType())) {
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

    private void setVertexValues(final int index, final float x, final float y, final float z, final float u, final float v) {
        this.vertices[index * ELEMENT_PER_VERTEX] = x;
        this.vertices[index * ELEMENT_PER_VERTEX + 1] = y;
        this.vertices[index * ELEMENT_PER_VERTEX + 2] = z;
        this.vertices[index * ELEMENT_PER_VERTEX + 3] = u;
        this.vertices[index * ELEMENT_PER_VERTEX + 4] = v;
    }

    public void destroy() {
        LOG.trace("Destroying chunk mesh");
        GL30.glBindVertexArray(0);
        GL15.glDeleteBuffers(this.vbo);
        GL30.glDeleteVertexArrays(this.vao);
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


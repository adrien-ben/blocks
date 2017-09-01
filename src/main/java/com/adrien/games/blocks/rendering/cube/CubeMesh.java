package com.adrien.games.blocks.rendering.cube;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class CubeMesh {

    public static final int INDEX_COUNT = 36;
    private static final byte POS = 1;
    private static final byte NEG = 0;

    private final int vao;
    private final int vbo;
    private final int ibo;

    public CubeMesh() {
        this.vao = GL30.glGenVertexArrays();
        this.vbo = GL15.glGenBuffers();
        this.initVertices();

        this.ibo = GL15.glGenBuffers();
        this.initIndices();
    }

    private void initVertices() {
        final byte[] positions = new byte[]{
                NEG, NEG, POS,
                POS, NEG, POS,
                NEG, POS, POS,
                POS, POS, POS,
                POS, NEG, NEG,
                NEG, NEG, NEG,
                POS, POS, NEG,
                NEG, POS, NEG
        };
        GL30.glBindVertexArray(this.vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, this.bufferFromArray(positions), GL15.GL_STATIC_DRAW);
        GL20.glEnableVertexAttribArray(0);
        GL30.glVertexAttribIPointer(0, 3, GL11.GL_BYTE, 3, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    private void initIndices() {
        final byte[] indices = new byte[]{
                0, 1, 2, 2, 1, 3,
                1, 4, 3, 3, 4, 6,
                4, 5, 6, 6, 5, 7,
                5, 0, 7, 7, 0, 2,
                2, 3, 7, 7, 3, 6,
                5, 4, 0, 0, 4, 1
        };
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.ibo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, this.bufferFromArray(indices), GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private ByteBuffer bufferFromArray(final byte[] array) {
        final ByteBuffer buffer = MemoryUtil.memAlloc(array.length);
        buffer.put(array);
        buffer.flip();
        return buffer;
    }

    public void destroy() {
        GL15.glDeleteBuffers(this.ibo);
        GL15.glDeleteBuffers(this.vbo);
        GL30.glDeleteVertexArrays(this.vao);
    }

    public void bind() {
        GL30.glBindVertexArray(this.vao);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.ibo);
    }

    public void unbind() {
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

}

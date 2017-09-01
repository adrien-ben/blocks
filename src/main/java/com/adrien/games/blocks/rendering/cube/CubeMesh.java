package com.adrien.games.blocks.rendering.cube;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

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
        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final ByteBuffer vertices = stack.bytes(
                    NEG, NEG, POS,
                    POS, NEG, POS,
                    NEG, POS, POS,
                    POS, POS, POS,
                    POS, NEG, NEG,
                    NEG, NEG, NEG,
                    POS, POS, NEG,
                    NEG, POS, NEG);
            GL30.glBindVertexArray(this.vao);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
            GL20.glEnableVertexAttribArray(0);
            GL30.glVertexAttribIPointer(0, 3, GL11.GL_BYTE, 3, 0);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            GL30.glBindVertexArray(0);
        }
    }

    private void initIndices() {
        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final ByteBuffer indices = stack.bytes(
                    (byte) 0, (byte) 1, (byte) 2, (byte) 2, (byte) 1, (byte) 3,
                    (byte) 1, (byte) 4, (byte) 3, (byte) 3, (byte) 4, (byte) 6,
                    (byte) 4, (byte) 5, (byte) 6, (byte) 6, (byte) 5, (byte) 7,
                    (byte) 5, (byte) 0, (byte) 7, (byte) 7, (byte) 0, (byte) 2,
                    (byte) 2, (byte) 3, (byte) 7, (byte) 7, (byte) 3, (byte) 6,
                    (byte) 5, (byte) 4, (byte) 0, (byte) 0, (byte) 4, (byte) 1);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.ibo);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
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

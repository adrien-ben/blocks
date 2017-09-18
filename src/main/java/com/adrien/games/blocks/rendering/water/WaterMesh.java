package com.adrien.games.blocks.rendering.water;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

public class WaterMesh {

    private static final int BYTE_SIZE = Byte.SIZE / 8;
    private static final int ELEMENT_COUNT = 2;
    private static final int STRIDE = ELEMENT_COUNT * BYTE_SIZE;

    private static final byte MIN = Byte.MIN_VALUE;
    private static final byte MAX = Byte.MAX_VALUE;

    private final int vao;
    private final int vbo;

    public WaterMesh() {
        this.vao = GL30.glGenVertexArrays();
        this.vbo = GL15.glGenBuffers();

        GL30.glBindVertexArray(this.vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);

        try (final MemoryStack stack = MemoryStack.stackPush()) {
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, stack.bytes(MIN, MAX, MAX, MAX, MIN, MIN, MAX, MIN), GL15.GL_STATIC_DRAW);
        }

        GL20.glEnableVertexAttribArray(0);
        GL30.glVertexAttribIPointer(0, ELEMENT_COUNT, GL11.GL_BYTE, STRIDE, 0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    public void bind() {
        GL30.glBindVertexArray(this.vao);
    }

    public void unbind() {
        GL30.glBindVertexArray(0);
    }

    public void destroy() {
        GL15.glDeleteBuffers(this.vbo);
        GL30.glDeleteVertexArrays(this.vao);
    }

}

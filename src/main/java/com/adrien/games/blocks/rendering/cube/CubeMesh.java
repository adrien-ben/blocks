package com.adrien.games.blocks.rendering.cube;

import com.adrien.games.bagl.rendering.BufferUsage;
import com.adrien.games.bagl.rendering.DataType;
import com.adrien.games.bagl.rendering.vertex.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

public class CubeMesh {

    public static final int INDEX_COUNT = 36;
    private static final byte POS = 1;
    private static final byte NEG = 0;

    private final VertexBuffer vBuffer;
    private final VertexArray vArray;
    private final IndexBuffer iBuffer;

    public CubeMesh() {
        this.vBuffer = this.initVertices();
        this.vArray = new VertexArray();
        this.vArray.bind();
        this.vArray.attachVertexBuffer(this.vBuffer);
        this.vArray.unbind();

        this.iBuffer = this.initIndices();
    }

    private VertexBuffer initVertices() {
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
            return new VertexBuffer(vertices, VertexBufferParams.builder()
                    .dataType(DataType.BYTE)
                    .element(new VertexElement(0, 3))
                    .build());
        }
    }

    private IndexBuffer initIndices() {
        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final ByteBuffer indices = stack.bytes(
                    (byte) 0, (byte) 1, (byte) 2, (byte) 2, (byte) 1, (byte) 3,
                    (byte) 1, (byte) 4, (byte) 3, (byte) 3, (byte) 4, (byte) 6,
                    (byte) 4, (byte) 5, (byte) 6, (byte) 6, (byte) 5, (byte) 7,
                    (byte) 5, (byte) 0, (byte) 7, (byte) 7, (byte) 0, (byte) 2,
                    (byte) 2, (byte) 3, (byte) 7, (byte) 7, (byte) 3, (byte) 6,
                    (byte) 5, (byte) 4, (byte) 0, (byte) 0, (byte) 4, (byte) 1);
            return new IndexBuffer(indices, BufferUsage.STATIC_DRAW);
        }
    }

    public void destroy() {
        this.iBuffer.destroy();
        this.vBuffer.destroy();
        this.vArray.destroy();
    }

    public void bind() {
        this.vArray.bind();
        this.iBuffer.bind();
    }

    public void unbind() {
        this.iBuffer.unbind();
        this.vArray.unbind();
    }
}

package com.adrien.games.blocks.rendering.water;

import com.adrienben.games.bagl.opengl.DataType;
import com.adrienben.games.bagl.opengl.vertex.VertexArray;
import com.adrienben.games.bagl.opengl.vertex.VertexBuffer;
import com.adrienben.games.bagl.opengl.vertex.VertexBufferParams;
import com.adrienben.games.bagl.opengl.vertex.VertexElement;
import org.lwjgl.system.MemoryStack;

public class WaterMesh {

    private static final int ELEMENT_COUNT = 2;
    private static final byte MIN = Byte.MIN_VALUE;
    private static final byte MAX = Byte.MAX_VALUE;

    private final VertexBuffer vBuffer;
    private final VertexArray vArray;

    public WaterMesh() {
        try (final MemoryStack stack = MemoryStack.stackPush()) {
            this.vBuffer = new VertexBuffer(stack.bytes(MIN, MAX, MAX, MAX, MIN, MIN, MAX, MIN), VertexBufferParams.builder()
                    .dataType(DataType.BYTE)
                    .element(new VertexElement(0, ELEMENT_COUNT))
                    .build());
        }
        this.vArray = new VertexArray();
        this.vArray.bind();
        this.vArray.attachVertexBuffer(this.vBuffer);
        this.vArray.unbind();
    }

    public void bind() {
        this.vArray.bind();
    }

    public void unbind() {
        this.vArray.unbind();
    }

    public void destroy() {
        this.vBuffer.destroy();
        this.vArray.destroy();
    }

}

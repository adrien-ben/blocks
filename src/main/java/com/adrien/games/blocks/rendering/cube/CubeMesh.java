package com.adrien.games.blocks.rendering.cube;

import com.adrien.games.bagl.core.math.Vector3;
import com.adrien.games.bagl.rendering.BufferUsage;
import com.adrien.games.bagl.rendering.IndexBuffer;
import com.adrien.games.bagl.rendering.VertexBuffer;
import com.adrien.games.bagl.rendering.vertex.VertexPosition;

public class CubeMesh {

    public static final int INDEX_COUNT = 36;
    private static final float POS = 1f;
    private static final float NEG = 0f;

    private final VertexBuffer vertexBuffer;
    private final IndexBuffer indexBuffer;

    public CubeMesh() {
        final VertexPosition[] vertices = new VertexPosition[]{
                new VertexPosition(new Vector3(NEG, NEG, POS)),
                new VertexPosition(new Vector3(POS, NEG, POS)),
                new VertexPosition(new Vector3(NEG, POS, POS)),
                new VertexPosition(new Vector3(POS, POS, POS)),
                new VertexPosition(new Vector3(POS, NEG, NEG)),
                new VertexPosition(new Vector3(NEG, NEG, NEG)),
                new VertexPosition(new Vector3(POS, POS, NEG)),
                new VertexPosition(new Vector3(NEG, POS, NEG))
        };
        this.vertexBuffer = new VertexBuffer(VertexPosition.DESCRIPTION, BufferUsage.STATIC_DRAW, vertices);

        final int[] indices = new int[]{
                0, 1, 2, 2, 1, 3,
                1, 4, 3, 3, 4, 6,
                4, 5, 6, 6, 5, 7,
                5, 0, 7, 7, 0, 2,
                2, 3, 7, 7, 3, 6,
                5, 4, 0, 0, 4, 1
        };
        this.indexBuffer = new IndexBuffer(BufferUsage.STATIC_DRAW, indices);
    }

    public void destroy() {
        this.indexBuffer.destroy();
        this.vertexBuffer.destroy();
    }

    public VertexBuffer getVertexBuffer() {
        return this.vertexBuffer;
    }

    public IndexBuffer getIndexBuffer() {
        return this.indexBuffer;
    }

}

package com.adrien.games.blocks.rendering.cube;

import com.adrien.games.bagl.core.math.Vector2;
import com.adrien.games.bagl.core.math.Vector3;
import com.adrien.games.bagl.rendering.BufferUsage;
import com.adrien.games.bagl.rendering.IndexBuffer;
import com.adrien.games.bagl.rendering.VertexBuffer;
import com.adrien.games.bagl.rendering.vertex.VertexPositionTexture;

public class CubeMesh {

    public static final int INDEX_COUNT = 36;
    private static final float UV_0 = 1f / 16f;
    private static final float UV_1 = 1f - UV_0;
    private static final float POS = 0.5f;
    private static final float NEG = -POS;

    private final VertexBuffer vertexBuffer;
    private final IndexBuffer indexBuffer;

    public CubeMesh() {
        final VertexPositionTexture[] vertices = new VertexPositionTexture[]{
                new VertexPositionTexture(new Vector3(NEG, NEG, POS), new Vector2(UV_0, UV_0)),
                new VertexPositionTexture(new Vector3(POS, NEG, POS), new Vector2(UV_1, UV_0)),
                new VertexPositionTexture(new Vector3(NEG, POS, POS), new Vector2(UV_0, UV_1)),
                new VertexPositionTexture(new Vector3(POS, POS, POS), new Vector2(UV_1, UV_1)),

                new VertexPositionTexture(new Vector3(POS, NEG, POS), new Vector2(UV_0, UV_0)),
                new VertexPositionTexture(new Vector3(POS, NEG, NEG), new Vector2(UV_1, UV_0)),
                new VertexPositionTexture(new Vector3(POS, POS, POS), new Vector2(UV_0, UV_1)),
                new VertexPositionTexture(new Vector3(POS, POS, NEG), new Vector2(UV_1, UV_1)),

                new VertexPositionTexture(new Vector3(POS, NEG, NEG), new Vector2(UV_0, UV_0)),
                new VertexPositionTexture(new Vector3(NEG, NEG, NEG), new Vector2(UV_1, UV_0)),
                new VertexPositionTexture(new Vector3(POS, POS, NEG), new Vector2(UV_0, UV_1)),
                new VertexPositionTexture(new Vector3(NEG, POS, NEG), new Vector2(UV_1, UV_1)),

                new VertexPositionTexture(new Vector3(NEG, NEG, NEG), new Vector2(UV_0, UV_0)),
                new VertexPositionTexture(new Vector3(NEG, NEG, POS), new Vector2(UV_1, UV_0)),
                new VertexPositionTexture(new Vector3(NEG, POS, NEG), new Vector2(UV_0, UV_1)),
                new VertexPositionTexture(new Vector3(NEG, POS, POS), new Vector2(UV_1, UV_1)),

                new VertexPositionTexture(new Vector3(NEG, POS, POS), new Vector2(UV_0, UV_0)),
                new VertexPositionTexture(new Vector3(POS, POS, POS), new Vector2(UV_1, UV_0)),
                new VertexPositionTexture(new Vector3(NEG, POS, NEG), new Vector2(UV_0, UV_1)),
                new VertexPositionTexture(new Vector3(POS, POS, NEG), new Vector2(UV_1, UV_1)),

                new VertexPositionTexture(new Vector3(NEG, NEG, NEG), new Vector2(UV_0, UV_0)),
                new VertexPositionTexture(new Vector3(POS, NEG, NEG), new Vector2(UV_1, UV_0)),
                new VertexPositionTexture(new Vector3(NEG, NEG, POS), new Vector2(UV_0, UV_1)),
                new VertexPositionTexture(new Vector3(POS, NEG, POS), new Vector2(UV_1, UV_1))
        };
        this.vertexBuffer = new VertexBuffer(VertexPositionTexture.DESCRIPTION, BufferUsage.STATIC_DRAW, vertices);

        final int[] indices = new int[]{
                0, 1, 2, 2, 1, 3,
                4, 5, 6, 6, 5, 7,
                8, 9, 10, 10, 9, 11,
                12, 13, 14, 14, 13, 15,
                16, 17, 18, 18, 17, 19,
                20, 21, 22, 22, 21, 23
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

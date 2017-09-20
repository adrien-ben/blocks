package com.adrien.games.blocks.rendering.chunk;

import com.adrien.games.bagl.core.Camera;
import com.adrien.games.bagl.rendering.BufferUsage;
import com.adrien.games.bagl.rendering.IndexBuffer;
import com.adrien.games.bagl.rendering.Shader;
import com.adrien.games.bagl.rendering.light.DirectionalLight;
import com.adrien.games.bagl.rendering.light.Light;
import com.adrien.games.bagl.rendering.texture.Filter;
import com.adrien.games.bagl.rendering.texture.Texture;
import com.adrien.games.bagl.rendering.texture.TextureParameters;
import com.adrien.games.bagl.utils.FileUtils;
import org.lwjgl.opengl.GL11;

public class ChunkRenderer {

    private final Shader shader;
    private final Texture blockAtlas;
    private final IndexBuffer indexBuffer;

    public ChunkRenderer() {
        this.shader = new Shader().addVertexShader("chunk.vert").addFragmentShader("chunk.frag").compile();
        this.blockAtlas = new Texture(FileUtils.getResourceAbsolutePath("/textures/blocks.png"),
                new TextureParameters().minFilter(Filter.NEAREST).magFilter(Filter.NEAREST));
        this.indexBuffer = new IndexBuffer(BufferUsage.STATIC_DRAW, ChunkMesh.MAX_INDEX_COUNT);
        this.generateIndices();
    }

    private void generateIndices() {
        final int[] indices = new int[ChunkMesh.MAX_INDEX_COUNT];
        for (int i = 0; i < ChunkMesh.MAX_FACE_COUNT; i++) {
            indices[i * ChunkMesh.INDICES_PER_FACE] = i * ChunkMesh.VERTICES_PER_FACE;
            indices[i * ChunkMesh.INDICES_PER_FACE + 1] = i * ChunkMesh.VERTICES_PER_FACE + 1;
            indices[i * ChunkMesh.INDICES_PER_FACE + 2] = i * ChunkMesh.VERTICES_PER_FACE + 2;
            indices[i * ChunkMesh.INDICES_PER_FACE + 3] = i * ChunkMesh.VERTICES_PER_FACE + 2;
            indices[i * ChunkMesh.INDICES_PER_FACE + 4] = i * ChunkMesh.VERTICES_PER_FACE + 1;
            indices[i * ChunkMesh.INDICES_PER_FACE + 5] = i * ChunkMesh.VERTICES_PER_FACE + 3;
        }
        this.indexBuffer.setData(indices);
    }

    public void renderChunk(final ChunkMesh mesh, final Camera camera, final Light ambientLight, final DirectionalLight sunLight) {
        mesh.uploadMesh();
        mesh.bind();
        this.indexBuffer.bind();
        this.blockAtlas.bind();
        this.shader.bind();
        this.setUpShader(camera, ambientLight, sunLight);
        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getFaceCount() * ChunkMesh.INDICES_PER_FACE, GL11.GL_UNSIGNED_INT, 0);
        Shader.unbind();
        Texture.unbind();
        IndexBuffer.unbind();
        mesh.unbind();
    }

    private void setUpShader(final Camera camera, final Light ambientLight, final DirectionalLight sunLight) {
        this.shader.setUniform("uVP", camera.getViewProj());
        this.shader.setUniform("uAmbient.color", ambientLight.getColor());
        this.shader.setUniform("uAmbient.intensity", ambientLight.getIntensity());
        this.shader.setUniform("uSunLight.base.color", sunLight.getColor());
        this.shader.setUniform("uSunLight.base.intensity", sunLight.getIntensity());
        this.shader.setUniform("uSunLight.direction", sunLight.getDirection());
    }

    public void destroy() {
        this.shader.destroy();
        this.blockAtlas.destroy();
        this.indexBuffer.destroy();
    }

}

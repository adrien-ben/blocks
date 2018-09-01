package com.adrien.games.blocks.rendering.chunk;

import com.adrienben.games.bagl.core.io.ResourcePath;
import com.adrienben.games.bagl.engine.camera.Camera;
import com.adrienben.games.bagl.engine.rendering.light.DirectionalLight;
import com.adrienben.games.bagl.engine.rendering.light.Light;
import com.adrienben.games.bagl.opengl.buffer.BufferUsage;
import com.adrienben.games.bagl.opengl.shader.Shader;
import com.adrienben.games.bagl.opengl.texture.Filter;
import com.adrienben.games.bagl.opengl.texture.Texture;
import com.adrienben.games.bagl.opengl.texture.Texture2D;
import com.adrienben.games.bagl.opengl.texture.TextureParameters;
import com.adrienben.games.bagl.opengl.vertex.IndexBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class ChunkRenderer {

    private final Shader shader;
    private final Texture blockAtlas;
    private final IndexBuffer indexBuffer;

    public ChunkRenderer() {
        this.shader = Shader.pipelineBuilder()
                .vertexPath(ResourcePath.get("classpath:/shaders/chunk.vert"))
                .fragmentPath(ResourcePath.get("classpath:/shaders/chunk.frag"))
                .build();
        this.blockAtlas = Texture2D.fromFile(ResourcePath.get("classpath:/textures/blocks.png"),
                TextureParameters.builder().minFilter(Filter.NEAREST).magFilter(Filter.NEAREST));
        this.indexBuffer = this.generateIndices();
    }

    private IndexBuffer generateIndices() {
        final IntBuffer indices = MemoryUtil.memAllocInt(ChunkMesh.MAX_INDEX_COUNT);
        for (int i = 0; i < ChunkMesh.MAX_FACE_COUNT; i++) {
            indices.put(i * ChunkMesh.INDICES_PER_FACE, i * ChunkMesh.VERTICES_PER_FACE);
            indices.put(i * ChunkMesh.INDICES_PER_FACE + 1, i * ChunkMesh.VERTICES_PER_FACE + 1);
            indices.put(i * ChunkMesh.INDICES_PER_FACE + 2, i * ChunkMesh.VERTICES_PER_FACE + 2);
            indices.put(i * ChunkMesh.INDICES_PER_FACE + 3, i * ChunkMesh.VERTICES_PER_FACE + 2);
            indices.put(i * ChunkMesh.INDICES_PER_FACE + 4, i * ChunkMesh.VERTICES_PER_FACE + 1);
            indices.put(i * ChunkMesh.INDICES_PER_FACE + 5, i * ChunkMesh.VERTICES_PER_FACE + 3);
        }
        final IndexBuffer indexBuffer = new IndexBuffer(indices, BufferUsage.STATIC_DRAW);
        MemoryUtil.memFree(indices);
        return indexBuffer;
    }

    public void renderChunk(final ChunkMesh mesh, final Camera camera, final Light ambientLight, final DirectionalLight sunLight) {
        //FIXME: when a chunk is being updated it is not renderer anymore so if loading is longer that a frame we can see it disappearing an reappearing
        //FIXME: to fix it, we should flag the chunks as 'being updated' so we can render them
        mesh.uploadMesh();
        mesh.bind();
        this.indexBuffer.bind();
        this.blockAtlas.bind();
        this.shader.bind();
        this.setUpShader(camera, ambientLight, sunLight);
        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getFaceCount() * ChunkMesh.INDICES_PER_FACE, GL11.GL_UNSIGNED_INT, 0);
        Shader.unbind();
        blockAtlas.unbind();
        this.indexBuffer.unbind();
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

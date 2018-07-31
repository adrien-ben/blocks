package com.adrien.games.blocks.rendering.cube;

import com.adrienben.games.bagl.core.io.ResourcePath;
import com.adrienben.games.bagl.engine.camera.Camera;
import com.adrienben.games.bagl.opengl.shader.Shader;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class CubeRenderer {

    private Shader shader;
    private CubeMesh mesh;

    public CubeRenderer() {
        this.shader = Shader.builder()
                .vertexPath(ResourcePath.get("classpath:/shaders/cube.vert"))
                .fragmentPath(ResourcePath.get("classpath:/shaders/cube.frag"))
                .build();
        this.mesh = new CubeMesh();
    }

    public void renderCube(final Vector3f position, final Camera camera) {
        this.mesh.bind();
        this.shader.bind();
        this.shader.setUniform("uVP", camera.getViewProj());
        this.shader.setUniform("uPosition", position);
        GL11.glDrawElements(GL11.GL_TRIANGLES, CubeMesh.INDEX_COUNT, GL11.GL_UNSIGNED_BYTE, 0);
        Shader.unbind();
        this.mesh.unbind();
    }

    public void destroy() {
        this.shader.destroy();
        this.mesh.destroy();
    }

}

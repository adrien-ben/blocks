package com.adrien.games.blocks.rendering.cube;

import com.adrien.games.bagl.core.Camera;
import com.adrien.games.bagl.core.math.Vector3;
import com.adrien.games.bagl.rendering.Shader;
import org.lwjgl.opengl.GL11;

public class CubeRenderer {

    private Shader shader;
    private CubeMesh mesh;

    public CubeRenderer() {
        this.shader = new Shader().addVertexShader("cube.vert")
                .addFragmentShader("cube.frag")
                .compile();
        this.mesh = new CubeMesh();
    }

    public void renderCube(final Vector3 position, final Camera camera) {
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

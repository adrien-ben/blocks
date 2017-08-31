package com.adrien.games.blocks.rendering.cube;

import com.adrien.games.bagl.core.Camera;
import com.adrien.games.bagl.core.math.Vector3;
import com.adrien.games.bagl.rendering.IndexBuffer;
import com.adrien.games.bagl.rendering.Shader;
import com.adrien.games.bagl.rendering.VertexBuffer;
import org.lwjgl.opengl.GL11;

public class CubeRenderer {

    private Shader shader;

    public CubeRenderer() {
        this.shader = new Shader().addVertexShader("cube.vert").addFragmentShader("cube.frag").compile();
    }

    public void renderCube(final CubeMesh model, final Vector3 position, final Camera camera) {
        model.getVertexBuffer().bind();
        model.getIndexBuffer().bind();
        this.shader.bind();
        this.shader.setUniform("uVP", camera.getViewProj());
        this.shader.setUniform("uPosition", position);
        GL11.glDrawElements(GL11.GL_TRIANGLES, CubeMesh.INDEX_COUNT, GL11.GL_UNSIGNED_INT, 0);
        Shader.unbind();
        IndexBuffer.unbind();
        VertexBuffer.unbind();
    }

    public void destroy() {
        this.shader.destroy();
    }

}

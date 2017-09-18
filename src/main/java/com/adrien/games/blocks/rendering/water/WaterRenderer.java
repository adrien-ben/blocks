package com.adrien.games.blocks.rendering.water;

import com.adrien.games.bagl.core.Camera;
import com.adrien.games.bagl.rendering.Shader;
import com.adrien.games.blocks.world.World;
import org.lwjgl.opengl.GL11;

public class WaterRenderer {

    private Shader shader;
    private WaterMesh mesh;

    public WaterRenderer() {
        this.shader = new Shader().addVertexShader("water.vert").addFragmentShader("water.frag").compile();
        this.mesh = new WaterMesh();
    }

    public void render(final Camera camera) {
        this.shader.bind();
        this.shader.setUniform("uVP", camera.getViewProj());
        this.shader.setUniform("uOffset", camera.getPosition());
        this.shader.setUniform("uWaterLevel", World.WATER_LEVEL);
        this.mesh.bind();

        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 6);

        this.mesh.unbind();
        Shader.unbind();
    }

    public void destroy() {
        this.shader.destroy();
        this.mesh.destroy();
    }

}

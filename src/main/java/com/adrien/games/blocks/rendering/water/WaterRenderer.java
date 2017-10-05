package com.adrien.games.blocks.rendering.water;

import com.adrien.games.bagl.core.Camera;
import com.adrien.games.bagl.rendering.Shader;
import com.adrien.games.bagl.rendering.light.DirectionalLight;
import com.adrien.games.bagl.rendering.light.Light;
import com.adrien.games.blocks.world.World;
import org.lwjgl.opengl.GL11;

public class WaterRenderer {

    private Shader shader;
    private WaterMesh mesh;

    public WaterRenderer() {
        this.shader = new Shader().addVertexShader("water.vert").addFragmentShader("water.frag").compile();
        this.mesh = new WaterMesh();
    }

    public void render(final Camera camera, final Light ambientLight, final DirectionalLight sunLight) {
        this.shader.bind();
        this.setUpShader(camera, ambientLight, sunLight);
        this.mesh.bind();

        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);

        this.mesh.unbind();
        Shader.unbind();
    }

    private void setUpShader(final Camera camera, final Light ambientLight, final DirectionalLight sunLight) {
        this.shader.setUniform("uVP", camera.getViewProj());
        this.shader.setUniform("uOffset", camera.getPosition());
        this.shader.setUniform("uWaterLevel", World.WATER_LEVEL);
        this.shader.setUniform("uCameraPosition", camera.getPosition());
        this.shader.setUniform("uAmbient.color", ambientLight.getColor());
        this.shader.setUniform("uAmbient.intensity", ambientLight.getIntensity());
        this.shader.setUniform("uSunLight.base.color", sunLight.getColor());
        this.shader.setUniform("uSunLight.base.intensity", sunLight.getIntensity());
        this.shader.setUniform("uSunLight.direction", sunLight.getDirection());
    }

    public void destroy() {
        this.shader.destroy();
        this.mesh.destroy();
    }

}

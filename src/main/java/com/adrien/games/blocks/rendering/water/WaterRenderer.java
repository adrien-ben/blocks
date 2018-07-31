package com.adrien.games.blocks.rendering.water;

import com.adrien.games.blocks.world.World;
import com.adrienben.games.bagl.core.io.ResourcePath;
import com.adrienben.games.bagl.engine.camera.Camera;
import com.adrienben.games.bagl.engine.rendering.light.DirectionalLight;
import com.adrienben.games.bagl.engine.rendering.light.Light;
import com.adrienben.games.bagl.opengl.shader.Shader;
import org.lwjgl.opengl.GL11;

public class WaterRenderer {

    private Shader shader;
    private WaterMesh mesh;

    public WaterRenderer() {
        this.shader = Shader.builder()
                .vertexPath(ResourcePath.get("classpath:/shaders/water.vert"))
                .fragmentPath(ResourcePath.get("classpath:/shaders/water.frag"))
                .build();
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

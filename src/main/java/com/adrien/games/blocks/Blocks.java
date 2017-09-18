package com.adrien.games.blocks;

import com.adrien.games.bagl.core.*;
import com.adrien.games.bagl.core.math.Vector3;
import com.adrien.games.bagl.rendering.BlendMode;
import com.adrien.games.blocks.rendering.chunk.ChunkRenderer;
import com.adrien.games.blocks.rendering.cube.CubeRenderer;
import com.adrien.games.blocks.rendering.water.WaterRenderer;
import com.adrien.games.blocks.world.Chunk;
import com.adrien.games.blocks.world.World;
import com.adrien.games.blocks.world.block.Block;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.Objects;
import java.util.Optional;

public class Blocks implements Game {

    private Camera camera;
    private CameraController cameraController;

    private World world;
    private Block pointedBlock;

    private ChunkRenderer chunkRenderer;
    private CubeRenderer cubeRenderer;
    private WaterRenderer waterRenderer;

    @Override
    public void init() {
        Input.setMouseMode(MouseMode.DISABLED);
        Engine.setClearColor(Color.CORNFLOWER_BLUE);
        Engine.setBlendMode(BlendMode.DEFAULT);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);

        final Configuration conf = Configuration.getInstance();
        this.camera = new Camera(new Vector3(0f, World.WATER_LEVEL + 1, 0f), new Vector3(0, 0, 1), new Vector3(0, 1, 0),
                (float) Math.toRadians(70f), (float) conf.getXResolution() / conf.getYResolution(), 0.1f, 200f);
        this.cameraController = new CameraController(this.camera);

        this.world = new World();

        this.chunkRenderer = new ChunkRenderer();
        this.cubeRenderer = new CubeRenderer();
        this.waterRenderer = new WaterRenderer();
    }

    @Override
    public void destroy() {
        this.world.destroy();
        this.chunkRenderer.destroy();
        this.cubeRenderer.destroy();
    }

    @Override
    public void update(final Time time) {
        this.cameraController.update(time);
        this.world.update(this.camera.getPosition());

        final Vector3 position = new Vector3(this.camera.getPosition());
        final Vector3 direction = new Vector3(this.camera.getDirection());

        final Optional<Block> pointedBlock = this.world.getBlock(position, direction, 3, Block::isVisible);
        this.pointedBlock = pointedBlock.orElse(null);

        if (Input.wasMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_2)) {
            pointedBlock.ifPresent(block -> this.world.removeBlock(new Vector3(block.getWorldX(), block.getWorldY(), block.getWorldZ())));
        }
    }

    @Override
    public void render() {
        Engine.setBlendMode(BlendMode.DEFAULT);
        GL11.glDepthFunc(GL11.GL_LESS);
        this.world.getChunks().map(Chunk::getMesh).forEach(chunkMesh -> this.chunkRenderer.renderChunk(chunkMesh, this.camera));

        Engine.setBlendMode(BlendMode.TRANSPARENCY);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        this.waterRenderer.render(this.camera);

        if (Objects.nonNull(this.pointedBlock)) {
            this.cubeRenderer.renderCube(new Vector3(this.pointedBlock.getWorldX(), this.pointedBlock.getWorldY(), this.pointedBlock.getWorldZ()), this.camera);
        }
    }

    public static void main(final String[] args) {
        new Engine(new Blocks(), "Blocks").start();
    }

}

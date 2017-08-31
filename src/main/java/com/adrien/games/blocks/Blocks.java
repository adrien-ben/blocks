package com.adrien.games.blocks;

import com.adrien.games.bagl.core.*;
import com.adrien.games.bagl.core.math.Vector3;
import com.adrien.games.bagl.rendering.BlendMode;
import com.adrien.games.bagl.rendering.texture.Filter;
import com.adrien.games.bagl.rendering.texture.Texture;
import com.adrien.games.bagl.rendering.texture.TextureParameters;
import com.adrien.games.bagl.utils.FileUtils;
import com.adrien.games.blocks.rendering.chunk.ChunkRenderer;
import com.adrien.games.blocks.rendering.cube.CubeMesh;
import com.adrien.games.blocks.rendering.cube.CubeRenderer;
import com.adrien.games.blocks.world.Chunk;
import com.adrien.games.blocks.world.World;
import com.adrien.games.blocks.world.block.Block;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.Objects;
import java.util.Optional;

public class Blocks implements Game {

    private Texture texture;
    private Camera camera;
    private CameraController cameraController;

    private World world;
    private ChunkRenderer chunkRenderer;

    private CubeMesh cubeMesh;
    private CubeRenderer cubeRenderer;

    private Block pointedBlock;

    @Override
    public void init() {
        Input.setMouseMode(MouseMode.DISABLED);
        Engine.setClearColor(Color.CORNFLOWER_BLUE);
        Engine.setBlendMode(BlendMode.DEFAULT);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);

        final Configuration conf = Configuration.getInstance();
        this.texture = new Texture(FileUtils.getResourceAbsolutePath("/textures/blocks.png"),
                new TextureParameters().minFilter(Filter.NEAREST).magFilter(Filter.NEAREST));
        this.camera = new Camera(new Vector3(5.5f, World.WORLD_MAX_HEIGHT * World.CHUNK_HEIGHT + 1, 5.5f), new Vector3(0, 0, 1), new Vector3(0, 1, 0),
                (float) Math.toRadians(70f), (float) conf.getXResolution() / conf.getYResolution(), 0.1f, 100f);
        this.cameraController = new CameraController(this.camera);

        this.world = new World();
        this.chunkRenderer = new ChunkRenderer();

        this.cubeMesh = new CubeMesh();
        this.cubeRenderer = new CubeRenderer();
    }

    @Override
    public void destroy() {
        this.texture.destroy();
        this.world.destroy();
        this.chunkRenderer.destroy();
        this.cubeMesh.destroy();
        this.cubeRenderer.destroy();
    }

    @Override
    public void update(final Time time) {
        this.cameraController.update(time);
        this.world.update(this.camera.getPosition());

        final Vector3 position = new Vector3(this.camera.getPosition());
        final Vector3 direction = new Vector3(this.camera.getDirection());

        final Optional<Block> pointedBlock = this.world.getBlock(position, direction, 2, Block::isNotAir);
        this.pointedBlock = pointedBlock.orElse(null);

        if (Input.wasMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_2)) {
            pointedBlock.ifPresent(block -> this.world.removeBlock(new Vector3(block.getWorldX(), block.getWorldY(), block.getWorldZ())));
        }
    }

    @Override
    public void render() {
        this.world.getChunks().map(Chunk::getMesh).forEach(chunkMesh -> this.chunkRenderer.renderChunk(chunkMesh, this.camera, this.texture));
        if (Objects.nonNull(this.pointedBlock)) {
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            Engine.setBlendMode(BlendMode.TRANSPARENCY);
            this.cubeRenderer.renderCube(this.cubeMesh, new Vector3(this.pointedBlock.getWorldX(), this.pointedBlock.getWorldY(), this.pointedBlock.getWorldZ()), this.camera);
            Engine.setBlendMode(BlendMode.DEFAULT);
            GL11.glDepthFunc(GL11.GL_LESS);
        }
    }

    public static void main(final String[] args) {
        new Engine(new Blocks(), "Blocks").start();
    }

}

package com.adrien.games.blocks;

import com.adrien.games.bagl.core.*;
import com.adrien.games.bagl.core.camera.Camera;
import com.adrien.games.bagl.rendering.BlendMode;
import com.adrien.games.bagl.rendering.FrameBuffer;
import com.adrien.games.bagl.rendering.FrameBufferParameters;
import com.adrien.games.bagl.rendering.light.DirectionalLight;
import com.adrien.games.bagl.rendering.light.Light;
import com.adrien.games.bagl.rendering.postprocess.PostProcessor;
import com.adrien.games.bagl.rendering.postprocess.steps.FxaaStep;
import com.adrien.games.bagl.rendering.postprocess.steps.LumaStep;
import com.adrien.games.bagl.rendering.texture.Format;
import com.adrien.games.blocks.player.Player;
import com.adrien.games.blocks.player.PlayerController;
import com.adrien.games.blocks.rendering.chunk.ChunkRenderer;
import com.adrien.games.blocks.rendering.cube.CubeRenderer;
import com.adrien.games.blocks.rendering.water.WaterRenderer;
import com.adrien.games.blocks.world.Chunk;
import com.adrien.games.blocks.world.World;
import com.adrien.games.blocks.world.block.Block;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.Objects;
import java.util.Optional;

public class Blocks implements Game {

    private Player player;
    private PlayerController playerController;
    private Camera camera;

    private World world;
    private Block pointedBlock;

    private Light ambientLight = new Light(0.3f, Color.WHITE);
    private DirectionalLight sunLight = new DirectionalLight(0.8f, Color.WHITE, new Vector3f(1.2f, -2.0f, 3.0f));

    private ChunkRenderer chunkRenderer;
    private CubeRenderer cubeRenderer;
    private WaterRenderer waterRenderer;

    private FrameBuffer frameBuffer;
    private PostProcessor postProcessor;

    @Override
    public void init() {
        Input.setMouseMode(MouseMode.DISABLED);
        Engine.setClearColor(Color.CORNFLOWER_BLUE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        final Configuration conf = Configuration.getInstance();

        this.player = new Player(new Vector3f(0f, World.WATER_LEVEL + 1.8f, 0f), new Vector3f(0, 0, 1), 10f);
        this.playerController = new PlayerController(this.player);

        this.camera = new Camera(new Vector3f(this.player.getPosition()), new Vector3f(this.player.getDirection()), new Vector3f(0, 1, 0),
                (float) Math.toRadians(70f), (float) conf.getXResolution() / conf.getYResolution(), 0.1f, 200f);

        this.world = new World(this.player);

        this.chunkRenderer = new ChunkRenderer();
        this.cubeRenderer = new CubeRenderer();
        this.waterRenderer = new WaterRenderer();

        this.frameBuffer = new FrameBuffer(conf.getXResolution(), conf.getYResolution(), FrameBufferParameters.builder().colorOutputFormat(Format.RGBA8).build());
        this.postProcessor = new PostProcessor(
                new LumaStep(conf.getXResolution(), conf.getYResolution()),
                new FxaaStep(conf.getXResolution(), conf.getYResolution(), conf.getFxaaPresets())
        );
    }

    @Override
    public void destroy() {
        this.world.destroy();
        this.chunkRenderer.destroy();
        this.cubeRenderer.destroy();
        this.waterRenderer.destroy();
        this.frameBuffer.destroy();
        this.postProcessor.destroy();
    }

    @Override
    public void update(final Time time) {
        this.playerController.update();
        this.camera.setPosition(new Vector3f(this.player.getPosition()).add(new Vector3f(0f, 1.8f, 0f)));
        this.camera.setDirection(this.player.getDirection());

        this.world.update(time);

        final Vector3f position = new Vector3f(this.camera.getPosition());
        final Vector3f direction = new Vector3f(this.camera.getDirection());

        final Optional<Block> pointedBlock = this.world.getBlock(position, direction, 3, Block::isVisible);
        this.pointedBlock = pointedBlock.orElse(null);

        if (Input.wasMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_2)) {
            pointedBlock.ifPresent(block -> this.world.removeBlock(new Vector3f(block.getWorldX(), block.getWorldY(), block.getWorldZ())));
        }
    }

    @Override
    public void render() {
        frameBuffer.bind();
        frameBuffer.clear(Color.CORNFLOWER_BLUE);

        Engine.setBlendMode(BlendMode.DEFAULT);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDepthFunc(GL11.GL_LESS);
        this.world.getChunks()
                .map(Chunk::getMesh)
                .forEach(chunkMesh -> this.chunkRenderer.renderChunk(chunkMesh, this.camera, this.ambientLight, this.sunLight));

        Engine.setBlendMode(BlendMode.TRANSPARENCY);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        if (Objects.nonNull(this.pointedBlock)) {
            this.cubeRenderer.renderCube(new Vector3f(this.pointedBlock.getWorldX(), this.pointedBlock.getWorldY(), this.pointedBlock.getWorldZ()),
                    this.camera);
        }

        GL11.glDisable(GL11.GL_CULL_FACE);
        this.waterRenderer.render(this.camera, this.ambientLight, this.sunLight);

        frameBuffer.unbind();

        Engine.setBlendMode(BlendMode.NONE);
        postProcessor.process(frameBuffer.getColorTexture(0));
    }

    public static void main(final String[] args) {
        new Engine(new Blocks(), "Blocks").start();
    }

}

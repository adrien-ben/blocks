package com.adrien.games.blocks;

import com.adrien.games.bagl.core.*;
import com.adrien.games.bagl.core.math.Vector3;
import com.adrien.games.bagl.rendering.BlendMode;
import com.adrien.games.bagl.rendering.light.DirectionalLight;
import com.adrien.games.bagl.rendering.light.Light;
import com.adrien.games.blocks.player.Player;
import com.adrien.games.blocks.player.PlayerController;
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

    private Player player;
    private PlayerController playerController;
    private Camera camera;

    private World world;
    private Block pointedBlock;

    private Light ambientLight = new Light(0.3f, Color.WHITE);
    private DirectionalLight sunLight = new DirectionalLight(0.8f, Color.WHITE, new Vector3(1.2f, -2.0f, 3.0f));

    private ChunkRenderer chunkRenderer;
    private CubeRenderer cubeRenderer;
    private WaterRenderer waterRenderer;

    @Override
    public void init() {
        Input.setMouseMode(MouseMode.DISABLED);
        Engine.setClearColor(Color.CORNFLOWER_BLUE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        final Configuration conf = Configuration.getInstance();

        this.player = new Player(new Vector3(0f, World.WATER_LEVEL + 1.8f, 0f), new Vector3(0, 0, 1));
        this.playerController = new PlayerController(this.player);

        this.camera = new Camera(new Vector3(this.player.getPosition()), new Vector3(this.player.getDirection()), new Vector3(0, 1, 0),
                (float) Math.toRadians(70f), (float) conf.getXResolution() / conf.getYResolution(), 0.1f, 200f);

        this.world = new World(this.player);

        this.chunkRenderer = new ChunkRenderer();
        this.cubeRenderer = new CubeRenderer();
        this.waterRenderer = new WaterRenderer();
    }

    @Override
    public void destroy() {
        this.world.destroy();
        this.chunkRenderer.destroy();
        this.cubeRenderer.destroy();
        this.waterRenderer.destroy();
    }

    @Override
    public void update(final Time time) {
        this.playerController.update(time);
        this.camera.setPosition(Vector3.add(this.player.getPosition(), new Vector3(0, 1.8f, 0)));
        this.camera.setDirection(this.player.getDirection());

        this.world.update(time);

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
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDepthFunc(GL11.GL_LESS);
        //FIXME: when a chunk is being updated it is nor renderer anymore so if loading is longer that a frame we can see it disappearing an reappearing
        //FIXME: to fix it, we should flag the chunks as 'being updated' so we can render them
        this.world.getChunks()
                .map(Chunk::getMesh)
                .forEach(chunkMesh -> this.chunkRenderer.renderChunk(chunkMesh, this.camera, this.ambientLight, this.sunLight));

        Engine.setBlendMode(BlendMode.TRANSPARENCY);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        if (Objects.nonNull(this.pointedBlock)) {
            this.cubeRenderer.renderCube(new Vector3(this.pointedBlock.getWorldX(), this.pointedBlock.getWorldY(), this.pointedBlock.getWorldZ()),
                    this.camera);
        }

        GL11.glDisable(GL11.GL_CULL_FACE);
        this.waterRenderer.render(this.camera, this.ambientLight, this.sunLight);

    }

    public static void main(final String[] args) {
        new Engine(new Blocks(), "Blocks").start();
    }

}

package com.adrien.games.blocks.player;

import com.adrien.games.bagl.core.Input;
import com.adrien.games.bagl.core.math.Matrix4;
import com.adrien.games.bagl.core.math.Vector2;
import com.adrien.games.bagl.core.math.Vector3;
import org.lwjgl.glfw.GLFW;

public class PlayerController {

    private static final float DEFAULT_DEGREES_PER_PIXEL = 0.15f;
    private static final float INITIAL_JUMP_SPEED = 30f;

    private final Player player;
    private final Vector3 forward;
    private final Vector3 side;
    private final Vector3 direction;

    public PlayerController(final Player player) {
        this.player = player;
        this.forward = new Vector3();
        this.side = new Vector3();
        this.direction = new Vector3();
    }

    public void update() {
        this.handleRotation();
        this.handleMovement();
        this.handleJump();
    }

    private void handleRotation() {
        final Vector2 mouseDelta = Input.getMouseDelta();
        if (!mouseDelta.isZero()) {
            this.forward.set(this.player.getDirection()).normalise();
            Vector3.cross(this.forward, Vector3.UP, this.side);

            if (mouseDelta.getY() != 0) {
                float vAngle = (float) Math.toRadians(mouseDelta.getY() * DEFAULT_DEGREES_PER_PIXEL);
                Vector3.transform(Matrix4.createRotation(this.side, vAngle), this.forward, 0, this.forward);
            }
            if (mouseDelta.getX() != 0) {
                float hAngle = -(float) Math.toRadians(mouseDelta.getX() * DEFAULT_DEGREES_PER_PIXEL);
                Vector3.transform(Matrix4.createRotation(Vector3.UP, hAngle), this.forward, 0, this.forward);
            }

            this.player.getDirection().set(this.forward);
        }
    }

    private void handleMovement() {
        this.direction.setXYZ(0, 0, 0);
        this.forward.set(this.player.getDirection());
        this.forward.setY(0).normalise();
        Vector3.cross(this.forward, Vector3.UP, this.side);

        if (Input.isKeyPressed(GLFW.GLFW_KEY_W) || Input.isKeyPressed(GLFW.GLFW_KEY_S)) {
            if (Input.isKeyPressed(GLFW.GLFW_KEY_S)) {
                this.forward.scale(-1);
            }
            this.direction.add(this.forward);
        }
        if (Input.isKeyPressed(GLFW.GLFW_KEY_D) || Input.isKeyPressed(GLFW.GLFW_KEY_A)) {
            if (Input.isKeyPressed(GLFW.GLFW_KEY_A)) {
                this.side.scale(-1);
            }
            this.direction.add(this.side);
        }

        if (!this.direction.isZero()) {
            final Vector3 movement = this.direction.normalise().scale(this.player.getSpeed());
            this.player.getVelocity().setX(movement.getX());
            this.player.getVelocity().setZ(movement.getZ());
        } else {
            this.player.getVelocity().setX(0);
            this.player.getVelocity().setZ(0);
        }
    }

    private void handleJump() {
        if (Input.wasKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            final float currentYVelocity = this.player.getVelocity().getY();
            this.player.getVelocity().setY(currentYVelocity + INITIAL_JUMP_SPEED);
        }
    }

}

package com.adrien.games.blocks.player;

import com.adrienben.games.bagl.core.math.Vectors;
import com.adrienben.games.bagl.engine.Input;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class PlayerController {

    private static final float DEFAULT_DEGREES_PER_PIXEL = 0.15f;
    private static final float INITIAL_JUMP_SPEED = 30f;

    private final Player player;
    private final Vector3f forward;
    private final Vector3f side;
    private final Vector3f direction;

    public PlayerController(final Player player) {
        this.player = player;
        this.forward = new Vector3f();
        this.side = new Vector3f();
        this.direction = new Vector3f();
    }

    public void update() {
        this.handleRotation();
        this.handleMovement();
        this.handleJump();
    }

    private void handleRotation() {
        final Vector2f mouseDelta = Input.getMouseDelta();
        if (!Vectors.isZero(mouseDelta)) {
            this.forward.set(this.player.getDirection()).normalize();
            this.forward.cross(Vectors.VEC3_UP, this.side);

            if (mouseDelta.y() != 0) {
                float vAngle = (float) Math.toRadians(mouseDelta.y() * DEFAULT_DEGREES_PER_PIXEL);
                this.forward.rotateAxis(vAngle, this.side.x(), this.side.y(), this.side.z());
            }
            if (mouseDelta.x() != 0) {
                float hAngle = -(float) Math.toRadians(mouseDelta.x() * DEFAULT_DEGREES_PER_PIXEL);
                this.forward.rotateAxis(hAngle, 0, 1, 0);
            }

            this.player.getDirection().set(this.forward);
        }
    }

    private void handleMovement() {
        this.direction.set(0, 0, 0);
        this.forward.set(this.player.getDirection().x(), 0, this.player.getDirection().z());
        this.forward.cross(Vectors.VEC3_UP, this.side);

        if (Input.isKeyPressed(GLFW.GLFW_KEY_W) || Input.isKeyPressed(GLFW.GLFW_KEY_S)) {
            if (Input.isKeyPressed(GLFW.GLFW_KEY_S)) {
                this.forward.mul(-1);
            }
            this.direction.add(this.forward);
        }
        if (Input.isKeyPressed(GLFW.GLFW_KEY_D) || Input.isKeyPressed(GLFW.GLFW_KEY_A)) {
            if (Input.isKeyPressed(GLFW.GLFW_KEY_A)) {
                this.side.mul(-1);
            }
            this.direction.add(this.side);
        }

        if (!Vectors.isZero(this.direction)) {
            final Vector3f movement = this.direction.normalize().mul(this.player.getSpeed());
            this.player.getVelocity().setComponent(0, movement.x());
            this.player.getVelocity().setComponent(2, movement.z());
        } else {
            this.player.getVelocity().setComponent(0, 0);
            this.player.getVelocity().setComponent(2, 0);
        }
    }

    private void handleJump() {
        if (Input.wasKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            final float currentYVelocity = this.player.getVelocity().y();
            this.player.getVelocity().setComponent(1, currentYVelocity + INITIAL_JUMP_SPEED);
        }
    }

}

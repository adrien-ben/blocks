package com.adrien.games.blocks.player;


import org.joml.Vector3f;

public class Player {

    private final Vector3f position;
    private final Vector3f direction;
    private final Vector3f velocity;
    private float speed;

    public Player(final Vector3f position, final Vector3f direction, final float speed) {
        this.position = position;
        this.direction = direction;
        this.velocity = new Vector3f();
        this.speed = speed;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}

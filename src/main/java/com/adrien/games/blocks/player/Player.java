package com.adrien.games.blocks.player;

import com.adrien.games.bagl.core.math.Vector3;

public class Player {

    private final Vector3 position;
    private final Vector3 direction;
    private final Vector3 velocity;
    private float speed;

    public Player(final Vector3 position, final Vector3 direction, final float speed) {
        this.position = position;
        this.direction = direction;
        this.velocity = new Vector3();
        this.speed = speed;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getDirection() {
        return direction;
    }

    public Vector3 getVelocity() {
        return velocity;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}

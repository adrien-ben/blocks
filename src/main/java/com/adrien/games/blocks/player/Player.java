package com.adrien.games.blocks.player;

import com.adrien.games.bagl.core.math.Vector3;

public class Player {

    private final Vector3 position;
    private final Vector3 direction;
    private float speed;

    public Player(final Vector3 position, final Vector3 direction, final float speed) {
        this.position = position;
        this.direction = direction;
        this.speed = speed;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getDirection() {
        return direction;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}

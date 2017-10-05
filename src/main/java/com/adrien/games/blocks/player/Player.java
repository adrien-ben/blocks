package com.adrien.games.blocks.player;

import com.adrien.games.bagl.core.math.Vector3;

public class Player {

    private final Vector3 position;
    private final Vector3 direction;

    public Player(final Vector3 position, final Vector3 direction) {
        this.position = position;
        this.direction = direction;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getDirection() {
        return direction;
    }
}

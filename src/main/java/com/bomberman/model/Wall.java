package com.bomberman.model;

import com.bomberman.util.Position;

public class Wall {
    private Position position;
    private boolean destructible;

    public Wall(Position position, boolean destructible) {
        this.position = new Position(position);
        this.destructible = destructible;
    }

    public Position getPosition() { return new Position(position); }
    public boolean isDestructible() { return destructible; }
}
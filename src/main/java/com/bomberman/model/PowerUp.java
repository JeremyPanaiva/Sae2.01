package com.bomberman.model;

import com.bomberman.util.Position;

public class PowerUp {
    public enum Type {
        EXTRA_BOMB,
        BIGGER_EXPLOSION,
    }

    private Position position;
    private Type type;

    public PowerUp(Position position, Type type) {
        this.position = new Position(position);
        this.type = type;
    }

    public void applyTo(Player player) {
        switch (type) {
            case EXTRA_BOMB:
                player.setMaxBombs(player.getMaxBombs() + 1);
                break;
            case BIGGER_EXPLOSION:
                player.setExplosionRange(player.getExplosionRange() + 1);
                break;
        }
    }

    public Position getPosition() { return new Position(position); }
    public Type getType() { return type; }
}
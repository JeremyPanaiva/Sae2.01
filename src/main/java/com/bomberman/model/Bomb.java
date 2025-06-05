package com.bomberman.model;

import com.bomberman.util.Position;
import com.bomberman.util.GameConstants;

public class Bomb {
    private Position position;
    private Player owner;
    private long placedTime;
    private int explosionRange;

    public Bomb(Position position, Player owner) {
        this.position = new Position(position);
        this.owner = owner;
        this.placedTime = System.currentTimeMillis();
        this.explosionRange = owner.getExplosionRange();
    }

    public boolean shouldExplode() {
        return System.currentTimeMillis() - placedTime >= GameConstants.BOMB_TIMER;
    }

    public Position getPosition() { return new Position(position); }
    public Player getOwner() { return owner; }
    public int getExplosionRange() { return explosionRange; }
    public long getTimeLeft() {
        return Math.max(0, GameConstants.BOMB_TIMER - (System.currentTimeMillis() - placedTime));
    }
}
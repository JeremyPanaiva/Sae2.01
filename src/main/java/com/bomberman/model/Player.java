package com.bomberman.model;

import com.bomberman.util.Direction;
import com.bomberman.util.Position;

public class Player {
    private int id;
    private Position position;
    private boolean alive;
    private int bombCount;
    private int maxBombs;
    private int explosionRange;
    private String color;

    public Player(int id, Position position, String color) {
        this.id = id;
        this.position = new Position(position);
        this.alive = true;
        this.bombCount = 0;
        this.maxBombs = 1;
        this.explosionRange = 2;
        this.color = color;
    }

    public boolean canPlaceBomb() {
        return bombCount < maxBombs;
    }

    public void placeBomb() {
        if (canPlaceBomb()) {
            bombCount++;
        }
    }

    public void bombExploded() {
        if (bombCount > 0) {
            bombCount--;
        }
    }

    public void move(Direction direction, GameBoard board) {
        if (!alive) return;

        Position newPosition = position.getNeighbor(direction);
        if (board.canMoveTo(newPosition)) {
            position = newPosition;
        }
    }

    public void kill() {
        this.alive = false;
    }

    // Getters
    public int getId() { return id; }
    public Position getPosition() { return new Position(position); }
    public boolean isAlive() { return alive; }
    public int getBombCount() { return bombCount; }
    public int getMaxBombs() { return maxBombs; }
    public int getExplosionRange() { return explosionRange; }
    public String getColor() { return color; }

    // Setters pour les power-ups
    public void setMaxBombs(int maxBombs) { this.maxBombs = maxBombs; }
    public void setExplosionRange(int explosionRange) { this.explosionRange = explosionRange; }
}
package com.bomberman.model;

import com.bomberman.util.Position;
import com.bomberman.util.GameConstants;
import java.util.*;

public class GameBoard {
    private Wall[][] walls;
    private Map<Position, Bomb> bombs;
    private Map<Position, PowerUp> powerUps;
    private List<Explosion> explosions;

    public GameBoard() {
        walls = new Wall[GameConstants.BOARD_WIDTH][GameConstants.BOARD_HEIGHT];
        bombs = new HashMap<>();
        powerUps = new HashMap<>();
        explosions = new ArrayList<>();
        initializeWalls();
    }

    private void initializeWalls() {
        // Murs indestructibles sur les bords et en damier
        for (int x = 0; x < GameConstants.BOARD_WIDTH; x++) {
            for (int y = 0; y < GameConstants.BOARD_HEIGHT; y++) {
                if (x == 0 || x == GameConstants.BOARD_WIDTH - 1 ||
                        y == 0 || y == GameConstants.BOARD_HEIGHT - 1 ||
                        (x % 2 == 0 && y % 2 == 0)) {
                    walls[x][y] = new Wall(new Position(x, y), false);
                }
            }
        }

        // Murs destructibles aléatoirement
        Random random = new Random();
        for (int x = 1; x < GameConstants.BOARD_WIDTH - 1; x++) {
            for (int y = 1; y < GameConstants.BOARD_HEIGHT - 1; y++) {
                if (walls[x][y] == null && random.nextDouble() < 0.4) {
                    // Ne pas placer de murs près des positions de départ des joueurs
                    if (!isNearStartPosition(x, y)) {
                        walls[x][y] = new Wall(new Position(x, y), true);
                    }
                }
            }
        }
    }

    private boolean isNearStartPosition(int x, int y) {
        Position[] startPositions = {
                new Position(1, 1), new Position(GameConstants.BOARD_WIDTH - 2, 1),
                new Position(1, GameConstants.BOARD_HEIGHT - 2),
                new Position(GameConstants.BOARD_WIDTH - 2, GameConstants.BOARD_HEIGHT - 2)
        };

        for (Position start : startPositions) {
            if (Math.abs(x - start.getX()) <= 1 && Math.abs(y - start.getY()) <= 1) {
                return true;
            }
        }
        return false;
    }

    public boolean canMoveTo(Position position) {
        if (!isValidPosition(position)) return false;
        if (hasWall(position)) return false;
        if (hasBomb(position)) return false;
        return true;
    }

    public boolean isValidPosition(Position position) {
        int x = position.getX();
        int y = position.getY();
        return x >= 0 && x < GameConstants.BOARD_WIDTH &&
                y >= 0 && y < GameConstants.BOARD_HEIGHT;
    }

    public boolean hasWall(Position position) {
        if (!isValidPosition(position)) return true;
        return walls[position.getX()][position.getY()] != null;
    }

    public boolean hasDestructibleWall(Position position) {
        if (!hasWall(position)) return false;
        Wall wall = walls[position.getX()][position.getY()];
        return wall.isDestructible();
    }

    public void destroyWall(Position position) {
        if (hasDestructibleWall(position)) {
            // Chance de laisser un power-up
            if (new Random().nextDouble() < 0.3) {
                PowerUp.Type[] types = PowerUp.Type.values();
                PowerUp.Type randomType = types[new Random().nextInt(types.length)];
                powerUps.put(position, new PowerUp(position, randomType));
            }
            walls[position.getX()][position.getY()] = null;
        }
    }

    public boolean hasBomb(Position position) {
        return bombs.containsKey(position);
    }

    public void placeBomb(Bomb bomb) {
        bombs.put(bomb.getPosition(), bomb);
    }

    public void removeBomb(Position position) {
        bombs.remove(position);
    }

    public PowerUp getPowerUp(Position position) {
        return powerUps.remove(position);
    }

    public void addExplosion(Explosion explosion) {
        explosions.add(explosion);
    }

    public void removeFinishedExplosions() {
        explosions.removeIf(Explosion::isFinished);
    }

    // Getters
    public Wall getWall(Position position) {
        if (!isValidPosition(position)) return null;
        return walls[position.getX()][position.getY()];
    }

    public Collection<Bomb> getBombs() { return new ArrayList<>(bombs.values()); }
    public Collection<PowerUp> getPowerUps() { return new ArrayList<>(powerUps.values()); }
    public List<Explosion> getExplosions() { return new ArrayList<>(explosions); }
}
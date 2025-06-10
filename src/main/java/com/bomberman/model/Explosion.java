package com.bomberman.model;

import com.bomberman.util.Position;
import com.bomberman.util.Direction;
import com.bomberman.util.GameConstants;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Explosion {
    public enum ExplosionType {
        CENTER,
        HORIZONTAL,
        VERTICAL,
        END
    }

    private Map<Position, ExplosionType> explosionTypes;
    private long startTime;
    private Position centerPosition;

    public Explosion(List<Position> positions, Position center) {
        this.explosionTypes = new HashMap<>();
        this.startTime = System.currentTimeMillis();
        this.centerPosition = center;

        categorizeExplosionPositions(positions, center);
    }

    private void categorizeExplosionPositions(List<Position> positions, Position center) {
        for (Position pos : positions) {
            if (pos.equals(center)) {
                // Position centrale
                explosionTypes.put(pos, ExplosionType.CENTER);
            } else {
                // Déterminer la direction par rapport au centre
                int deltaX = pos.getX() - center.getX();
                int deltaY = pos.getY() - center.getY();

                if (deltaX == 0 && deltaY != 0) {
                    // Explosion verticale (haut/bas)
                    explosionTypes.put(pos, ExplosionType.VERTICAL);
                } else if (deltaY == 0 && deltaX != 0) {
                    // Explosion horizontale (gauche/droite)
                    explosionTypes.put(pos, ExplosionType.HORIZONTAL);
                } else {
                    // Position de fin d'explosion (normalement ne devrait pas arriver)
                    explosionTypes.put(pos, ExplosionType.END);
                }
            }
        }
    }

    public boolean isFinished() {
        return System.currentTimeMillis() - startTime >= GameConstants.EXPLOSION_DURATION;
    }

    public boolean isAboutToFinish() {
        long elapsed = System.currentTimeMillis() - startTime;
        return elapsed >= GameConstants.EXPLOSION_DURATION - 200; // 200ms avant la fin
    }

    public List<Position> getPositions() {
        return new ArrayList<>(explosionTypes.keySet());
    }

    public ExplosionType getExplosionType(Position position) {
        return explosionTypes.getOrDefault(position, ExplosionType.END);
    }

    public Map<Position, ExplosionType> getAllExplosionTypes() {
        return new HashMap<>(explosionTypes);
    }

    public boolean contains(Position position) {
        return explosionTypes.containsKey(position);
    }

    public Position getCenterPosition() {
        return new Position(centerPosition);
    }
}
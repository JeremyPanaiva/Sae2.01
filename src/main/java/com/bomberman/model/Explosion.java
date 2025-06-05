package com.bomberman.model;

import com.bomberman.util.Position;
import com.bomberman.util.GameConstants;
import java.util.List;
import java.util.ArrayList;

public class Explosion {
    private List<Position> positions;
    private long startTime;

    public Explosion(List<Position> positions) {
        this.positions = new ArrayList<>(positions);
        this.startTime = System.currentTimeMillis();
    }

    public boolean isFinished() {
        return System.currentTimeMillis() - startTime >= GameConstants.EXPLOSION_DURATION;
    }

    public List<Position> getPositions() {
        return new ArrayList<>(positions);
    }

    public boolean contains(Position position) {
        return positions.contains(position);
    }
}
package com.bomberman.model;

import com.bomberman.util.Direction;
import com.bomberman.util.Position;
import java.util.*;

public class BotPlayer extends Player {
    private Random random;
    private long lastActionTime;
    private long lastBombTime;
    private static final long ACTION_DELAY = 120;
    private static final long BOMB_INTERVAL = 10000; // 10 secondes

    public BotPlayer(int id, Position position, String color) {
        super(id, position, color);
        this.random = new Random();
        this.lastActionTime = 0;
        this.lastBombTime = 0;
    }

    public void makeMove(GameBoard board, List<Player> allPlayers) {
        if (!isAlive()) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastActionTime < ACTION_DELAY) return;
        lastActionTime = currentTime;

        Position myPos = getPosition();

        // PRIORITÉ ABSOLUE : Survivre aux explosions
        if (isInImmediateDanger(board, myPos)) {
            Direction escapeDir = findBestEscapeRoute(board, myPos);
            if (escapeDir != null) {
                move(escapeDir, board);
                return;
            }
        }

        // Mouvement aléatoire si pas en danger
        moveRandomly(board);
    }

    private void moveRandomly(GameBoard board) {
        Position myPos = getPosition();
        List<Direction> possibleDirections = new ArrayList<>();

        // Collecter toutes les directions possibles et sûres
        for (Direction dir : Direction.values()) {
            Position newPos = myPos.getNeighbor(dir);
            if (board.canMoveTo(newPos) && isSafeMove(board, newPos)) {
                possibleDirections.add(dir);
            }
        }

        // Choisir une direction aléatoire parmi les possibles
        if (!possibleDirections.isEmpty()) {
            Direction randomDir = possibleDirections.get(random.nextInt(possibleDirections.size()));
            move(randomDir, board);
        }
    }

    // === MÉTHODES D'ESQUIVE (gardées du code original) ===

    private boolean isInImmediateDanger(GameBoard board, Position pos) {
        // Explosions actuelles
        for (Explosion explosion : board.getExplosions()) {
            if (explosion.getPositions().contains(pos)) return true;
        }

        // Bombes sur le point d'exploser
        for (Bomb bomb : board.getBombs()) {
            if (bomb.getTimeLeft() <= 1500 && isInBombBlastZone(pos, bomb, board)) {
                return true;
            }
        }

        return false;
    }

    private Direction findBestEscapeRoute(GameBoard board, Position pos) {
        Map<Direction, Integer> escapeScores = new HashMap<>();

        for (Direction dir : Direction.values()) {
            Position newPos = pos.getNeighbor(dir);
            if (board.canMoveTo(newPos)) {
                int score = calculateEscapeScore(board, newPos);
                escapeScores.put(dir, score);
            }
        }

        return escapeScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private int calculateEscapeScore(GameBoard board, Position pos) {
        int score = 100;

        // Pénalité pour chaque danger proche
        for (Bomb bomb : board.getBombs()) {
            if (isInBombBlastZone(pos, bomb, board)) {
                score -= (3000 - bomb.getTimeLeft()) / 10; // Plus c'est proche, plus c'est dangereux
            }
        }

        // Bonus pour les espaces ouverts (plus de liberté de mouvement)
        int openSpaces = 0;
        for (Direction dir : Direction.values()) {
            if (board.canMoveTo(pos.getNeighbor(dir))) {
                openSpaces++;
            }
        }
        score += openSpaces * 20;

        return score;
    }

    private boolean isInBombBlastZone(Position pos, Bomb bomb, GameBoard board) {
        Position bombPos = bomb.getPosition();
        if (pos.equals(bombPos)) return true;

        for (Direction dir : Direction.values()) {
            for (int i = 1; i <= bomb.getExplosionRange(); i++) {
                Position checkPos = new Position(
                        bombPos.getX() + dir.getDeltaX() * i,
                        bombPos.getY() + dir.getDeltaY() * i
                );

                if (checkPos.equals(pos)) return true;
                if (board.hasWall(checkPos) || board.hasDestructibleWall(checkPos)) break;
            }
        }
        return false;
    }

    private boolean isSafeMove(GameBoard board, Position pos) {
        return board.canMoveTo(pos) && !isInImmediateDanger(board, pos);
    }

    private boolean canEscapeAfterBombAt(GameBoard board, Position bombPos) {
        for (Direction dir : Direction.values()) {
            Position escapePos = bombPos.getNeighbor(dir);
            if (board.canMoveTo(escapePos) && !wouldBeInBlastZone(escapePos, bombPos)) {
                return true;
            }
        }
        return false;
    }

    private boolean wouldBeInBlastZone(Position pos, Position bombPos) {
        if (pos.equals(bombPos)) return true;

        for (Direction dir : Direction.values()) {
            for (int i = 1; i <= getExplosionRange(); i++) {
                Position blastPos = new Position(
                        bombPos.getX() + dir.getDeltaX() * i,
                        bombPos.getY() + dir.getDeltaY() * i
                );
                if (blastPos.equals(pos)) return true;
            }
        }
        return false;
    }

    // === LOGIQUE DE POSE DE BOMBES ===
    public boolean wantsToPlaceBomb(GameBoard board, List<Player> allPlayers) {
        if (!canPlaceBomb()) return false;

        long currentTime = System.currentTimeMillis();

        // Poser une bombe toutes les 10 secondes
        if (currentTime - lastBombTime >= BOMB_INTERVAL) {
            Position myPos = getPosition();

            // Vérifier s'il y a un mur destructible ou un ennemi proche
            for (Direction dir : Direction.values()) {
                Position adjacent = myPos.getNeighbor(dir);

                if (board.hasDestructibleWall(adjacent)) {
                    lastBombTime = currentTime;
                    return true;
                }

                for (Player p : allPlayers) {
                    if (p != this && p.isAlive() && p.getPosition().equals(adjacent)) {
                        lastBombTime = currentTime;
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
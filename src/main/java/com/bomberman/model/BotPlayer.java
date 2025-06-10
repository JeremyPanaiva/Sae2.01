package com.bomberman.model;

import com.bomberman.util.Direction;
import com.bomberman.util.Position;
import java.util.*;

public class BotPlayer extends Player {
    private Random random;
    private long lastActionTime;
    private static final long ACTION_DELAY = 150;

    public BotPlayer(int id, Position position, String color) {
        super(id, position, color);
        this.random = new Random();
        this.lastActionTime = 0;
    }

    public void makeMove(GameBoard board, List<Player> allPlayers) {
        if (!isAlive()) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastActionTime < ACTION_DELAY) return;
        lastActionTime = currentTime;

        Position myPos = getPosition();

        // PRIORITÉ 1 : Fuir si on est en danger
        if (isInDanger(board, myPos)) {
            Direction escapeDir = findEscapeDirection(board, myPos);
            if (escapeDir != null) {
                move(escapeDir, board);
            }
            return;
        }

        // PRIORITÉ 2 : Poser une bombe aléatoirement (et intelligemment)
        if (canPlaceBomb() && wantsToPlaceBombRandomly(board, myPos)) {
            // Le GameController doit gérer la pose réelle de la bombe
            return;
        }

        // PRIORITÉ 3 : Mouvement aléatoire ou vers un objectif
        Direction moveDir = chooseMovementDirection(board, allPlayers, myPos);
        if (moveDir != null) {
            move(moveDir, board);
        }
    }

    private boolean wantsToPlaceBombRandomly(GameBoard board, Position myPos) {
        // 10% de chance de poser une bombe, uniquement si on peut s’échapper ensuite
        if (random.nextDouble() < 0.1) {
            return canEscapeAfterBomb(board, myPos);
        }
        return false;
    }


    // Vérifie si on est en danger immédiat
    private boolean isInDanger(GameBoard board, Position pos) {
        // Vérifier les explosions actuelles
        for (Explosion explosion : board.getExplosions()) {
            if (explosion.getPositions().contains(pos)) {
                return true;
            }
        }

        // Vérifier les bombes dangereuses
        for (Bomb bomb : board.getBombs()) {
            if (bomb.getTimeLeft() <= 2000) { // Moins de 2 secondes
                if (isInBombRange(pos, bomb)) {
                    return true;
                }
            }
        }

        return false;
    }


    // Trouve une direction pour échapper au danger
    private Direction findEscapeDirection(GameBoard board, Position pos) {
        List<Direction> safeDirs = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            Position newPos = pos.getNeighbor(dir);
            if (board.canMoveTo(newPos) && !isInDanger(board, newPos)) {
                safeDirs.add(dir);
            }
        }

        if (!safeDirs.isEmpty()) {
            return safeDirs.get(random.nextInt(safeDirs.size()));
        }

        // Si aucune direction sûre, essayer n'importe où
        for (Direction dir : Direction.values()) {
            Position newPos = pos.getNeighbor(dir);
            if (board.canMoveTo(newPos)) {
                return dir;
            }
        }

        return null;
    }

    // Trouve le joueur le plus proche
    private Player findNearestPlayer(List<Player> allPlayers, Position myPos) {
        Player nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (Player player : allPlayers) {
            if (player != this && player.isAlive()) {
                int distance = manhattanDistance(myPos, player.getPosition());
                if (distance < minDistance && distance <= 4) { // Seulement si proche
                    minDistance = distance;
                    nearest = player;
                }
            }
        }

        return nearest;
    }

    // Vérifie si on peut tuer un joueur avec une bombe
    private boolean canKillPlayer(GameBoard board, Position myPos, Position playerPos) {
        if (!canPlaceBomb()) return false;

        // Vérifier si le joueur est dans la ligne d'explosion
        if (!isInExplosionLine(myPos, playerPos)) return false;

        // Vérifier la distance
        int distance = manhattanDistance(myPos, playerPos);
        if (distance > getExplosionRange()) return false;

        // Vérifier qu'il n'y a pas de mur entre nous
        if (hasWallBetween(board, myPos, playerPos)) return false;

        // Vérifier qu'on peut s'échapper après avoir posé la bombe
        return canEscapeAfterBomb(board, myPos);
    }

    // Vérifie si on devrait casser des murs
    private boolean shouldBreakWalls(GameBoard board, Position pos) {
        if (!canPlaceBomb()) return false;

        // Compter les murs destructibles à portée
        int wallCount = 0;
        for (Direction dir : Direction.values()) {
            Position checkPos = pos.getNeighbor(dir);
            if (board.hasDestructibleWall(checkPos)) {
                wallCount++;
            }
        }

        // Poser une bombe si on peut casser au moins 1 mur et qu'on peut s'échapper
        return wallCount > 0 && canEscapeAfterBomb(board, pos);
    }

    // Choisit la direction de mouvement
    private Direction chooseMovementDirection(GameBoard board, List<Player> allPlayers, Position myPos) {
        List<Direction> validDirs = new ArrayList<>();

        // Trouver toutes les directions valides et sûres
        for (Direction dir : Direction.values()) {
            Position newPos = myPos.getNeighbor(dir);
            if (board.canMoveTo(newPos) && !isInDanger(board, newPos)) {
                validDirs.add(dir);
            }
        }

        if (validDirs.isEmpty()) {
            // Si aucune direction sûre, essayer n'importe où
            for (Direction dir : Direction.values()) {
                Position newPos = myPos.getNeighbor(dir);
                if (board.canMoveTo(newPos)) {
                    validDirs.add(dir);
                }
            }
        }

        if (validDirs.isEmpty()) return null ;

        // Préférer se diriger vers un joueur ou des murs destructibles
        Player target = findNearestPlayer(allPlayers, myPos);
        if (target != null) {
            Direction towardsPlayer = getDirectionTowards(myPos, target.getPosition());
            if (validDirs.contains(towardsPlayer)) {
                return towardsPlayer;
            }
        }

        // Sinon, se diriger vers des murs destructibles
        Position nearestWall = findNearestWall(board, myPos);
        if (nearestWall != null) {
            Direction towardsWall = getDirectionTowards(myPos, nearestWall);
            if (validDirs.contains(towardsWall)) {
                return towardsWall;
            }
        }

        // Mouvement aléatoire
        return validDirs.get(random.nextInt(validDirs.size()));
    }

    // Méthodes utilitaires

    private boolean isInBombRange(Position pos, Bomb bomb) {
        Position bombPos = bomb.getPosition();

        // Même position
        if (pos.equals(bombPos)) return true;

        // Vérifier les 4 directions
        for (Direction dir : Direction.values()) {
            for (int i = 1; i <= bomb.getExplosionRange(); i++) {
                Position explodePos = new Position(
                        bombPos.getX() + dir.getDeltaX() * i,
                        bombPos.getY() + dir.getDeltaY() * i
                );

                if (explodePos.equals(pos)) return true;

                // Arrêter si on rencontre un mur
                if (hasWallAt(explodePos)) break;
            }
        }

        return false;
    }

    private boolean isInExplosionLine(Position pos1, Position pos2) {
        return pos1.getX() == pos2.getX() || pos1.getY() == pos2.getY();
    }

    private boolean hasWallBetween(GameBoard board, Position from, Position to) {
        if (from.getX() == to.getX()) {
            // Ligne verticale
            int minY = Math.min(from.getY(), to.getY());
            int maxY = Math.max(from.getY(), to.getY());
            for (int y = minY + 1; y < maxY; y++) {
                if (board.hasWall(new Position(from.getX(), y))) {
                    return true;
                }
            }
        } else if (from.getY() == to.getY()) {
            // Ligne horizontale
            int minX = Math.min(from.getX(), to.getX());
            int maxX = Math.max(from.getX(), to.getX());
            for (int x = minX + 1; x < maxX; x++) {
                if (board.hasWall(new Position(x, from.getY()))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canEscapeAfterBomb(GameBoard board, Position bombPos) {
        // Vérifier s'il y a au moins une direction où on peut aller et être en sécurité
        for (Direction dir : Direction.values()) {
            Position escapePos = bombPos.getNeighbor(dir);
            if (board.canMoveTo(escapePos)) {
                // Vérifier si cette position sera sûre après l'explosion
                boolean safe = true;
                for (int i = 1; i <= getExplosionRange(); i++) {
                    Position explodePos = new Position(
                            bombPos.getX() + dir.getDeltaX() * i,
                            bombPos.getY() + dir.getDeltaY() * i
                    );
                    if (explodePos.equals(escapePos)) {
                        safe = false;
                        break;
                    }
                    if (hasWallAt(explodePos)) break;
                }
                if (safe) return true;
            }
        }
        return false;
    }

    private Position findNearestWall(GameBoard board, Position myPos) {
        Position nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (int x = 0; x < com.bomberman.util.GameConstants.BOARD_WIDTH; x++) {
            for (int y = 0; y < com.bomberman.util.GameConstants.BOARD_HEIGHT; y++) {
                Position wallPos = new Position(x, y);
                if (board.hasDestructibleWall(wallPos)) {
                    int distance = manhattanDistance(myPos, wallPos);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearest = wallPos;
                    }
                }
            }
        }

        return nearest;
    }

    private Direction getDirectionTowards(Position from, Position to) {
        int deltaX = to.getX() - from.getX();
        int deltaY = to.getY() - from.getY();

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            return deltaX > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            return deltaY > 0 ? Direction.DOWN : Direction.UP;
        }
    }

    private int manhattanDistance(Position p1, Position p2) {
        return Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
    }

    private boolean hasWallAt(Position pos) {
        // Cette méthode devrait être implémentée selon votre GameBoard
        // return board.hasWall(pos) || board.hasDestructibleWall(pos);
        return false; // Placeholder
    }

    public boolean wantsToPlaceBomb(GameBoard board, List<Player> allPlayers) {
        Position myPos = getPosition();

        // Placer une bombe si on peut tuer un joueur
        Player target = findNearestPlayer(allPlayers, myPos);
        if (target != null && canKillPlayer(board, myPos, target.getPosition())) {
            return true;
        }

        // Placer une bombe pour casser des murs
        return shouldBreakWalls(board, myPos);
    }
}
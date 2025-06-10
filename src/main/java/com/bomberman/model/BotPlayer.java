package com.bomberman.model;

import com.bomberman.util.Direction;
import com.bomberman.util.Position;
import java.util.*;

public class BotPlayer extends Player {
    private Random random;
    private long lastActionTime;
    private static final long ACTION_DELAY = 120;

    // États du bot
    private enum BotState {
        AGGRESSIVE,     // Chasser les joueurs
        DEFENSIVE,      // Se protéger et collecter power-ups
        WALL_BREAKING,  // Détruire des murs pour les power-ups
        CORNER_TRAP     // Piéger un joueur dans un coin
    }

    private BotState currentState;
    private Position lastTargetPosition;
    private int consecutiveFailedAttacks;

    public BotPlayer(int id, Position position, String color) {
        super(id, position, color);
        this.random = new Random();
        this.lastActionTime = 0;
        this.currentState = BotState.WALL_BREAKING;
        this.consecutiveFailedAttacks = 0;
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

        // Adapter la stratégie selon la situation
        adaptStrategy(board, allPlayers);

    }

    private void adaptStrategy(GameBoard board, List<Player> allPlayers) {
        int aliveEnemies = countAliveEnemies(allPlayers);
        int myBombs = getBombCount();

        // Stratégie agressive si peu d'ennemis ou si on a l'avantage
        if (aliveEnemies <= 2 && myBombs >= 1) { // Réduit le seuil de bombes
            currentState = BotState.AGGRESSIVE;
        }
        // Piéger si on peut acculer quelqu'un
        else if (canTrapEnemy(board, allPlayers)) {
            currentState = BotState.CORNER_TRAP;
        }
        // Sinon casser des murs pour les power-ups
        else {
            currentState = BotState.WALL_BREAKING;
        }
    }

    private void executeAggressiveStrategy(GameBoard board, List<Player> allPlayers, Position myPos) {
        Player target = findBestTarget(allPlayers, myPos);

        if (target != null) {
            Position targetPos = target.getPosition();

            // Si le joueur est dans notre ligne de mire et à bonne distance
            if (canKillPlayerSafely(board, myPos, targetPos)) {
                // Le GameController gérera la pose de bombe via wantsToPlaceBomb()
                return;
            }

            // Sinon se rapprocher du joueur en anticipant ses mouvements
            Direction huntDir = calculateHuntingDirection(board, myPos, targetPos, target);
            if (huntDir != null && isSafeMove(board, myPos.getNeighbor(huntDir))) {
                move(huntDir, board);
                lastTargetPosition = targetPos;
            } else {
                consecutiveFailedAttacks++;
                // Si on n'arrive pas à atteindre, changer de stratégie
                if (consecutiveFailedAttacks > 5) {
                    currentState = BotState.WALL_BREAKING;
                    consecutiveFailedAttacks = 0;
                }
            }
        }
    }

    private void executeDefensiveStrategy(GameBoard board, List<Player> allPlayers, Position myPos) {
        // Chercher les power-ups ou les zones sûres
        Position powerUp = findNearestPowerUp(board, myPos);
        Position safeZone = findSafestPosition(board, allPlayers, myPos);

        Position target = (powerUp != null) ? powerUp : safeZone;

        if (target != null) {
            Direction dir = getSmartDirectionTowards(board, myPos, target);
            if (dir != null && isSafeMove(board, myPos.getNeighbor(dir))) {
                move(dir, board);
            }
        }
    }

    private void executeWallBreakingStrategy(GameBoard board, Position myPos) {
        // Chercher le meilleur endroit pour casser des murs
        Position bestWallCluster = findBestWallCluster(board, myPos);

        if (bestWallCluster != null) {
            if (manhattanDistance(myPos, bestWallCluster) <= 1) {
                // On est à côté du cluster, on peut poser une bombe
                return; // wantsToPlaceBomb() gérera la logique
            } else {
                // Se diriger vers le cluster
                Direction dir = getSmartDirectionTowards(board, myPos, bestWallCluster);
                if (dir != null && isSafeMove(board, myPos.getNeighbor(dir))) {
                    move(dir, board);
                }
            }
        }
    }

    private void executeCornerTrapStrategy(GameBoard board, List<Player> allPlayers, Position myPos) {
        Player victim = findTrappablePlayer(board, allPlayers, myPos);

        if (victim != null) {
            Position trapPos = calculateTrapPosition(board, victim.getPosition());
            if (trapPos != null) {
                Direction dir = getSmartDirectionTowards(board, myPos, trapPos);
                if (dir != null && isSafeMove(board, myPos.getNeighbor(dir))) {
                    move(dir, board);
                }
            }
        }
    }

    // Méthodes de détection et d'analyse

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

    private Player findBestTarget(List<Player> allPlayers, Position myPos) {
        Player bestTarget = null;
        int bestScore = -1;

        for (Player player : allPlayers) {
            if (player != this && player.isAlive()) {
                int score = calculateTargetScore(player, myPos);
                if (score > bestScore) {
                    bestScore = score;
                    bestTarget = player;
                }
            }
        }

        return bestTarget;
    }

    private int calculateTargetScore(Player player, Position myPos) {
        Position playerPos = player.getPosition();
        int distance = manhattanDistance(myPos, playerPos);

        int score = 100;
        score -= distance * 5; // Préférer les joueurs proches

        // Bonus si le joueur est dans une ligne droite
        if (isInStraightLine(myPos, playerPos)) {
            score += 30;
        }

        // Bonus si le joueur semble coincé
        if (isPlayerTrapped(player, myPos)) {
            score += 50;
        }

        return Math.max(0, score);
    }

    private boolean canKillPlayerSafely(GameBoard board, Position myPos, Position targetPos) {
        if (!canPlaceBomb()) return false;
        if (!isInStraightLine(myPos, targetPos)) return false;
        if (manhattanDistance(myPos, targetPos) > getExplosionRange()) return false;
        if (hasObstacleBetween(board, myPos, targetPos)) return false;

        return canEscapeAfterBombAt(board, myPos);
    }

    private Direction calculateHuntingDirection(GameBoard board, Position myPos, Position targetPos, Player target) {
        // Prédire où le joueur va aller
        Position predictedPos = predictPlayerMovement(board, target);

        // Se diriger vers la position prédite
        return getSmartDirectionTowards(board, myPos, predictedPos != null ? predictedPos : targetPos);
    }

    private Position predictPlayerMovement(GameBoard board, Player player) {
        // Logique simple de prédiction basée sur les mouvements précédents
        Position currentPos = player.getPosition();

        // Vérifier dans quelle direction le joueur a le plus d'espace libre
        Direction bestDir = null;
        int maxFreeSpace = 0;

        for (Direction dir : Direction.values()) {
            Position nextPos = currentPos.getNeighbor(dir);
            if (board.canMoveTo(nextPos)) {
                int freeSpace = countFreeSpacesInDirection(board, nextPos, dir);
                if (freeSpace > maxFreeSpace) {
                    maxFreeSpace = freeSpace;
                    bestDir = dir;
                }
            }
        }

        return bestDir != null ? currentPos.getNeighbor(bestDir) : null;
    }

    // Méthodes utilitaires

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

    private boolean isInStraightLine(Position pos1, Position pos2) {
        return pos1.getX() == pos2.getX() || pos1.getY() == pos2.getY();
    }

    private boolean hasObstacleBetween(GameBoard board, Position from, Position to) {
        if (from.getX() == to.getX()) {
            int minY = Math.min(from.getY(), to.getY());
            int maxY = Math.max(from.getY(), to.getY());
            for (int y = minY + 1; y < maxY; y++) {
                Position checkPos = new Position(from.getX(), y);
                if (board.hasWall(checkPos) || board.hasDestructibleWall(checkPos)) {
                    return true;
                }
            }
        } else if (from.getY() == to.getY()) {
            int minX = Math.min(from.getX(), to.getX());
            int maxX = Math.max(from.getX(), to.getX());
            for (int x = minX + 1; x < maxX; x++) {
                Position checkPos = new Position(x, from.getY());
                if (board.hasWall(checkPos) || board.hasDestructibleWall(checkPos)) {
                    return true;
                }
            }
        }
        return false;
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

    private Direction getSmartDirectionTowards(GameBoard board, Position from, Position to) {
        int deltaX = to.getX() - from.getX();
        int deltaY = to.getY() - from.getY();

        // Essayer d'abord la direction principale
        Direction primaryDir = null;
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            primaryDir = deltaX > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            primaryDir = deltaY > 0 ? Direction.DOWN : Direction.UP;
        }

        Position primaryPos = from.getNeighbor(primaryDir);
        if (board.canMoveTo(primaryPos) && isSafeMove(board, primaryPos)) {
            return primaryDir;
        }

        // Essayer la direction secondaire
        Direction secondaryDir = null;
        if (Math.abs(deltaX) <= Math.abs(deltaY)) {
            secondaryDir = deltaX > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            secondaryDir = deltaY > 0 ? Direction.DOWN : Direction.UP;
        }

        Position secondaryPos = from.getNeighbor(secondaryDir);
        if (board.canMoveTo(secondaryPos) && isSafeMove(board, secondaryPos)) {
            return secondaryDir;
        }

        return null;
    }

    private int manhattanDistance(Position p1, Position p2) {
        return Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
    }

    private int countAliveEnemies(List<Player> allPlayers) {
        int count = 0;
        for (Player player : allPlayers) {
            if (player != this && player.isAlive()) {
                count++;
            }
        }
        return count;
    }

    // Méthodes placeholder pour les fonctionnalités avancées
    private boolean canTrapEnemy(GameBoard board, List<Player> allPlayers) {
        return findTrappablePlayer(board, allPlayers, getPosition()) != null;
    }

    private Player findTrappablePlayer(GameBoard board, List<Player> allPlayers, Position myPos) {
        // Implémentation simplifiée - cherche un joueur proche
        for (Player player : allPlayers) {
            if (player != this && player.isAlive() &&
                    manhattanDistance(myPos, player.getPosition()) <= 3) {
                return player;
            }
        }
        return null;
    }

    private Position calculateTrapPosition(GameBoard board, Position playerPos) {
        // Retourner une position à côté du joueur
        for (Direction dir : Direction.values()) {
            Position trapPos = playerPos.getNeighbor(dir);
            if (board.canMoveTo(trapPos)) {
                return trapPos;
            }
        }
        return null;
    }

    private Position findNearestPowerUp(GameBoard board, Position myPos) {
        // Placeholder - à implémenter selon votre système de power-ups
        return null;
    }

    private Position findSafestPosition(GameBoard board, List<Player> allPlayers, Position myPos) {
        // Chercher une position éloignée des autres joueurs
        Position safest = null;
        int maxDistance = 0;

        for (int x = 0; x < com.bomberman.util.GameConstants.BOARD_WIDTH; x++) {
            for (int y = 0; y < com.bomberman.util.GameConstants.BOARD_HEIGHT; y++) {
                Position pos = new Position(x, y);
                if (board.canMoveTo(pos)) {
                    int minDistanceToEnemy = Integer.MAX_VALUE;
                    for (Player player : allPlayers) {
                        if (player != this && player.isAlive()) {
                            int dist = manhattanDistance(pos, player.getPosition());
                            minDistanceToEnemy = Math.min(minDistanceToEnemy, dist);
                        }
                    }
                    if (minDistanceToEnemy > maxDistance) {
                        maxDistance = minDistanceToEnemy;
                        safest = pos;
                    }
                }
            }
        }
        return safest;
    }

    private Position findBestWallCluster(GameBoard board, Position myPos) {
        // Chercher le groupe de murs destructibles le plus intéressant
        Position bestCluster = null;
        int bestScore = 0;

        for (int x = 0; x < com.bomberman.util.GameConstants.BOARD_WIDTH; x++) {
            for (int y = 0; y < com.bomberman.util.GameConstants.BOARD_HEIGHT; y++) {
                Position pos = new Position(x, y);
                if (board.hasDestructibleWall(pos)) {
                    int score = countNearbyWalls(board, pos) - manhattanDistance(myPos, pos);
                    if (score > bestScore) {
                        bestScore = score;
                        bestCluster = pos;
                    }
                }
            }
        }
        return bestCluster;
    }

    private int countNearbyWalls(GameBoard board, Position center) {
        int count = 0;
        for (Direction dir : Direction.values()) {
            Position nearby = center.getNeighbor(dir);
            if (board.hasDestructibleWall(nearby)) {
                count++;
            }
        }
        return count;
    }

    private boolean isPlayerTrapped(Player player, Position myPos) {
        Position playerPos = player.getPosition();
        int freeDirections = 0;

        // Compter les directions libres pour le joueur


        return freeDirections <= 2;
    }

    private int countFreeSpacesInDirection(GameBoard board, Position start, Direction dir) {
        int count = 0;
        Position current = start;

        for (int i = 0; i < 5; i++) {
            current = current.getNeighbor(dir);
            if (board.canMoveTo(current)) {
                count++;
            } else {
                break;
            }
        }

        return count;
    }

    // Méthode principale pour déterminer si le bot veut poser une bombe
    public boolean wantsToPlaceBomb(GameBoard board, List<Player> allPlayers) {
        if (!canPlaceBomb()) return false;

        Position myPos = getPosition();

        // Vérifier d'abord qu'on peut s'échapper
        if (!canEscapeAfterBombAt(board, myPos)) {
            return false;
        }

        switch (currentState) {
            case AGGRESSIVE:
                // Poser une bombe si on peut tuer un joueur
                Player target = findBestTarget(allPlayers, myPos);
                if (target != null) {
                    Position targetPos = target.getPosition();
                    int distance = manhattanDistance(myPos, targetPos);
                    // Conditions plus permissives
                    if (distance <= getExplosionRange() &&
                            (isInStraightLine(myPos, targetPos) || distance <= 2)) {
                        return true;
                    }
                }
                break;

            case CORNER_TRAP:
                // Poser une bombe pour piéger
                Player victim = findTrappablePlayer(board, allPlayers, myPos);
                if (victim != null) {
                    int distance = manhattanDistance(myPos, victim.getPosition());
                    if (distance <= getExplosionRange() && distance >= 1) {
                        return true;
                    }
                }
                break;

            case WALL_BREAKING:
                // Poser une bombe si on peut casser des murs
                int wallsNearby = countNearbyWalls(board, myPos);
                if (wallsNearby > 0) {
                    return true;
                }
                break;

            case DEFENSIVE:
                // Poser une bombe si on est acculé
                Player nearestEnemy = findBestTarget(allPlayers, myPos);
                if (nearestEnemy != null) {
                    int distance = manhattanDistance(myPos, nearestEnemy.getPosition());
                    if (distance <= 2) { // Ennemi très proche
                        return true;
                    }
                }
                break;
        }

        // Logique de secours : poser une bombe occasionnellement
        if (random.nextInt(100) < 5) { // 5% de chance
            return true;
        }

        return false;
    }
}
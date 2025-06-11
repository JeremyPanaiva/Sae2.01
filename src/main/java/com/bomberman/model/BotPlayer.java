package com.bomberman.model;

import com.bomberman.util.Direction;
import com.bomberman.util.Position;

import java.util.*;

/**
 * Représente un joueur contrôlé par l'ordinateur dans le jeu Bomberman.
 * Le bot prend des décisions automatiques pour se déplacer et poser des bombes.
 */
public class BotPlayer extends Player {
    private Random random; // Générateur de nombres aléatoires pour les mouvements
    private long lastActionTime; // Dernière fois que le bot a effectué une action
    private long lastBombTime; // Dernière fois que le bot a posé une bombe
    private static final long ACTION_DELAY = 120; // Délai entre les actions du bot
    private static final long BOMB_INTERVAL = 10000; // Intervalle de pose de bombe en millisecondes

    /**
     * Constructeur pour créer un nouveau joueur bot.
     *
     * @param id L'identifiant du joueur.
     * @param position La position initiale du joueur.
     * @param color La couleur du joueur.
     */
    public BotPlayer(int id, Position position, String color) {
        super(id, position, color);
        this.random = new Random();
        this.lastActionTime = 0;
        this.lastBombTime = 0;
    }

    /**
     * Effectue un mouvement pour le bot en fonction de l'état du plateau de jeu.
     *
     * @param board Le plateau de jeu actuel.
     * @param allPlayers La liste de tous les joueurs.
     */
    public void makeMove(GameBoard board, List<Player> allPlayers) {
        if (!isAlive()) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastActionTime < ACTION_DELAY) return;
        lastActionTime = currentTime;

        Position myPos = getPosition();

        // Priorité absolue : survivre aux explosions
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

    /**
     * Déplace le bot de manière aléatoire sur le plateau.
     *
     * @param board Le plateau de jeu actuel.
     */
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

    // Méthodes d'esquive

    /**
     * Vérifie si le bot est en danger immédiat d'une explosion.
     *
     * @param board Le plateau de jeu.
     * @param pos La position actuelle du bot.
     * @return true si le bot est en danger immédiat, false sinon.
     */
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

    /**
     * Trouve la meilleure direction pour échapper au danger.
     *
     * @param board Le plateau de jeu.
     * @param pos La position actuelle du bot.
     * @return La direction optimale pour échapper au danger.
     */
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

    /**
     * Calcule un score pour une position d'échappement potentielle.
     *
     * @param board Le plateau de jeu.
     * @param pos La position à évaluer.
     * @return Le score de la position.
     */
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

    /**
     * Vérifie si une position est dans la zone d'explosion d'une bombe.
     *
     * @param pos La position à vérifier.
     * @param bomb La bombe à considérer.
     * @param board Le plateau de jeu.
     * @return true si la position est dans la zone d'explosion, false sinon.
     */
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

    /**
     * Vérifie si un mouvement vers une position est sûr.
     *
     * @param board Le plateau de jeu.
     * @param pos La position à vérifier.
     * @return true si le mouvement est sûr, false sinon.
     */
    private boolean isSafeMove(GameBoard board, Position pos) {
        return board.canMoveTo(pos) && !isInImmediateDanger(board, pos);
    }

    /**
     * Vérifie si le bot peut s'échapper après avoir posé une bombe à une position donnée.
     *
     * @param board Le plateau de jeu.
     * @param bombPos La position de la bombe.
     * @return true si le bot peut s'échapper, false sinon.
     */
    private boolean canEscapeAfterBombAt(GameBoard board, Position bombPos) {
        for (Direction dir : Direction.values()) {
            Position escapePos = bombPos.getNeighbor(dir);
            if (board.canMoveTo(escapePos) && !wouldBeInBlastZone(escapePos, bombPos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si une position serait dans la zone d'explosion d'une bombe.
     *
     * @param pos La position à vérifier.
     * @param bombPos La position de la bombe.
     * @return true si la position serait dans la zone d'explosion, false sinon.
     */
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

    /**
     * Détermine si le bot veut poser une bombe.
     *
     * @param board Le plateau de jeu.
     * @param allPlayers La liste de tous les joueurs.
     * @return true si le bot veut poser une bombe, false sinon.
     */
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

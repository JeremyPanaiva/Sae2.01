package com.bomberman.model;

import com.bomberman.util.Position;
import com.bomberman.util.GameConstants;

import java.util.*;

/**
 * Représente le plateau de jeu pour le jeu Bomberman.
 * Gère les murs, les bombes, les bonus et les explosions sur le plateau.
 */
public class GameBoard {
    private Wall[][] walls; // Tableau de murs sur le plateau
    private Map<Position, Bomb> bombs; // Map des bombes placées sur le plateau
    private Map<Position, PowerUp> powerUps; // Map des bonus sur le plateau
    private List<Explosion> explosions; // Liste des explosions sur le plateau

    /**
     * Constructeur pour initialiser un nouveau plateau de jeu.
     */
    public GameBoard() {
        walls = new Wall[GameConstants.BOARD_WIDTH][GameConstants.BOARD_HEIGHT];
        bombs = new HashMap<>();
        powerUps = new HashMap<>();
        explosions = new ArrayList<>();
        initializeWalls();
    }

    /**
     * Initialise les murs sur le plateau.
     * Place des murs indestructibles sur les bords et en damier, et des murs destructibles aléatoirement.
     */
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

    /**
     * Vérifie si une position est proche d'une position de départ des joueurs.
     *
     * @param x La coordonnée x de la position à vérifier.
     * @param y La coordonnée y de la position à vérifier.
     * @return true si la position est proche d'une position de départ, false sinon.
     */
    private boolean isNearStartPosition(int x, int y) {
        Position[] startPositions = {
                new Position(1, 1),
                new Position(GameConstants.BOARD_WIDTH - 2, 1),
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

    /**
     * Vérifie si un joueur peut se déplacer vers une position donnée.
     *
     * @param position La position à vérifier.
     * @return true si le joueur peut se déplacer vers cette position, false sinon.
     */
    public boolean canMoveTo(Position position) {
        if (!isValidPosition(position)) return false;
        if (hasWall(position)) return false;
        if (hasBomb(position)) return false;
        return true;
    }

    /**
     * Vérifie si une position est valide sur le plateau.
     *
     * @param position La position à vérifier.
     * @return true si la position est valide, false sinon.
     */
    public boolean isValidPosition(Position position) {
        int x = position.getX();
        int y = position.getY();
        return x >= 0 && x < GameConstants.BOARD_WIDTH &&
                y >= 0 && y < GameConstants.BOARD_HEIGHT;
    }

    /**
     * Vérifie s'il y a un mur à une position donnée.
     *
     * @param position La position à vérifier.
     * @return true s'il y a un mur, false sinon.
     */
    public boolean hasWall(Position position) {
        if (!isValidPosition(position)) return true;
        return walls[position.getX()][position.getY()] != null;
    }

    /**
     * Vérifie s'il y a un mur destructible à une position donnée.
     *
     * @param position La position à vérifier.
     * @return true s'il y a un mur destructible, false sinon.
     */
    public boolean hasDestructibleWall(Position position) {
        if (!hasWall(position)) return false;
        Wall wall = walls[position.getX()][position.getY()];
        return wall.isDestructible();
    }

    /**
     * Détruit un mur à une position donnée et peut laisser un bonus.
     *
     * @param position La position du mur à détruire.
     */
    public void destroyWall(Position position) {
        if (hasDestructibleWall(position)) {
            // Chance de laisser un bonus
            if (new Random().nextDouble() < 0.3) {
                PowerUp.Type[] types = PowerUp.Type.values();
                PowerUp.Type randomType = types[new Random().nextInt(types.length)];
                powerUps.put(position, new PowerUp(position, randomType));
            }
            walls[position.getX()][position.getY()] = null;
        }
    }

    /**
     * Vérifie s'il y a une bombe à une position donnée.
     *
     * @param position La position à vérifier.
     * @return true s'il y a une bombe, false sinon.
     */
    public boolean hasBomb(Position position) {
        return bombs.containsKey(position);
    }

    /**
     * Place une bombe sur le plateau.
     *
     * @param bomb La bombe à placer.
     */
    public void placeBomb(Bomb bomb) {
        bombs.put(bomb.getPosition(), bomb);
    }

    /**
     * Retire une bombe du plateau.
     *
     * @param position La position de la bombe à retirer.
     */
    public void removeBomb(Position position) {
        bombs.remove(position);
    }

    /**
     * Récupère un bonus à une position donnée.
     *
     * @param position La position du bonus à récupérer.
     * @return Le bonus à cette position, ou null s'il n'y a pas de bonus.
     */
    public PowerUp getPowerUp(Position position) {
        return powerUps.remove(position);
    }

    /**
     * Ajoute une explosion au plateau.
     *
     * @param explosion L'explosion à ajouter.
     */
    public void addExplosion(Explosion explosion) {
        explosions.add(explosion);
    }

    /**
     * Retire les explosions terminées du plateau.
     */
    public void removeFinishedExplosions() {
        explosions.removeIf(Explosion::isFinished);
    }

    /**
     * Retourne le mur à une position donnée.
     *
     * @param position La position du mur à récupérer.
     * @return Le mur à cette position, ou null s'il n'y a pas de mur.
     */
    public Wall getWall(Position position) {
        if (!isValidPosition(position)) return null;
        return walls[position.getX()][position.getY()];
    }

    /**
     * Retourne une collection de toutes les bombes sur le plateau.
     *
     * @return Une collection de bombes.
     */
    public Collection<Bomb> getBombs() {
        return new ArrayList<>(bombs.values());
    }

    /**
     * Retourne une collection de tous les bonus sur le plateau.
     *
     * @return Une collection de bonus.
     */
    public Collection<PowerUp> getPowerUps() {
        return new ArrayList<>(powerUps.values());
    }

    /**
     * Retourne une liste de toutes les explosions sur le plateau.
     *
     * @return Une liste d'explosions.
     */
    public List<Explosion> getExplosions() {
        return new ArrayList<>(explosions);
    }
}

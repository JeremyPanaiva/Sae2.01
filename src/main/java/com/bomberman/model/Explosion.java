package com.bomberman.model;

import com.bomberman.util.Position;
import com.bomberman.util.GameConstants;

import java.util.*;

/**
 * Représente une explosion dans le jeu Bomberman.
 * Gère les positions et les types d'explosion, ainsi que leur durée.
 */
public class Explosion {

    /**
     * Types d'explosion possibles.
     */
    public enum ExplosionType {
        CENTER, // Centre de l'explosion
        HORIZONTAL, // Partie horizontale de l'explosion
        VERTICAL, // Partie verticale de l'explosion
        END // Fin de l'explosion
    }

    private Map<Position, ExplosionType> explosionTypes; // Types d'explosion associés à chaque position
    private long startTime; // Temps de début de l'explosion
    private Position centerPosition; // Position centrale de l'explosion

    /**
     * Constructeur pour créer une nouvelle explosion.
     *
     * @param positions Liste des positions affectées par l'explosion.
     * @param center Position centrale de l'explosion.
     */
    public Explosion(List<Position> positions, Position center) {
        this.explosionTypes = new HashMap<>();
        this.startTime = System.currentTimeMillis();
        this.centerPosition = center;

        categorizeExplosionPositions(positions, center);
    }

    /**
     * Catégorise les positions de l'explosion en différents types.
     *
     * @param positions Liste des positions à catégoriser.
     * @param center Position centrale de l'explosion.
     */
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

    /**
     * Vérifie si l'explosion est terminée.
     *
     * @return true si l'explosion est terminée, false sinon.
     */
    public boolean isFinished() {
        return System.currentTimeMillis() - startTime >= GameConstants.EXPLOSION_DURATION;
    }

    /**
     * Vérifie si l'explosion est sur le point de se terminer.
     *
     * @return true si l'explosion est sur le point de se terminer, false sinon.
     */
    public boolean isAboutToFinish() {
        long elapsed = System.currentTimeMillis() - startTime;
        return elapsed >= GameConstants.EXPLOSION_DURATION - 200; // 200ms avant la fin
    }

    /**
     * Retourne la liste des positions affectées par l'explosion.
     *
     * @return Liste des positions.
     */
    public List<Position> getPositions() {
        return new ArrayList<>(explosionTypes.keySet());
    }

    /**
     * Retourne le type d'explosion à une position donnée.
     *
     * @param position La position à vérifier.
     * @return Le type d'explosion à cette position.
     */
    public ExplosionType getExplosionType(Position position) {
        return explosionTypes.getOrDefault(position, ExplosionType.END);
    }

    /**
     * Retourne une carte de tous les types d'explosion avec leurs positions.
     *
     * @return Carte des types d'explosion.
     */
    public Map<Position, ExplosionType> getAllExplosionTypes() {
        return new HashMap<>(explosionTypes);
    }

    /**
     * Vérifie si une position est affectée par l'explosion.
     *
     * @param position La position à vérifier.
     * @return true si la position est affectée, false sinon.
     */
    public boolean contains(Position position) {
        return explosionTypes.containsKey(position);
    }

    /**
     * Retourne la position centrale de l'explosion.
     *
     * @return La position centrale.
     */
    public Position getCenterPosition() {
        return new Position(centerPosition);
    }
}

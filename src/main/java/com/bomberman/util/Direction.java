package com.bomberman.util;

/**
 * Énumération représentant les directions possibles dans le jeu.
 * Chaque direction est associée à un changement de coordonnées (deltaX, deltaY).
 */
public enum Direction {

    /** Direction vers le haut, décrémente la coordonnée Y. */
    UP(0, -1),

    /** Direction vers le bas, incrémente la coordonnée Y. */
    DOWN(0, 1),

    /** Direction vers la gauche, décrémente la coordonnée X. */
    LEFT(-1, 0),

    /** Direction vers la droite, incrémente la coordonnée X. */
    RIGHT(1, 0);

    private final int deltaX; // Changement de la coordonnée X
    private final int deltaY; // Changement de la coordonnée Y

    /**
     * Constructeur pour initialiser une direction avec des deltas pour X et Y.
     *
     * @param deltaX Le changement de la coordonnée X.
     * @param deltaY Le changement de la coordonnée Y.
     */
    Direction(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    /**
     * Retourne le changement de la coordonnée X pour cette direction.
     *
     * @return Le delta X.
     */
    public int getDeltaX() {
        return deltaX;
    }

    /**
     * Retourne le changement de la coordonnée Y pour cette direction.
     *
     * @return Le delta Y.
     */
    public int getDeltaY() {
        return deltaY;
    }
}

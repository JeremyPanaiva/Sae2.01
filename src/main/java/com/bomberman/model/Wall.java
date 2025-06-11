package com.bomberman.model;

import com.bomberman.util.Position;

/**
 * Représente un mur dans le jeu Bomberman.
 * Un mur peut être destructible ou indestructible.
 */
public class Wall {

    private Position position; // Position du mur sur le plateau de jeu
    private boolean destructible; // Indique si le mur est destructible

    /**
     * Constructeur pour créer un nouveau mur.
     *
     * @param position La position du mur sur le plateau de jeu.
     * @param destructible Indique si le mur est destructible.
     */
    public Wall(Position position, boolean destructible) {
        this.position = new Position(position);
        this.destructible = destructible;
    }

    /**
     * Retourne la position du mur.
     *
     * @return La position du mur.
     */
    public Position getPosition() {
        return new Position(position);
    }

    /**
     * Vérifie si le mur est destructible.
     *
     * @return true si le mur est destructible, false sinon.
     */
    public boolean isDestructible() {
        return destructible;
    }
}

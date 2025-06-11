package com.bomberman.model;

import com.bomberman.util.Direction;
import com.bomberman.util.Position;

/**
 * Représente un joueur dans le jeu Bomberman.
 * Gère les propriétés et les actions d'un joueur, comme le placement de bombes et le mouvement.
 */
public class Player {
    private int id; // Identifiant unique du joueur
    private Position position; // Position actuelle du joueur sur le plateau
    private boolean alive; // Indique si le joueur est en vie
    private int bombCount; // Nombre de bombes actuellement placées par le joueur
    private int maxBombs; // Nombre maximum de bombes que le joueur peut placer
    private int explosionRange; // Portée des explosions des bombes du joueur
    private String color; // Couleur du joueur

    /**
     * Constructeur pour créer un nouveau joueur.
     *
     * @param id L'identifiant unique du joueur.
     * @param position La position initiale du joueur sur le plateau.
     * @param color La couleur du joueur.
     */
    public Player(int id, Position position, String color) {
        this.id = id;
        this.position = new Position(position);
        this.alive = true;
        this.bombCount = 0;
        this.maxBombs = 1;
        this.explosionRange = 2;
        this.color = color;
    }

    /**
     * Vérifie si le joueur peut placer une bombe.
     *
     * @return true si le joueur peut placer une bombe, false sinon.
     */
    public boolean canPlaceBomb() {
        return bombCount < maxBombs;
    }

    /**
     * Place une bombe pour le joueur.
     */
    public void placeBomb() {
        if (canPlaceBomb()) {
            bombCount++;
        }
    }

    /**
     * Indique qu'une bombe du joueur a explosé.
     */
    public void bombExploded() {
        if (bombCount > 0) {
            bombCount--;
        }
    }

    /**
     * Déplace le joueur dans une direction donnée si le mouvement est valide.
     *
     * @param direction La direction dans laquelle déplacer le joueur.
     * @param board Le plateau de jeu pour vérifier la validité du mouvement.
     */
    public void move(Direction direction, GameBoard board) {
        if (!alive) return;

        Position newPosition = position.getNeighbor(direction);
        if (board.canMoveTo(newPosition)) {
            position = newPosition;
        }
    }

    /**
     * Tue le joueur.
     */
    public void kill() {
        this.alive = false;
    }

    /**
     * Retourne l'identifiant du joueur.
     *
     * @return L'identifiant du joueur.
     */
    public int getId() {
        return id;
    }

    /**
     * Retourne la position actuelle du joueur.
     *
     * @return La position du joueur.
     */
    public Position getPosition() {
        return new Position(position);
    }

    /**
     * Vérifie si le joueur est en vie.
     *
     * @return true si le joueur est en vie, false sinon.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Retourne le nombre de bombes actuellement placées par le joueur.
     *
     * @return Le nombre de bombes placées.
     */
    public int getBombCount() {
        return bombCount;
    }

    /**
     * Retourne le nombre maximum de bombes que le joueur peut placer.
     *
     * @return Le nombre maximum de bombes.
     */
    public int getMaxBombs() {
        return maxBombs;
    }

    /**
     * Retourne la portée des explosions des bombes du joueur.
     *
     * @return La portée des explosions.
     */
    public int getExplosionRange() {
        return explosionRange;
    }

    /**
     * Retourne la couleur du joueur.
     *
     * @return La couleur du joueur.
     */
    public String getColor() {
        return color;
    }

    /**
     * Définit le nombre maximum de bombes que le joueur peut placer.
     *
     * @param maxBombs Le nouveau nombre maximum de bombes.
     */
    public void setMaxBombs(int maxBombs) {
        this.maxBombs = maxBombs;
    }

    /**
     * Définit la portée des explosions des bombes du joueur.
     *
     * @param explosionRange La nouvelle portée des explosions.
     */
    public void setExplosionRange(int explosionRange) {
        this.explosionRange = explosionRange;
    }
}

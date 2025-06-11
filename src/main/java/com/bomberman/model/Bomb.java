package com.bomberman.model;

import com.bomberman.util.Position;
import com.bomberman.util.GameConstants;

/**
 * Représente une bombe dans le jeu Bomberman.
 * Gère les propriétés et le comportement d'une bombe, y compris son explosion.
 */
public class Bomb {
    private Position position; // Position de la bombe sur le plateau de jeu
    private Player owner; // Joueur ayant placé la bombe
    private long placedTime; // Temps auquel la bombe a été placée
    private int explosionRange; // Portée de l'explosion de la bombe

    /**
     * Constructeur pour créer une nouvelle bombe.
     *
     * @param position La position où la bombe est placée.
     * @param owner Le joueur qui a placé la bombe.
     */
    public Bomb(Position position, Player owner) {
        this.position = new Position(position);
        this.owner = owner;
        this.placedTime = System.currentTimeMillis();
        this.explosionRange = owner.getExplosionRange();
    }

    /**
     * Vérifie si la bombe doit exploser en fonction du temps écoulé.
     *
     * @return true si la bombe doit exploser, false sinon.
     */
    public boolean shouldExplode() {
        return System.currentTimeMillis() - placedTime >= GameConstants.BOMB_TIMER;
    }

    /**
     * Retourne la position de la bombe.
     *
     * @return La position de la bombe.
     */
    public Position getPosition() {
        return new Position(position);
    }

    /**
     * Retourne le joueur propriétaire de la bombe.
     *
     * @return Le joueur propriétaire de la bombe.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Retourne la portée de l'explosion de la bombe.
     *
     * @return La portée de l'explosion.
     */
    public int getExplosionRange() {
        return explosionRange;
    }

    /**
     * Retourne le temps restant avant l'explosion de la bombe.
     *
     * @return Le temps restant en millisecondes.
     */
    public long getTimeLeft() {
        return Math.max(0, GameConstants.BOMB_TIMER - (System.currentTimeMillis() - placedTime));
    }
}

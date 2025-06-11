package com.bomberman.model;

import com.bomberman.util.Position;

/**
 * Représente un bonus dans le jeu Bomberman.
 * Les bonus peuvent améliorer les capacités d'un joueur, comme augmenter le nombre de bombes ou la portée des explosions.
 */
public class PowerUp {

    /**
     * Types de bonus disponibles.
     */
    public enum Type {
        EXTRA_BOMB, // Bonus pour augmenter le nombre de bombes qu'un joueur peut poser
        BIGGER_EXPLOSION // Bonus pour augmenter la portée des explosions des bombes
    }

    private Position position; // Position du bonus sur le plateau
    private Type type; // Type du bonus

    /**
     * Constructeur pour créer un nouveau bonus.
     *
     * @param position La position du bonus sur le plateau.
     * @param type Le type du bonus.
     */
    public PowerUp(Position position, Type type) {
        this.position = new Position(position);
        this.type = type;
    }

    /**
     * Applique l'effet du bonus à un joueur.
     *
     * @param player Le joueur à qui appliquer le bonus.
     */
    public void applyTo(Player player) {
        switch (type) {
            case EXTRA_BOMB:
                player.setMaxBombs(player.getMaxBombs() + 1);
                break;
            case BIGGER_EXPLOSION:
                player.setExplosionRange(player.getExplosionRange() + 1);
                break;
        }
    }

    /**
     * Retourne la position du bonus.
     *
     * @return La position du bonus.
     */
    public Position getPosition() {
        return new Position(position);
    }

    /**
     * Retourne le type du bonus.
     *
     * @return Le type du bonus.
     */
    public Type getType() {
        return type;
    }
}

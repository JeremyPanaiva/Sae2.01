package com.bomberman.util;

/**
 * Représente une position dans le jeu Bomberman.
 * Cette classe gère les coordonnées (x, y) et permet de manipuler les positions sur le plateau de jeu.
 */
public class Position {
    private int x; // Coordonnée x de la position
    private int y; // Coordonnée y de la position

    /**
     * Constructeur pour créer une nouvelle position avec des coordonnées spécifiées.
     *
     * @param x La coordonnée x de la position.
     * @param y La coordonnée y de la position.
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructeur pour créer une nouvelle position en copiant une autre position.
     *
     * @param other La position à copier.
     */
    public Position(Position other) {
        this.x = other.x;
        this.y = other.y;
    }

    /**
     * Retourne la coordonnée x de la position.
     *
     * @return La coordonnée x.
     */
    public int getX() {
        return x;
    }

    /**
     * Retourne la coordonnée y de la position.
     *
     * @return La coordonnée y.
     */
    public int getY() {
        return y;
    }

    /**
     * Définit la coordonnée x de la position.
     *
     * @param x La nouvelle coordonnée x.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Définit la coordonnée y de la position.
     *
     * @param y La nouvelle coordonnée y.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Déplace la position dans une direction donnée.
     *
     * @param direction La direction dans laquelle déplacer la position.
     */
    public void move(Direction direction) {
        this.x += direction.getDeltaX();
        this.y += direction.getDeltaY();
    }

    /**
     * Retourne une nouvelle position voisine dans une direction donnée.
     *
     * @param direction La direction de la position voisine.
     * @return La nouvelle position voisine.
     */
    public Position getNeighbor(Direction direction) {
        return new Position(x + direction.getDeltaX(), y + direction.getDeltaY());
    }

    /**
     * Vérifie si cette position est égale à un autre objet.
     *
     * @param obj L'objet à comparer.
     * @return true si les objets sont égaux, false sinon.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }

    /**
     * Retourne un code de hachage pour cette position.
     *
     * @return Le code de hachage.
     */
    @Override
    public int hashCode() {
        return x * 31 + y;
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères de cette position.
     *
     * @return La représentation sous forme de chaîne de caractères.
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}

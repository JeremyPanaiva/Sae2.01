package com.bomberman.model;

/**
 * Représente un pack de textures dans le jeu.
 * Stocke les informations relatives à un pack de textures, comme son nom et son chemin d'accès.
 */
public class Texture {

    // Stocke le nom du pack de texture
    private String nom;

    // Stocke le chemin du pack de texture
    private String path;

    /**
     * Constructeur pour créer un nouveau pack de textures.
     *
     * @param nom Le nom du pack de textures.
     * @param path Le chemin d'accès au pack de textures.
     */
    public Texture(String nom, String path) {
        this.nom = nom;
        this.path = path;
    }

    /**
     * Retourne le nom du pack de textures.
     *
     * @return Le nom du pack de textures.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Retourne le chemin d'accès au pack de textures.
     *
     * @return Le chemin d'accès au pack de textures.
     */
    public String getPath() {
        return path;
    }

    /**
     * Retourne une représentation sous forme de chaîne de caractères du pack de textures.
     *
     * @return Le nom du pack de textures.
     */
    @Override
    public String toString() {
        return nom;
    }
}

package com.bomberman.model;

public class Texture {
    //stocker le nom du pack de texture
    private String nom;
    //stocker le chemin du pack de texture
    private String path;


    //constructeur
    public Texture(String nom, String path) {
        this.nom = nom;
        this.path = path;
    }

    //renvoie le nom du pack de texture pour le controller
    public String getNom() {
        return nom;
    }

    //renvoie le chemin du pack de texture
    public String getPath() {
        return path;
    }

    //renvoie le nom du pack de ressource en string pour l'affichage
    public String toString() {
        return nom;
    }
}

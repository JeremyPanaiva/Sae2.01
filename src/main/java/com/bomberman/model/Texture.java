package com.bomberman.model;

public class Texture {
    private String nom;
    private String path;

    public Texture(String nom, String path) {
        this.nom = nom;
        this.path = path;
    }

    public String getNom() {
        return nom;
    }

    public String getPath() {
        return path;
    }

    public String toString() {
        return nom;
    }
}

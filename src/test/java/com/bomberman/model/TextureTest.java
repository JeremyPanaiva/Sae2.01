package com.bomberman.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextureTest {

    @Test
    void getNom() {
        Texture texture = new Texture("mur", "/textures/mur.png");
        assertEquals("mur", texture.getNom(), "Le nom doit être 'mur'");
    }

    @Test
    void getPath() {
        Texture texture = new Texture("mur", "/textures/mur.png");
        assertEquals("/textures/mur.png", texture.getPath(), "Le chemin doit être '/textures/mur.png'");
    }

    @Test
    void testToString() {
        Texture texture = new Texture("sol", "/textures/sol.png");
        assertEquals("sol", texture.toString(), "toString doit retourner le nom");
    }
}

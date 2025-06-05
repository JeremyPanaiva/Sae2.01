package com.bomberman.util;

public class GameConstants {
    public static final int BOARD_WIDTH = 15;
    public static final int BOARD_HEIGHT = 13;
    public static final int CELL_SIZE = 40;
    public static final int BOMB_TIMER = 3000; // 3 secondes
    public static final int EXPLOSION_DURATION = 1000; // 1 seconde
    public static final int EXPLOSION_RANGE = 2;
    public static final int MAX_PLAYERS = 4;

    // Couleurs des joueurs
    public static final String[] PLAYER_COLORS = {
            "#FF0000", // Rouge
            "#0000FF", // Bleu
            "#00FF00", // Vert
            "#FFFF00"  // Jaune
    };
}
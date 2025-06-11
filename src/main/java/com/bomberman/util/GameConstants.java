package com.bomberman.util;

/**
 * Classe contenant les constantes utilisées dans le jeu Bomberman.
 * Ces constantes définissent divers paramètres du jeu, tels que la taille du plateau, les timers, etc.
 */
public class GameConstants {

    /** Largeur du plateau de jeu en nombre de cellules. */
    public static final int BOARD_WIDTH = 15;

    /** Hauteur du plateau de jeu en nombre de cellules. */
    public static final int BOARD_HEIGHT = 13;

    /** Taille d'une cellule du plateau en pixels. */
    public static final int CELL_SIZE = 40;

    /** Temps en millisecondes avant qu'une bombe n'explose. */
    public static final int BOMB_TIMER = 3000; // 3 secondes

    /** Durée en millisecondes pendant laquelle une explosion est visible. */
    public static final int EXPLOSION_DURATION = 1000; // 1 seconde

    /** Portée initiale des explosions des bombes. */
    public static final int EXPLOSION_RANGE = 2;

    /** Nombre maximum de joueurs autorisés dans le jeu. */
    public static final int MAX_PLAYERS = 4;

    /** Couleurs associées à chaque joueur. */
    public static final String[] PLAYER_COLORS = {
            "#FF0000", // Rouge
            "#0000FF", // Bleu
            "#00FF00", // Vert
            "#FFFF00"  // Jaune
    };
}

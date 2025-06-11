package com.bomberman.view;

import com.bomberman.controller.AvatarController;
import com.bomberman.model.*;
import com.bomberman.util.GameConstants;
import com.bomberman.util.Position;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * Classe responsable de l'affichage du jeu Bomberman.
 * Gère le rendu graphique du jeu, y compris les joueurs, les murs, les bombes, les explosions et les bonus.
 */
public class GameView {
    private Canvas canvas; // Le canevas sur lequel le jeu est dessiné
    private GraphicsContext gc; // Le contexte graphique pour dessiner sur le canevas
    private Map<Integer, Image> PlayerImage; // Images des joueurs
    private Image wallImages; // Image des murs indestructibles
    private Image wallDestructible; // Image des murs destructibles
    private Image bomb; // Image des bombes
    private Map<Explosion.ExplosionType, Image> explosionImages; // Images des explosions
    private Map<PowerUp.Type, Image> bonusImages; // Images des bonus
    private Texture texture; // Pack de textures utilisé

    /**
     * Constructeur pour initialiser la vue du jeu.
     *
     * @param canvas Le canevas sur lequel le jeu sera dessiné.
     */
    public GameView(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        // Définir la taille du canevas
        canvas.setWidth(GameConstants.BOARD_WIDTH * GameConstants.CELL_SIZE);
        canvas.setHeight(GameConstants.BOARD_HEIGHT * GameConstants.CELL_SIZE);

        // Charger le pack de textures
        loadSelectTexturePack();
        loadAllImage();
    }

    /**
     * Charge le pack de textures sélectionné à partir des préférences utilisateur.
     */
    private void loadSelectTexturePack() {
        Preferences texturePrefs = Preferences.userRoot().node(AvatarController.class.getName());
        String textureName = texturePrefs.get(AvatarController.TEXTURE_PACK_KEY, "defaut");
        System.out.println("Nom de la texture chargée depuis les préférences : " + textureName);

        if ("defaut".equals(textureName)) {
            this.texture = new Texture("defaut", "/image/defaut/");
            System.out.println("Utilisation du pack de textures par défaut");
        } else if ("mario".equals(textureName)) {
            this.texture = new Texture("mario", "/image/mario/");
            System.out.println("Utilisation du pack de textures Mario");
        } else {
            this.texture = new Texture("defaut", "/image/defaut/");
            System.out.println("Texture inconnue, retour au pack de textures par défaut : " + textureName);
        }

        System.out.println("Chemin final de la texture : " + this.texture.getPath());
    }

    /**
     * Charge toutes les images nécessaires pour le jeu.
     */
    private void loadAllImage() {
        loadPlayerImage();
        loadWallImage();
        loadbombImage();
        loadExplosionImages();
        loadBonus();
    }

    /**
     * Charge les images des joueurs.
     */
    private void loadPlayerImage() {
        PlayerImage = new HashMap<>();
        try {
            PlayerImage.put(0, new Image(getClass().getResourceAsStream(texture.getPath() + "player1.png")));
            PlayerImage.put(1, new Image(getClass().getResourceAsStream(texture.getPath() + "player2.png")));
            PlayerImage.put(2, new Image(getClass().getResourceAsStream(texture.getPath() + "player3.png")));
            PlayerImage.put(3, new Image(getClass().getResourceAsStream(texture.getPath() + "player4.png")));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images des joueurs");
        }
    }

    /**
     * Charge les images des murs.
     */
    private void loadWallImage() {
        try {
            wallImages = new Image(getClass().getResourceAsStream(texture.getPath() + "wall.png"));
            wallDestructible = new Image(getClass().getResourceAsStream(texture.getPath() + "destructible_wall.png"));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images de murs: " + e.getMessage());
        }
    }

    /**
     * Charge l'image des bombes.
     */
    private void loadbombImage() {
        try {
            bomb = new Image(getClass().getResourceAsStream(texture.getPath() + "bomb.png"));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image de la bombe");
        }
    }

    /**
     * Charge les images des explosions.
     */
    private void loadExplosionImages() {
        explosionImages = new HashMap<>();
        try {
            explosionImages.put(Explosion.ExplosionType.CENTER,
                    new Image(getClass().getResourceAsStream(texture.getPath() + "explosion_centre.png")));
            explosionImages.put(Explosion.ExplosionType.VERTICAL,
                    new Image(getClass().getResourceAsStream(texture.getPath() + "explosion_horizontal.png")));
            explosionImages.put(Explosion.ExplosionType.HORIZONTAL,
                    new Image(getClass().getResourceAsStream(texture.getPath() + "explosion_vertical.png")));
            explosionImages.put(Explosion.ExplosionType.END,
                    new Image(getClass().getResourceAsStream(texture.getPath() + "explosion_end.png")));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images d'explosion: " + e.getMessage());
        }
    }

    /**
     * Charge les images des bonus.
     */
    private void loadBonus() {
        bonusImages = new HashMap<>();
        try {
            // Charger l'image pour EXTRA_BOMB
            bonusImages.put(PowerUp.Type.EXTRA_BOMB,
                    new Image(getClass().getResourceAsStream(texture.getPath() + "bonusFois2.png")));

            // Charger l'image pour BIGGER_EXPLOSION
            bonusImages.put(PowerUp.Type.BIGGER_EXPLOSION,
                    new Image(getClass().getResourceAsStream(texture.getPath() + "bonnusPlusGrand.png")));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images de bonus: " + e.getMessage());
        }
    }

    /**
     * Affiche le jeu sur le canevas.
     *
     * @param game L'état actuel du jeu à afficher.
     */
    public void render(Game game) {
        clearCanvas();

        GameBoard board = game.getBoard();

        // Dessiner le plateau de base
        drawBackground();

        // Dessiner les murs
        drawWalls(board);

        // Dessiner les power-ups
        drawPowerUps(board);

        // Dessiner les bombes
        drawBombs(board);

        // Dessiner les explosions
        drawExplosions(board);

        // Dessiner les joueurs
        drawPlayers(game.getPlayers());
    }

    /**
     * Efface le canevas.
     */
    private void clearCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Dessine l'arrière-plan du plateau de jeu.
     */
    private void drawBackground() {
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Grille
        gc.setStroke(Color.DARKGREEN);
        gc.setLineWidth(0.5);

        for (int x = 0; x <= GameConstants.BOARD_WIDTH; x++) {
            gc.strokeLine(x * GameConstants.CELL_SIZE, 0, x * GameConstants.CELL_SIZE, canvas.getHeight());
        }
        for (int y = 0; y <= GameConstants.BOARD_HEIGHT; y++) {
            gc.strokeLine(0, y * GameConstants.CELL_SIZE, canvas.getWidth(), y * GameConstants.CELL_SIZE);
        }
    }

    /**
     * Dessine les murs sur le plateau de jeu.
     *
     * @param board Le plateau de jeu contenant les murs.
     */
    private void drawWalls(GameBoard board) {
        for (int x = 0; x < GameConstants.BOARD_WIDTH; x++) {
            for (int y = 0; y < GameConstants.BOARD_HEIGHT; y++) {
                Wall wall = board.getWall(new Position(x, y));
                if (wall != null) {
                    Image wallImage = wall.isDestructible() ? wallDestructible : wallImages;

                    if (wallImage != null && !wallImage.isError()) {
                        gc.drawImage(wallImage,
                                x * GameConstants.CELL_SIZE,
                                y * GameConstants.CELL_SIZE,
                                GameConstants.CELL_SIZE,
                                GameConstants.CELL_SIZE);
                    } else {
                        // Solution de repli : rectangles colorés
                        if (wall.isDestructible()) {
                            gc.setFill(Color.BROWN);
                        } else {
                            gc.setFill(Color.GRAY);
                        }
                        gc.fillRect(x * GameConstants.CELL_SIZE, y * GameConstants.CELL_SIZE,
                                GameConstants.CELL_SIZE, GameConstants.CELL_SIZE);

                        // Bordure
                        gc.setStroke(Color.BLACK);
                        gc.setLineWidth(1);
                        gc.strokeRect(x * GameConstants.CELL_SIZE, y * GameConstants.CELL_SIZE,
                                GameConstants.CELL_SIZE, GameConstants.CELL_SIZE);
                    }
                }
            }
        }
    }

    /**
     * Dessine les bonus sur le plateau de jeu.
     *
     * @param board Le plateau de jeu contenant les bonus.
     */
    private void drawPowerUps(GameBoard board) {
        for (PowerUp powerUp : board.getPowerUps()) {
            Position pos = powerUp.getPosition();
            int x = pos.getX() * GameConstants.CELL_SIZE;
            int y = pos.getY() * GameConstants.CELL_SIZE;

            // Essayer d'utiliser l'image correspondante
            Image bonusImage = bonusImages.get(powerUp.getType());

            if (bonusImage != null && !bonusImage.isError()) {
                // Dessiner l'image du bonus
                gc.drawImage(bonusImage, x, y, GameConstants.CELL_SIZE, GameConstants.CELL_SIZE);
            } else {
                // Solution de repli : dessiner avec des couleurs
                switch (powerUp.getType()) {
                    case EXTRA_BOMB:
                        gc.setFill(Color.ORANGE);
                        break;
                    case BIGGER_EXPLOSION:
                        gc.setFill(Color.RED);
                        break;
                }

                gc.fillOval(x + 5, y + 5, GameConstants.CELL_SIZE - 10, GameConstants.CELL_SIZE - 10);

                // Symbole
                gc.setFill(Color.WHITE);
                gc.setFont(javafx.scene.text.Font.font(16));
                String symbol = switch (powerUp.getType()) {
                    case EXTRA_BOMB -> "B";
                    case BIGGER_EXPLOSION -> "E";
                };
                gc.fillText(symbol, x + GameConstants.CELL_SIZE / 2 - 5, y + GameConstants.CELL_SIZE / 2 + 5);
            }
        }
    }

    /**
     * Dessine les bombes sur le plateau de jeu.
     *
     * @param board Le plateau de jeu contenant les bombes.
     */
    private void drawBombs(GameBoard board) {
        for (Bomb bomb : board.getBombs()) {
            Position pos = bomb.getPosition();
            int x = pos.getX() * GameConstants.CELL_SIZE;
            int y = pos.getY() * GameConstants.CELL_SIZE;

            // Animation de clignotement basée sur le temps restant
            long timeLeft = bomb.getTimeLeft();
            boolean blink = (timeLeft < 1000) && (System.currentTimeMillis() / 200) % 2 == 0;

            if (!blink) {
                if (this.bomb != null && !this.bomb.isError()) {
                    gc.drawImage(this.bomb, x, y, GameConstants.CELL_SIZE, GameConstants.CELL_SIZE);
                } else {
                    // Solution de repli
                    gc.setFill(Color.BLACK);
                    gc.fillOval(x + 5, y + 5, GameConstants.CELL_SIZE - 10, GameConstants.CELL_SIZE - 10);

                    // Mèche
                    gc.setStroke(Color.ORANGE);
                    gc.setLineWidth(3);
                    gc.strokeLine(x + GameConstants.CELL_SIZE/2, y + 5,
                            x + GameConstants.CELL_SIZE/2, y);
                }
            }
        }
    }

    /**
     * Dessine les explosions sur le plateau de jeu.
     *
     * @param board Le plateau de jeu contenant les explosions.
     */
    private void drawExplosions(GameBoard board) {
        for (Explosion explosion : board.getExplosions()) {
            Map<Position, Explosion.ExplosionType> explosionTypes = explosion.getAllExplosionTypes();

            for (Map.Entry<Position, Explosion.ExplosionType> entry : explosionTypes.entrySet()) {
                Position pos = entry.getKey();
                Explosion.ExplosionType type = entry.getValue();

                int x = pos.getX() * GameConstants.CELL_SIZE;
                int y = pos.getY() * GameConstants.CELL_SIZE;

                Image explosionImage = explosionImages.get(type);

                if (explosionImage != null && !explosionImage.isError()) {
                    gc.drawImage(explosionImage, x, y, GameConstants.CELL_SIZE, GameConstants.CELL_SIZE);
                } else {
                    // Solution de repli avec couleurs différentes selon le type
                    switch (type) {
                        case CENTER:
                            gc.setFill(Color.WHITE);
                            break;
                        case HORIZONTAL:
                            gc.setFill(Color.YELLOW);
                            break;
                        case VERTICAL:
                            gc.setFill(Color.ORANGE);
                            break;
                        case END:
                            gc.setFill(Color.RED);
                            break;
                    }

                    gc.fillRect(x, y, GameConstants.CELL_SIZE, GameConstants.CELL_SIZE);

                    // Effet de flamme au centre
                    gc.setFill(Color.ORANGE);
                    gc.fillOval(x + 5, y + 5, GameConstants.CELL_SIZE - 10, GameConstants.CELL_SIZE - 10);
                }
            }
        }
    }

    /**
     * Dessine les joueurs sur le plateau de jeu.
     *
     * @param players La liste des joueurs à dessiner.
     */
    private void drawPlayers(List<Player> players) {
        for (Player player : players) {
            if (!player.isAlive()) continue;

            Position pos = player.getPosition();
            int x = pos.getX() * GameConstants.CELL_SIZE;
            int y = pos.getY() * GameConstants.CELL_SIZE;

            // Corps du joueur
            Image PlayerImages = PlayerImage.get(player.getId());
            if (PlayerImages != null && !PlayerImages.isError()) {
                gc.drawImage(PlayerImages, x + 3, y + 3, GameConstants.CELL_SIZE - 6, GameConstants.CELL_SIZE - 6);
            } else {
                gc.setFill(Color.web(player.getColor()));
                gc.fillRect(x + 3, y + 3, GameConstants.CELL_SIZE - 6, GameConstants.CELL_SIZE - 6);
            }
        }
    }
}

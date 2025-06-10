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

public class GameView {
    private Canvas canvas;
    private GraphicsContext gc;
    private Map<Integer, Image> PlayerImage;
    private Image wallImages;
    private Image wallDestructible;
    private Image bomb;
    private Map<Explosion.ExplosionType, Image> explosionImages;
    private Map<PowerUp.Type, Image> bonusImages;
    private Texture texture;

    public GameView(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        // Définir la taille du canvas
        canvas.setWidth(GameConstants.BOARD_WIDTH * GameConstants.CELL_SIZE);
        canvas.setHeight(GameConstants.BOARD_HEIGHT * GameConstants.CELL_SIZE);

        //pack de texture
        loadSelectTexturePack();
        loadAllImage();
    }

    private void loadSelectTexturePack() {
        //charger les image avec le pack de texture selectionner
        Preferences texturePrefs = Preferences.userRoot().node(AvatarController.class.getName());
        String textureName = texturePrefs.get(AvatarController.TEXTURE_PACK_KEY, "defaut");

        if ("defaut".equals(textureName)) {
            this.texture = new Texture("defaut", "/image/defaut/");
        } else if ("mario".equals(textureName)) {
            this.texture = new Texture("mario", "/image/mario/");
        } else {
            this.texture = new Texture("defaut", "/image/defaut/");
        }
    }


    private void loadAllImage() {
        loadPlayerImage();
        loadWallImage();
        loadbombImage();
        loadExplosionImages();
        loadBonus();
    }

    private void loadPlayerImage() {
        PlayerImage = new HashMap<>();
        try {
            //chercher et près cherger l'image
            PlayerImage.put(0, new Image(getClass().getResourceAsStream(texture.getPath() + "player1.png")));
            PlayerImage.put(1, new Image(getClass().getResourceAsStream(texture.getPath() + "player2.png")));
            PlayerImage.put(2, new Image(getClass().getResourceAsStream(texture.getPath() + "player3.png")));
            PlayerImage.put(3, new Image(getClass().getResourceAsStream(texture.getPath() + "player4.png")));
        }catch (Exception e) {
            System.err.println("Error loading player image");
        }
    }

    private void loadWallImage() {
        try {
            wallImages = new Image(getClass().getResourceAsStream(texture.getPath() + "wall.png"));
            wallDestructible = new Image(getClass().getResourceAsStream(texture.getPath() + "destructible_wall.png"));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images de murs: " + e.getMessage());
        }
    }

    private void loadbombImage() {
        try{
            bomb = new Image(getClass().getResourceAsStream(texture.getPath() + "bomb.png"));
        }catch (Exception e) {
            System.err.println("Erreur image bomb");
        }
    }

    private void loadExplosionImages() {
        explosionImages = new HashMap<>();
        try {
            explosionImages.put(Explosion.ExplosionType.CENTER,
                    new Image(getClass().getResourceAsStream(texture.getPath() + "explosion_centre.png")));
            explosionImages.put(Explosion.ExplosionType.VERTICAL,
                    new Image(getClass().getResourceAsStream(texture.getPath() + "explosion_horizontal.png")));
            explosionImages.put(Explosion.ExplosionType.HORIZONTAL,
                    new Image(getClass().getResourceAsStream(texture.getPath() +"explosion_vertical.png")));
            explosionImages.put(Explosion.ExplosionType.END,
                    new Image(getClass().getResourceAsStream(texture.getPath() + "explosion_end.png")));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images d'explosion: " + e.getMessage());
        }
    }

    private void loadBonus() {
        bonusImages = new HashMap<>(); // Initialisation de la Map

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

    private void clearCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

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
                        // Fallback : rectangles colorés
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
                // Fallback : dessiner avec des couleurs (code existant)
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
                    // Fallback
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
                    // Fallback avec couleurs différentes selon le type
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

    private void drawPlayers(java.util.List<Player> players) {
        for (Player player : players) {
            if (!player.isAlive()) continue;

            Position pos = player.getPosition();
            int x = pos.getX() * GameConstants.CELL_SIZE;
            int y = pos.getY() * GameConstants.CELL_SIZE;

            // Corps du joueur
            Image PlayerImages = PlayerImage.get(player.getId());
            if (PlayerImages != null && !PlayerImages.isError()) {
                gc.drawImage(PlayerImages, x + 3, y + 3, GameConstants.CELL_SIZE - 6, GameConstants.CELL_SIZE -6 );
            } else {
                gc.setFill(Color.web(player.getColor()));
                gc.fillRect(x+3, y+3, GameConstants.CELL_SIZE-6 , GameConstants.CELL_SIZE-6);
            }

            // Numéro du joueur
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(14));
        }
    }
}
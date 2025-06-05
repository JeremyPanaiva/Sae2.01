package com.bomberman.view;

import com.bomberman.model.*;
import com.bomberman.util.GameConstants;
import com.bomberman.util.Position;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class GameView {
    private Canvas canvas;
    private GraphicsContext gc;
    private Map<Integer, Image> PlayerImage;
    private Image wallImages;
    private Image wallDestructible;
    private Image bomb;


    public GameView(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        // Définir la taille du canvas
        canvas.setWidth(GameConstants.BOARD_WIDTH * GameConstants.CELL_SIZE);
        canvas.setHeight(GameConstants.BOARD_HEIGHT * GameConstants.CELL_SIZE);

        loadAllImage();
    }

    private void loadAllImage() {
        loadPlayerImage();
        loadWallImage();
        loadbombImage();
    }

    private void loadPlayerImage() {
        PlayerImage = new HashMap<>();
        try {
            PlayerImage.put(0, new Image(getClass().getResourceAsStream("/image/player1.png")));
            PlayerImage.put(1, new Image(getClass().getResourceAsStream("/image/player2.png")));
            PlayerImage.put(2, new Image(getClass().getResourceAsStream("/image/player3.png")));
            PlayerImage.put(3, new Image(getClass().getResourceAsStream("/image/player4.png")));
        }catch (Exception e) {
            System.err.println("Error loading player image");
        }
    }

    private void loadWallImage() {
        try {
            wallImages = new Image(getClass().getResourceAsStream("/image/wall.png"));
            wallDestructible = new Image(getClass().getResourceAsStream("/image/destructible_wall.png"));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images de murs: " + e.getMessage());
        }
    }

    private void loadbombImage() {
        try{
            bomb = new Image(getClass().getResourceAsStream("/image/bomb.png"));
        }catch (Exception e) {
            System.err.println("Erreur image bomb");
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
        gc.setFill(Color.LIGHTGREEN);
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

            // Couleur selon le type de power-up
            switch (powerUp.getType()) {
                case EXTRA_BOMB:
                    gc.setFill(Color.ORANGE);
                    break;
                case BIGGER_EXPLOSION:
                    gc.setFill(Color.RED);
                    break;
                case SPEED_UP:
                    gc.setFill(Color.CYAN);
                    break;
            }

            gc.fillOval(x + 5, y + 5, GameConstants.CELL_SIZE - 10, GameConstants.CELL_SIZE - 10);

            // Symbole
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(16));
            String symbol = switch (powerUp.getType()) {
                case EXTRA_BOMB -> "B";
                case BIGGER_EXPLOSION -> "E";
                case SPEED_UP -> "S";
            };
            gc.fillText(symbol, x + GameConstants.CELL_SIZE/2 - 5, y + GameConstants.CELL_SIZE/2 + 5);
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

    private void drawExplosions(GameBoard board) {
        for (Explosion explosion : board.getExplosions()) {
            gc.setFill(Color.YELLOW);
            for (Position pos : explosion.getPositions()) {
                int x = pos.getX() * GameConstants.CELL_SIZE;
                int y = pos.getY() * GameConstants.CELL_SIZE;
                gc.fillRect(x, y, GameConstants.CELL_SIZE, GameConstants.CELL_SIZE);

                // Effet de flamme
                gc.setFill(Color.ORANGE);
                gc.fillOval(x + 5, y + 5, GameConstants.CELL_SIZE - 10, GameConstants.CELL_SIZE - 10);
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
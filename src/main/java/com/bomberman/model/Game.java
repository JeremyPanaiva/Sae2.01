package com.bomberman.model;

import com.bomberman.controller.AvatarController;
import com.bomberman.util.*;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class Game {
    private GameBoard board;
    private List<Player> players;
    private boolean gameRunning;
    private Player winner;
    private Stage primaryStage;

    public Game(int numPlayers) {
        board = new GameBoard();
        players = new ArrayList<>();
        gameRunning = true;
        winner = null;


        //compteur de match +1 dès qu'une partie commence
        initializePlayers(numPlayers);
        com.bomberman.controller.AvatarController.incrementTotalMatch(AvatarController.TOTAL_MATCH_KEY1);
        com.bomberman.controller.AvatarController.incrementTotalMatch(AvatarController.TOTAL_MATCH_KEY2);
        com.bomberman.controller.AvatarController.incrementTotalMatch(AvatarController.TOTAL_MATCH_KEY3);
        com.bomberman.controller.AvatarController.incrementTotalMatch(AvatarController.TOTAL_MATCH_KEY4);
    }

    private void initializePlayers(int numPlayers) {
        Position[] startPositions = {
                new Position(1, 1),
                new Position(GameConstants.BOARD_WIDTH - 2, 1),
                new Position(1, GameConstants.BOARD_HEIGHT - 2),
                new Position(GameConstants.BOARD_WIDTH - 2, GameConstants.BOARD_HEIGHT - 2)
        };

        for (int i = 0; i < Math.min(numPlayers, GameConstants.MAX_PLAYERS); i++) {
            players.add(new Player(i, startPositions[i], GameConstants.PLAYER_COLORS[i]));
        }

        //compteur nb match total

    }

    public Game(int numPlayers, Stage stage) {
        this(numPlayers);
        this.primaryStage = stage;
    }

    public void movePlayer(int playerId, Direction direction) {
        if (!gameRunning) return;

        Player player = players.get(playerId);
        if (player != null && player.isAlive()) {
            player.move(direction, board);
            checkPowerUpCollection(player);
        }
    }

    public void placeBomb(int playerId) {
        if (!gameRunning) return;

        Player player = players.get(playerId);
        if (player != null && player.isAlive() && player.canPlaceBomb()) {
            Bomb bomb = new Bomb(player.getPosition(), player);
            board.placeBomb(bomb);
            player.placeBomb();
        }
    }

    public void update() {
        if (!gameRunning) return;

        // Vérifier les bombes qui doivent exploser
        List<Bomb> bombsToExplode = new ArrayList<>();
        for (Bomb bomb : board.getBombs()) {
            if (bomb.shouldExplode()) {
                bombsToExplode.add(bomb);
            }
        }

        // Faire exploser les bombes
        for (Bomb bomb : bombsToExplode) {
            explodeBomb(bomb);
        }

        // Nettoyer les explosions finies
        board.removeFinishedExplosions();

        // Vérifier si des joueurs sont morts dans les explosions
        checkPlayerDeaths();

        // Vérifier les conditions de victoire
        checkWinCondition();
    }

    private void explodeBomb(Bomb bomb) {
        board.removeBomb(bomb.getPosition());
        bomb.getOwner().bombExploded();

        List<Position> explosionPositions = calculateExplosionPositions(bomb);
        // Passer la position centrale de la bombe pour categoriser les explosions
        board.addExplosion(new Explosion(explosionPositions, bomb.getPosition()));

        // Détruire les murs destructibles
        for (Position pos : explosionPositions) {
            if (board.hasDestructibleWall(pos)) {
                board.destroyWall(pos);
            }
        }
    }

    private List<Position> calculateExplosionPositions(Bomb bomb) {
        List<Position> positions = new ArrayList<>();
        Position center = bomb.getPosition();
        positions.add(center);

        // Explosion dans les 4 directions
        for (Direction dir : Direction.values()) {
            for (int i = 1; i <= bomb.getExplosionRange(); i++) {
                Position pos = new Position(center.getX() + dir.getDeltaX() * i,
                        center.getY() + dir.getDeltaY() * i);

                if (!board.isValidPosition(pos) || board.hasWall(pos)) {
                    if (board.hasDestructibleWall(pos)) {
                        positions.add(pos);
                    }
                    break;
                }

                positions.add(pos);
            }
        }

        return positions;
    }

    private void checkPowerUpCollection(Player player) {
        PowerUp powerUp = board.getPowerUp(player.getPosition());
        if (powerUp != null) {
            powerUp.applyTo(player);
        }
    }

    private void checkPlayerDeaths() {
        for (Player player : players) {
            if (player.isAlive()) {
                for (Explosion explosion : board.getExplosions()) {
                    if (explosion.contains(player.getPosition())) {
                        player.kill();
                        break;
                    }
                }
            }
        }
    }

    private void checkWinCondition() {
        List<Player> alivePlayers = players.stream()
                .filter(Player::isAlive)
                .toList();

        if (alivePlayers.size() <= 1) {
            gameRunning = false;
            if (alivePlayers.size() == 1) {
                winner = alivePlayers.get(0);
                //récupére l'Id au player pour fair +1 dans ses match gagner
                com.bomberman.controller.AvatarController.incrementNbMatchGagner(winner.getId());
            }
        }
    }

    private void returnToMainMenu() {
        if (primaryStage != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 800, 600);
                scene.getStylesheets().add(getClass().getResource("/css/MainMenu.css").toExternalForm());

                primaryStage.setScene(scene);
                primaryStage.setResizable(false);

                // Focus sur la scene pour les événements clavier
                scene.getRoot().requestFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void resetGame() {
        board = new GameBoard();
        gameRunning = true;
        winner = null;

        // Réinitialiser les joueurs
        Position[] startPositions = {
                new Position(1, 1),
                new Position(GameConstants.BOARD_WIDTH - 2, 1),
                new Position(1, GameConstants.BOARD_HEIGHT - 2),
                new Position(GameConstants.BOARD_WIDTH - 2, GameConstants.BOARD_HEIGHT - 2)
        };

        for (int i = 0; i < players.size(); i++) {
            players.set(i, new Player(i, startPositions[i], GameConstants.PLAYER_COLORS[i]));
        }
    }

    // Getters
    public GameBoard getBoard() { return board; }
    public List<Player> getPlayers() { return new ArrayList<>(players); }
    public boolean isGameRunning() { return gameRunning; }
    public Player getWinner() { return winner; }
    public void setPrimaryStage(Stage stage) { this.primaryStage = stage; }


}


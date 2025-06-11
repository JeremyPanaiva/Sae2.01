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

/**
 * Représente le jeu Bomberman.
 * Gère le plateau de jeu, les joueurs, et les règles du jeu.
 */
public class Game {
    private GameBoard board; // Le plateau de jeu
    private List<Player> players; // Liste des joueurs
    private boolean gameRunning; // Indique si le jeu est en cours
    private Player winner; // Le joueur gagnant
    private Stage primaryStage; // La scène principale du jeu
    private int humanPlayerCount; // Nombre de joueurs humains

    /**
     * Constructeur pour initialiser un nouveau jeu.
     *
     * @param totalPlayers Le nombre total de joueurs.
     * @param humanPlayers Le nombre de joueurs humains.
     */
    public Game(int totalPlayers, int humanPlayers) {
        this.humanPlayerCount = humanPlayers;
        board = new GameBoard();
        players = new ArrayList<>();
        gameRunning = true;
        winner = null;

        initializePlayers(totalPlayers, humanPlayers);

        // Incrémenter les compteurs de matchs
        AvatarController.incrementTotalMatch(AvatarController.TOTAL_MATCH_KEY1);
        AvatarController.incrementTotalMatch(AvatarController.TOTAL_MATCH_KEY2);
        AvatarController.incrementTotalMatch(AvatarController.TOTAL_MATCH_KEY3);
        AvatarController.incrementTotalMatch(AvatarController.TOTAL_MATCH_KEY4);
    }

    /**
     * Constructeur de compatibilité pour l'ancien code.
     *
     * @param numPlayers Le nombre total de joueurs, tous humains par défaut.
     */
    public Game(int numPlayers) {
        this(numPlayers, numPlayers); // Tous les joueurs sont humains par défaut
    }

    /**
     * Initialise les joueurs pour le jeu.
     *
     * @param totalPlayers Le nombre total de joueurs.
     * @param humanPlayers Le nombre de joueurs humains.
     */
    private void initializePlayers(int totalPlayers, int humanPlayers) {
        Position[] startPositions = {
                new Position(1, 1),
                new Position(GameConstants.BOARD_WIDTH - 2, 1),
                new Position(1, GameConstants.BOARD_HEIGHT - 2),
                new Position(GameConstants.BOARD_WIDTH - 2, GameConstants.BOARD_HEIGHT - 2)
        };

        int actualTotalPlayers = Math.min(totalPlayers, GameConstants.MAX_PLAYERS);
        int actualHumanPlayers = Math.min(humanPlayers, actualTotalPlayers);

        // Créer les joueurs humains d'abord
        for (int i = 0; i < actualHumanPlayers; i++) {
            players.add(new Player(i, startPositions[i], GameConstants.PLAYER_COLORS[i]));
        }

        // Créer les bots pour compléter
        for (int i = actualHumanPlayers; i < actualTotalPlayers; i++) {
            players.add(new BotPlayer(i, startPositions[i], GameConstants.PLAYER_COLORS[i]));
        }
    }

    /**
     * Constructeur pour initialiser un nouveau jeu avec une scène principale.
     *
     * @param numPlayers Le nombre total de joueurs.
     * @param stage La scène principale du jeu.
     */
    public Game(int numPlayers, Stage stage) {
        this(numPlayers);
        this.primaryStage = stage;
    }

    /**
     * Déplace un joueur dans une direction donnée.
     *
     * @param playerId L'identifiant du joueur.
     * @param direction La direction dans laquelle déplacer le joueur.
     */
    public void movePlayer(int playerId, Direction direction) {
        if (!gameRunning) return;

        if (playerId >= 0 && playerId < players.size()) {
            Player player = players.get(playerId);
            if (player != null && player.isAlive()) {
                player.move(direction, board);
                checkPowerUpCollection(player);
            }
        }
    }

    /**
     * Place une bombe pour un joueur donné.
     *
     * @param playerId L'identifiant du joueur.
     */
    public void placeBomb(int playerId) {
        if (!gameRunning) return;

        if (playerId >= 0 && playerId < players.size()) {
            Player player = players.get(playerId);
            if (player != null && player.isAlive() && player.canPlaceBomb()) {
                Bomb bomb = new Bomb(player.getPosition(), player);
                board.placeBomb(bomb);
                player.placeBomb();
            }
        }
    }

    /**
     * Met à jour l'état du jeu.
     */
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

    /**
     * Fait exploser une bombe et gère les conséquences.
     *
     * @param bomb La bombe à faire exploser.
     */
    private void explodeBomb(Bomb bomb) {
        board.removeBomb(bomb.getPosition());
        bomb.getOwner().bombExploded();

        List<Position> explosionPositions = calculateExplosionPositions(bomb);
        // Passer la position centrale de la bombe pour catégoriser les explosions
        board.addExplosion(new Explosion(explosionPositions, bomb.getPosition()));

        // Détruire les murs destructibles
        for (Position pos : explosionPositions) {
            if (board.hasDestructibleWall(pos)) {
                board.destroyWall(pos);
            }
        }
    }

    /**
     * Calcule les positions affectées par l'explosion d'une bombe.
     *
     * @param bomb La bombe.
     * @return La liste des positions affectées par l'explosion.
     */
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

    /**
     * Vérifie si un joueur a collecté un bonus.
     *
     * @param player Le joueur à vérifier.
     */
    private void checkPowerUpCollection(Player player) {
        PowerUp powerUp = board.getPowerUp(player.getPosition());
        if (powerUp != null) {
            powerUp.applyTo(player);
        }
    }

    /**
     * Vérifie si des joueurs sont morts dans les explosions.
     */
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

    /**
     * Vérifie les conditions de victoire.
     */
    private void checkWinCondition() {
        List<Player> alivePlayers = players.stream()
                .filter(Player::isAlive)
                .toList();

        if (alivePlayers.size() <= 1) {
            gameRunning = false;
            if (alivePlayers.size() == 1) {
                winner = alivePlayers.get(0);
                System.out.println("Le gagnant est le joueur " + winner.getId());
                AvatarController.incrementNbMatchGagner(winner.getId());
            }
        }
    }

    /**
     * Retourne au menu principal.
     */
    private void returnToMainMenu() {
        if (primaryStage != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 800, 600);
                scene.getStylesheets().add(getClass().getResource("/css/MainMenu.css").toExternalForm());

                primaryStage.setScene(scene);
                primaryStage.setResizable(false);

                // Focus sur la scène pour les événements clavier
                scene.getRoot().requestFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Réinitialise le jeu.
     */
    public void resetGame() {
        resetGame(this.humanPlayerCount);
    }

    /**
     * Réinitialise le jeu avec un nombre spécifique de joueurs humains.
     *
     * @param humanPlayers Le nombre de joueurs humains.
     */
    public void resetGame(int humanPlayers) {
        this.humanPlayerCount = humanPlayers;
        board = new GameBoard();
        gameRunning = true;
        winner = null;

        // Réinitialiser les joueurs avec le bon nombre de joueurs humains
        players.clear();
        initializePlayers(4, humanPlayers); // Toujours 4 joueurs au total

        // Incrémenter les compteurs de matchs
        AvatarController.incrementTotalMatch(AvatarController.TOTAL_MATCH_KEY1);
        AvatarController.incrementTotalMatch(AvatarController.TOTAL_MATCH_KEY2);
        AvatarController.incrementTotalMatch(AvatarController.TOTAL_MATCH_KEY3);
        AvatarController.incrementTotalMatch(AvatarController.TOTAL_MATCH_KEY4);
    }

    /**
     * Retourne le plateau de jeu.
     *
     * @return Le plateau de jeu.
     */
    public GameBoard getBoard() {
        return board;
    }

    /**
     * Retourne la liste des joueurs.
     *
     * @return La liste des joueurs.
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Vérifie si le jeu est en cours.
     *
     * @return true si le jeu est en cours, false sinon.
     */
    public boolean isGameRunning() {
        return gameRunning;
    }

    /**
     * Retourne le joueur gagnant.
     *
     * @return Le joueur gagnant.
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Définit la scène principale du jeu.
     *
     * @param stage La scène principale.
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Retourne le nombre de joueurs humains.
     *
     * @return Le nombre de joueurs humains.
     */
    public int getHumanPlayerCount() {
        return humanPlayerCount;
    }

    /**
     * Retourne un joueur par son identifiant.
     *
     * @param playerId L'identifiant du joueur.
     * @return Le joueur correspondant à l'identifiant.
     */
    public Player getPlayer(int playerId) {
        if (playerId >= 0 && playerId < players.size()) {
            return players.get(playerId);
        }
        return null;
    }

    /**
     * Retourne le nombre total de joueurs.
     *
     * @return Le nombre total de joueurs.
     */
    public int getTotalPlayerCount() {
        return players.size();
    }
}

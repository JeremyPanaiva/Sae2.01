package com.bomberman.controller;

import com.bomberman.model.BotPlayer;
import com.bomberman.model.Game;
import com.bomberman.model.Player;
import com.bomberman.util.Direction;
import com.bomberman.view.GameView;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Classe contrôleur pour le jeu Bomberman.
 * Gère les entrées utilisateur, la boucle de jeu et les mises à jour de l'état du jeu.
 */
public class GameController implements Initializable {

    @FXML private VBox root;
    @FXML private Canvas gameCanvas;
    @FXML private Text statusText;

    private Game game;
    private GameView gameView;
    private AnimationTimer gameLoop;
    private Set<KeyCode> pressedKeys;
    private Set<KeyCode> processedKeys;
    private int humanPlayerCount = 2; // Valeur par défaut

    /**
     * Contrôles pour chaque joueur.
     * Chaque joueur a un tableau de KeyCodes pour haut, bas, gauche, droite et placement de bombe.
     */
    private static final KeyCode[][] PLAYER_CONTROLS = {
            {KeyCode.Z, KeyCode.S, KeyCode.Q, KeyCode.D, KeyCode.A}, // Joueur 1: ZQSD + A pour bombe
            {KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.ENTER}, // Joueur 2: Flèches + Entrée
            {KeyCode.I, KeyCode.K, KeyCode.J, KeyCode.L, KeyCode.U}, // Joueur 3: IJKL + U
            {KeyCode.NUMPAD8, KeyCode.NUMPAD5, KeyCode.NUMPAD4, KeyCode.NUMPAD6, KeyCode.NUMPAD0} // Joueur 4: Pavé numérique
    };

    /**
     * Initialise le contrôleur.
     * @param location L'emplacement utilisé pour résoudre les chemins relatifs pour l'objet racine.
     * @param resources Les ressources utilisées pour localiser l'objet racine.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pressedKeys = new HashSet<>();
        processedKeys = new HashSet<>();

        // Initialiser le jeu avec le nombre de joueurs humains par défaut
        setHumanPlayerCount(humanPlayerCount);
        System.out.println("GameController initialisé");
    }

    /**
     * Définit le nombre de joueurs humains dans le jeu.
     * @param humanPlayers Le nombre de joueurs humains.
     */
    public void setHumanPlayerCount(int humanPlayers) {
        this.humanPlayerCount = humanPlayers;
        System.out.println("Définir le nombre de joueurs humains à : " + humanPlayers);
        initializeGame();
    }

    /**
     * Initialise le jeu avec le nombre spécifié de joueurs.
     */
    private void initializeGame() {
        // Créer le jeu avec 4 joueurs au total, dont humanPlayerCount joueurs humains
        game = new Game(4, humanPlayerCount);
        gameView = new GameView(gameCanvas);

        initializeGameLoop();
        setupKeyHandlers();

        root.requestFocus();

        System.out.println("Jeu initialisé avec " + humanPlayerCount + " joueurs humains");
        // Debug: afficher les types de joueurs
        for (int i = 0; i < game.getPlayers().size(); i++) {
            Player player = game.getPlayer(i);
            if (player != null) {
                System.out.println("Joueur " + i + ": " + (player instanceof BotPlayer ? "Bot" : "Humain"));
            } else {
                System.out.println("Joueur " + i + ": null");
            }
        }
    }

    /**
     * Initialise la boucle de jeu.
     */
    private void initializeGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 16_666_667) { // ~60 FPS
                    handleInput();
                    handleBots();
                    game.update();
                    gameView.render(game);
                    updateStatus();
                    lastUpdate = now;
                }
            }
        };
        gameLoop.start();
    }

    /**
     * Configure les gestionnaires d'événements de touches.
     */
    private void setupKeyHandlers() {
        root.setOnKeyPressed(this::onKeyPressed);
        root.setOnKeyReleased(this::onKeyReleased);
        root.setFocusTraversable(true);
    }

    /**
     * Gère les événements d'appui sur les touches.
     * @param event L'événement de touche.
     */
    private void onKeyPressed(KeyEvent event) {
        KeyCode key = event.getCode();

        // Si la touche n'était pas déjà pressée, on l'ajoute aux deux ensembles
        if (!pressedKeys.contains(key)) {
            pressedKeys.add(key);
            processedKeys.add(key);
        }

        // Redémarrer le jeu avec R
        if (key == KeyCode.R) {
            game.resetGame(humanPlayerCount);
        }

        if (key == KeyCode.ESCAPE) {
            returnToMainMenu();
        }
    }

    /**
     * Gère les événements de relâchement des touches.
     * @param event L'événement de touche.
     */
    private void onKeyReleased(KeyEvent event) {
        KeyCode key = event.getCode();
        pressedKeys.remove(key);
        processedKeys.remove(key);
    }

    /**
     * Gère les entrées du joueur pour le mouvement et le placement de bombe.
     */
    private void handleInput() {
        if (!game.isGameRunning()) return;

        // Gérer les contrôles pour chaque joueur humain
        for (int playerId = 0; playerId < humanPlayerCount && playerId < PLAYER_CONTROLS.length; playerId++) {
            Player player = game.getPlayer(playerId);

            // Vérifier que ce joueur existe et n'est pas un bot
            if (player == null || player instanceof BotPlayer) {
                System.out.println("Ignorer le joueur " + playerId + " - est un bot ou null");
                continue;
            }

            KeyCode[] controls = PLAYER_CONTROLS[playerId];

            // Mouvement - seulement si la touche vient d'être pressée
            if (processedKeys.contains(controls[0])) { // Haut
                game.movePlayer(playerId, Direction.UP);
                processedKeys.remove(controls[0]);
            }
            if (processedKeys.contains(controls[1])) { // Bas
                game.movePlayer(playerId, Direction.DOWN);
                processedKeys.remove(controls[1]);
            }
            if (processedKeys.contains(controls[2])) { // Gauche
                game.movePlayer(playerId, Direction.LEFT);
                processedKeys.remove(controls[2]);
            }
            if (processedKeys.contains(controls[3])) { // Droite
                game.movePlayer(playerId, Direction.RIGHT);
                processedKeys.remove(controls[3]);
            }

            // Bombe - seulement si la touche vient d'être pressée
            if (processedKeys.contains(controls[4])) {
                game.placeBomb(playerId);
                processedKeys.remove(controls[4]);
            }
        }
    }

    /**
     * Gère les mouvements et les placements de bombes des bots.
     */
    private void handleBots() {
        if (!game.isGameRunning()) return;

        for (int i = 0; i < game.getPlayers().size(); i++) {
            Player player = game.getPlayer(i);
            if (player instanceof BotPlayer) {
                BotPlayer bot = (BotPlayer) player;
                bot.makeMove(game.getBoard(), game.getPlayers());

                // Vérifier si le bot veut placer une bombe
                if (bot.wantsToPlaceBomb(game.getBoard(), game.getPlayers())) {
                    game.placeBomb(i);
                }
            }
        }
    }

    /**
     * Met à jour le texte de statut en fonction de l'état du jeu.
     */
    private void updateStatus() {
        if (!game.isGameRunning()) {
            if (game.getWinner() != null) {
                Player winner = game.getWinner();
                String playerType = (winner instanceof BotPlayer) ? "Bot " : "Joueur ";
                statusText.setText(playerType + (winner.getId() + 1) + " a gagné ! Appuyez sur R pour rejouer.");
            } else {
                statusText.setText("Égalité ! Appuyez sur R pour rejouer.");
            }
        } else {
            // Compter les joueurs vivants en fonction de leur type réel
            long humanAlive = 0;
            long botsAlive = 0;

            for (int i = 0; i < game.getPlayers().size(); i++) {
                Player player = game.getPlayer(i);
                if (player != null && player.isAlive()) {
                    if (player instanceof BotPlayer) {
                        botsAlive++;
                    } else {
                        humanAlive++;
                    }
                }
            }

            statusText.setText("Joueurs en vie: " + humanAlive + " | Bots en vie: " + botsAlive +
                    " | Contrôles: J1(ZQSD+A) J2(Flèches+Entrée) J3(IJKL+U) J4(Pavé+0)");
        }
    }

    /**
     * Retourne au menu principal.
     */
    private void returnToMainMenu() {
        if (gameLoop != null) {
            gameLoop.stop(); // Arrêter la boucle de jeu
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
            Parent rootParent = loader.load();
            Scene scene = new Scene(rootParent, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/MainMenu.css").toExternalForm());

            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);

            // Focus sur la scène pour les événements clavier
            scene.getRoot().requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

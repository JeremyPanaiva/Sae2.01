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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class GameController implements Initializable {

    /** Conteneur principal de l'interface graphique. */
    @FXML
    private VBox root;

    /** Canvas pour dessiner le jeu. */
    @FXML
    private Canvas gameCanvas;

    /** Zone de texte affichant le statut du jeu (victoire, nombre de joueurs, etc). */
    @FXML
    private Text statusText;

    /** Instance du modèle de jeu contenant la logique métier. */
    private Game game;

    /** Vue graphique permettant de rendre le jeu sur le canvas. */
    private GameView gameView;

    /** Boucle d'animation gérant la mise à jour du jeu à ~60 FPS. */
    private AnimationTimer gameLoop;

    /** Ensemble des touches actuellement pressées. */
    private Set<KeyCode> pressedKeys;

    /** Ensemble des touches pressées à traiter une seule fois (pour éviter répétition continue). */
    private Set<KeyCode> processedKeys;

    /** Nombre de joueurs humains dans la partie. Par défaut 2. */
    private int humanPlayerCount = 2;

    /**
     * Tableau des contrôles clavier pour chaque joueur humain.
     * Chaque joueur a 5 touches : Haut, Bas, Gauche, Droite, Poser bombe.
     */
    private static final KeyCode[][] PLAYER_CONTROLS = {
            {KeyCode.Z, KeyCode.S, KeyCode.Q, KeyCode.D, KeyCode.A},            // Joueur 1 : ZQSD + A
            {KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.ENTER}, // Joueur 2 : Flèches + Entrée
            {KeyCode.I, KeyCode.K, KeyCode.J, KeyCode.L, KeyCode.U},            // Joueur 3 : IJKL + U
            {KeyCode.NUMPAD8, KeyCode.NUMPAD5, KeyCode.NUMPAD4, KeyCode.NUMPAD6, KeyCode.NUMPAD0} // Joueur 4 : Pavé numérique + 0
    };

    /**
     * Initialisation automatique appelée après chargement du FXML.
     * Initialise les collections de touches et lance la création du jeu.
     *
     * @param location URL de localisation (non utilisé ici).
     * @param resources Ressources locales (non utilisées ici).
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pressedKeys = new HashSet<>();
        processedKeys = new HashSet<>();

        // Initialisation du jeu avec le nombre par défaut de joueurs humains.
        setHumanPlayerCount(humanPlayerCount);
        System.out.println("GameController initialized");
    }

    /**
     * Modifie le nombre de joueurs humains dans la partie et réinitialise le jeu.
     *
     * @param humanPlayers Nombre de joueurs humains souhaité.
     */
    public void setHumanPlayerCount(int humanPlayers) {
        this.humanPlayerCount = humanPlayers;
        System.out.println("Setting human player count to: " + humanPlayers);
        initializeGame();
    }

    /**
     * Initialise les objets du jeu : modèle, vue, boucle de jeu, et gestion des événements clavier.
     */
    private void initializeGame() {
        // Création d'une nouvelle instance du jeu avec 4 joueurs dont humanPlayerCount humains.
        game = new Game(4, humanPlayerCount);
        gameView = new GameView(gameCanvas);

        initializeGameLoop();
        setupKeyHandlers();

        // Demande le focus clavier sur le conteneur principal pour recevoir les événements clavier.
        root.requestFocus();

        System.out.println("Game initialized with " + humanPlayerCount + " human players");
        // Affiche en console le type de chaque joueur (Bot ou Humain)
        for (int i = 0; i < game.getPlayers().size(); i++) {
            Player player = game.getPlayer(i);
            if (player != null) {
                System.out.println("Player " + i + ": " + (player instanceof BotPlayer ? "Bot" : "Human"));
            } else {
                System.out.println("Player " + i + ": null");
            }
        }
    }

    /**
     * Crée et démarre la boucle d'animation (gameLoop) pour mettre à jour le jeu à environ 60 FPS.
     * Dans chaque cycle : traite les entrées, déplace les bots, met à jour le modèle, rend la vue, et met à jour le statut.
     */
    private void initializeGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                // Environ 16.67ms entre chaque frame (60 FPS)
                if (now - lastUpdate >= 16_666_667) {
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
     * Configure les gestionnaires d'événements clavier sur le conteneur racine (root).
     * Permet de détecter les pressions et relâchements de touches.
     */
    private void setupKeyHandlers() {
        root.setOnKeyPressed(this::onKeyPressed);
        root.setOnKeyReleased(this::onKeyReleased);
        root.setFocusTraversable(true);
    }

    /**
     * Méthode appelée lorsqu'une touche est pressée.
     * Ajoute la touche aux ensembles de touches pressées et traite les commandes spéciales (R pour reset, ESC pour menu).
     *
     * @param event Événement clavier KeyEvent.
     */
    private void onKeyPressed(KeyEvent event) {
        KeyCode key = event.getCode();

        // Si la touche n'était pas déjà pressée, on l'ajoute aux ensembles.
        if (!pressedKeys.contains(key)) {
            pressedKeys.add(key);
            processedKeys.add(key);
        }

        // Redémarrer la partie si la touche R est pressée
        if (key == KeyCode.R) {
            game.resetGame(humanPlayerCount);
        }

        // Retour au menu principal si ESC est pressé
        if (key == KeyCode.ESCAPE) {
            returnToMainMenu();
        }
    }

    /**
     * Méthode appelée lorsqu'une touche est relâchée.
     * Retire la touche des ensembles de touches pressées et traitées.
     *
     * @param event Événement clavier KeyEvent.
     */
    private void onKeyReleased(KeyEvent event) {
        KeyCode key = event.getCode();
        pressedKeys.remove(key);
        processedKeys.remove(key);
    }

    /**
     * Traite les entrées clavier des joueurs humains.
     * Pour chaque joueur humain, déplace le joueur ou place une bombe si une touche correspondante vient d'être pressée.
     * Les touches sont consommées pour ne pas être traitées plusieurs fois.
     */
    private void handleInput() {
        if (!game.isGameRunning()) return;

        for (int playerId = 0; playerId < humanPlayerCount && playerId < PLAYER_CONTROLS.length; playerId++) {
            Player player = game.getPlayer(playerId);

            // Ignorer si joueur null ou bot
            if (player == null || player instanceof BotPlayer) {
                System.out.println("Skipping player " + playerId + " - is bot or null");
                continue;
            }

            KeyCode[] controls = PLAYER_CONTROLS[playerId];

            // Gestion des déplacements (haut, bas, gauche, droite)
            if (processedKeys.contains(controls[0])) {
                game.movePlayer(playerId, Direction.UP);
                processedKeys.remove(controls[0]);
            }
            if (processedKeys.contains(controls[1])) {
                game.movePlayer(playerId, Direction.DOWN);
                processedKeys.remove(controls[1]);
            }
            if (processedKeys.contains(controls[2])) {
                game.movePlayer(playerId, Direction.LEFT);
                processedKeys.remove(controls[2]);
            }
            if (processedKeys.contains(controls[3])) {
                game.movePlayer(playerId, Direction.RIGHT);
                processedKeys.remove(controls[3]);
            }

            // Pose de bombe
            if (processedKeys.contains(controls[4])) {
                game.placeBomb(playerId);
                processedKeys.remove(controls[4]);
            }
        }
    }

    /**
     * Demande à chaque bot de décider de son mouvement et d'éventuellement poser une bombe.
     */
    private void handleBots() {
        if (!game.isGameRunning()) return;

        for (int i = 0; i < game.getPlayers().size(); i++) {
            Player player = game.getPlayer(i);
            if (player instanceof BotPlayer) {
                BotPlayer bot = (BotPlayer) player;
                bot.makeMove(game.getBoard(), game.getPlayers());

                if (bot.wantsToPlaceBomb(game.getBoard(), game.getPlayers())) {
                    game.placeBomb(i);
                }
            }
        }
    }

    /**
     * Met à jour le texte de statut affiché à l'écran.
     * Affiche soit le gagnant, soit le nombre de joueurs vivants et les contrôles clavier.
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
            statusText.setStyle("-fx-font-size: 20px; -fx-fill: gold; -fx-font-weight: bold;");
            StackPane.setAlignment(statusText, javafx.geometry.Pos.CENTER);
        } else {
            statusText.setStyle("-fx-font-size: 12px; -fx-fill: white;");
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
     * Quitte la partie en cours et revient au menu principal.
     * Stoppe la boucle de jeu et charge la scène du menu principal.
     */
    private void returnToMainMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
            Parent rootParent = loader.load();
            Scene scene = new Scene(rootParent, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/MainMenu.css").toExternalForm());

            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);

            // Demander le focus clavier pour la nouvelle scène
            scene.getRoot().requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



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

/**
 * Contrôleur principal du jeu Bomberman.
 * Gère l'interface graphique, la boucle de jeu, les entrées clavier,
 * la gestion des joueurs humains et bots, ainsi que la mise à jour de la vue.
 */
public class GameController implements Initializable {

    /** Conteneur principal de l'interface graphique défini dans le FXML. */
    @FXML
    private VBox root;

    /** Canvas sur lequel le jeu est dessiné. */
    @FXML
    private Canvas gameCanvas;

    /** Zone de texte affichant les informations sur le statut du jeu (victoire, nombre de joueurs, etc.). */
    @FXML
    private Text statusText;

    /** Modèle du jeu contenant toute la logique métier et les états du jeu. */
    private Game game;

    /** Vue graphique qui rend le modèle sur le canvas. */
    private GameView gameView;

    /** Boucle d'animation qui met à jour le jeu à environ 60 images par seconde. */
    private AnimationTimer gameLoop;

    /** Ensemble des touches actuellement pressées par l'utilisateur. */
    private Set<KeyCode> pressedKeys;

    /** Ensemble des touches pressées à traiter une seule fois pour éviter les répétitions. */
    private Set<KeyCode> processedKeys;

    /** Nombre de joueurs humains dans la partie, par défaut 2. */
    private int humanPlayerCount = 2;

    /**
     * Tableau des touches clavier pour chaque joueur humain.
     * Chaque sous-tableau contient 5 touches : Haut, Bas, Gauche, Droite, Poser une bombe.
     */
    private static final KeyCode[][] PLAYER_CONTROLS = {
            {KeyCode.Z, KeyCode.S, KeyCode.Q, KeyCode.D, KeyCode.A},            // Joueur 1 : ZQSD + A
            {KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.ENTER}, // Joueur 2 : Flèches + Entrée
            {KeyCode.I, KeyCode.K, KeyCode.J, KeyCode.L, KeyCode.U},            // Joueur 3 : IJKL + U
            {KeyCode.NUMPAD8, KeyCode.NUMPAD5, KeyCode.NUMPAD4, KeyCode.NUMPAD6, KeyCode.NUMPAD0} // Joueur 4 : Pavé numérique + 0
    };

    /**
     * Méthode d'initialisation automatique appelée après le chargement du fichier FXML.
     * Initialise les collections de touches pressées et démarre la création du jeu avec le nombre par défaut de joueurs humains.
     *
     * @param location  URL de localisation (non utilisé ici).
     * @param resources Ressources localisées (non utilisées ici).
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pressedKeys = new HashSet<>();
        processedKeys = new HashSet<>();

        // Initialise la partie avec le nombre défini de joueurs humains.
        setHumanPlayerCount(humanPlayerCount);
        System.out.println("GameController initialized");
    }

    /**
     * Définit le nombre de joueurs humains dans la partie, puis initialise la partie avec ce paramètre.
     *
     * @param humanPlayers Nombre de joueurs humains souhaité.
     */
    public void setHumanPlayerCount(int humanPlayers) {
        this.humanPlayerCount = humanPlayers;
        System.out.println("Setting human player count to: " + humanPlayers);
        initializeGame();
    }

    /**
     * Initialise le jeu en créant le modèle, la vue, la boucle d'animation, et la gestion des événements clavier.
     * Affiche en console des informations sur les types de joueurs (Humain ou Bot).
     */
    private void initializeGame() {
        // Initialise le modèle avec 4 joueurs dont humanPlayerCount humains
        game = new Game(4, humanPlayerCount);
        gameView = new GameView(gameCanvas);

        initializeGameLoop();
        setupKeyHandlers();

        // Donne le focus clavier au conteneur racine pour capter les événements clavier
        root.requestFocus();

        System.out.println("Game initialized with " + humanPlayerCount + " human players");

        // Affiche le type de chaque joueur dans la console
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
     * Initialise et démarre la boucle d'animation qui met à jour le jeu à environ 60 FPS.
     * À chaque frame, elle traite les entrées clavier, déplace les bots, met à jour le modèle, redessine la vue et actualise le statut.
     */
    private void initializeGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                // Frame toutes les ~16.67 ms (60 FPS)
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
     * Configure les gestionnaires d'événements clavier sur le conteneur principal.
     * Permet la détection des touches pressées et relâchées.
     */
    private void setupKeyHandlers() {
        root.setOnKeyPressed(this::onKeyPressed);
        root.setOnKeyReleased(this::onKeyReleased);
        root.setFocusTraversable(true);
    }

    /**
     * Gestionnaire d'événement appelé lors de la pression d'une touche clavier.
     * Ajoute la touche aux ensembles des touches pressées et traite les commandes spéciales (R pour reset, ESC pour revenir au menu).
     *
     * @param event Événement clavier.
     */
    private void onKeyPressed(KeyEvent event) {
        KeyCode key = event.getCode();

        // Ajout aux ensembles seulement si la touche n'est pas déjà présente
        if (!pressedKeys.contains(key)) {
            pressedKeys.add(key);
            processedKeys.add(key);
        }

        // Redémarre la partie si R est pressée
        if (key == KeyCode.R) {
            game.resetGame(humanPlayerCount);
        }

        // Retour au menu principal si ESC est pressé
        if (key == KeyCode.ESCAPE) {
            returnToMainMenu();
        }
    }

    /**
     * Gestionnaire d'événement appelé lors du relâchement d'une touche clavier.
     * Retire la touche des ensembles de touches pressées et traitées.
     *
     * @param event Événement clavier.
     */
    private void onKeyReleased(KeyEvent event) {
        KeyCode key = event.getCode();
        pressedKeys.remove(key);
        processedKeys.remove(key);
    }

    /**
     * Traite les entrées clavier des joueurs humains.
     * Pour chaque joueur humain, déplace le joueur dans la direction correspondant à la touche pressée,
     * ou place une bombe si la touche dédiée est détectée.
     * Les touches sont consommées une seule fois pour éviter le traitement répétitif continu.
     */
    private void handleInput() {
        if (!game.isGameRunning()) return;

        for (int playerId = 0; playerId < humanPlayerCount && playerId < PLAYER_CONTROLS.length; playerId++) {
            Player player = game.getPlayer(playerId);

            // Ignore les bots ou joueurs null
            if (player == null || player instanceof BotPlayer) {
                System.out.println("Skipping player " + playerId + " - is bot or null");
                continue;
            }

            KeyCode[] controls = PLAYER_CONTROLS[playerId];

            // Déplacements
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
     * Demande à chaque bot de décider de son mouvement et de poser une bombe si nécessaire.
     * Appelé à chaque frame de la boucle de jeu.
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
     * Met à jour le texte du statut affiché à l'écran.
     * Affiche le gagnant si la partie est terminée, sinon le nombre de joueurs vivants et les contrôles.
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
     * Interrompt la partie en cours et revient au menu principal.
     * Arrête la boucle d'animation et charge la scène du menu principal.
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

            // Focus sur la scene pour les événements clavier
            scene.getRoot().requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

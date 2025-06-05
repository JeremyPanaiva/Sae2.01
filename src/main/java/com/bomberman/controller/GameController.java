package com.bomberman.controller;

import com.bomberman.model.Game;
import com.bomberman.util.Direction;
import com.bomberman.view.GameView;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class GameController implements Initializable {

    @FXML private VBox root;
    @FXML private Canvas gameCanvas;
    @FXML private Text statusText;

    private Game game;
    private GameView gameView;
    private AnimationTimer gameLoop;
    private Set<KeyCode> pressedKeys;

    // Contrôles des joueurs
    private static final KeyCode[][] PLAYER_CONTROLS = {
            {KeyCode.Z, KeyCode.S, KeyCode.Q, KeyCode.D, KeyCode.A}, // Joueur 1: ZQSD + A pour bombe
            {KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.ENTER}, // Joueur 2: Flèches + Entrée
            {KeyCode.I, KeyCode.K, KeyCode.J, KeyCode.L, KeyCode.U}, // Joueur 3: IJKL + U
            {KeyCode.NUMPAD8, KeyCode.NUMPAD2, KeyCode.NUMPAD4, KeyCode.NUMPAD6, KeyCode.NUMPAD0} // Joueur 4: Pavé numérique
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        game = new Game(4); // 4 joueurs
        gameView = new GameView(gameCanvas);
        pressedKeys = new HashSet<>();

        initializeGameLoop();
        setupKeyHandlers();

        root.requestFocus();
    }

    private void initializeGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 16_666_667) { // ~60 FPS
                    handleInput();
                    game.update();
                    gameView.render(game);
                    updateStatus();
                    lastUpdate = now;
                }
            }
        };
        gameLoop.start();
    }

    private void setupKeyHandlers() {
        root.setOnKeyPressed(this::onKeyPressed);
        root.setOnKeyReleased(this::onKeyReleased);
        root.setFocusTraversable(true);
    }

    private void onKeyPressed(KeyEvent event) {
        pressedKeys.add(event.getCode());

        // Redémarrer le jeu avec R
        if (event.getCode() == KeyCode.R) {
            game.resetGame();
        }
    }

    private void onKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode());
    }

    private void handleInput() {
        if (!game.isGameRunning()) return;

        // Gérer les contrôles pour chaque joueur
        for (int playerId = 0; playerId < Math.min(game.getPlayers().size(), PLAYER_CONTROLS.length); playerId++) {
            KeyCode[] controls = PLAYER_CONTROLS[playerId];

            // Mouvement
            if (pressedKeys.contains(controls[0])) { // Haut
                game.movePlayer(playerId, Direction.UP);
            }
            if (pressedKeys.contains(controls[1])) { // Bas
                game.movePlayer(playerId, Direction.DOWN);
            }
            if (pressedKeys.contains(controls[2])) { // Gauche
                game.movePlayer(playerId, Direction.LEFT);
            }
            if (pressedKeys.contains(controls[3])) { // Droite
                game.movePlayer(playerId, Direction.RIGHT);
            }

            // Bombe
            if (pressedKeys.contains(controls[4])) {
                game.placeBomb(playerId);
                pressedKeys.remove(controls[4]); // Éviter le spam de bombes
            }
        }
    }

    private void updateStatus() {
        if (!game.isGameRunning()) {
            if (game.getWinner() != null) {
                statusText.setText("Joueur " + (game.getWinner().getId() + 1) + " a gagné ! Appuyez sur R pour rejouer.");
            } else {
                statusText.setText("Égalité ! Appuyez sur R pour rejouer.");
            }
        } else {
            long alivePlayers = game.getPlayers().stream().filter(p -> p.isAlive()).count();
            statusText.setText("Joueurs en vie: " + alivePlayers + " | Contrôles: J1(ZQSD+A) J2(Flèches+Entrée) J3(IJKL+U) J4(Pavé+0)");
        }
    }
}
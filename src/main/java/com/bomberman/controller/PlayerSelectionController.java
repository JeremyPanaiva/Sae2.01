package com.bomberman.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Contrôleur pour la sélection du nombre de joueurs dans le jeu Bomberman.
 * Gère les actions des boutons et la sélection du nombre de joueurs.
 */
public class PlayerSelectionController {

    @FXML
    private RadioButton onePlayerRadio; // Bouton radio pour un joueur

    @FXML
    private RadioButton twoPlayersRadio; // Bouton radio pour deux joueurs

    @FXML
    private RadioButton threePlayersRadio; // Bouton radio pour trois joueurs

    @FXML
    private RadioButton fourPlayersRadio; // Bouton radio pour quatre joueurs

    @FXML
    private Button startGameButton; // Bouton pour démarrer le jeu

    @FXML
    private Button backButton; // Bouton pour revenir au menu principal

    private ToggleGroup playerCountGroup; // Groupe de boutons radio pour la sélection du nombre de joueurs

    /**
     * Initialise le contrôleur et configure le groupe de boutons radio.
     */
    @FXML
    private void initialize() {
        playerCountGroup = new ToggleGroup();
        onePlayerRadio.setToggleGroup(playerCountGroup);
        twoPlayersRadio.setToggleGroup(playerCountGroup);
        threePlayersRadio.setToggleGroup(playerCountGroup);
        fourPlayersRadio.setToggleGroup(playerCountGroup);

        // Sélectionner 2 joueurs par défaut
        twoPlayersRadio.setSelected(true);
    }

    /**
     * Gère l'action du bouton pour démarrer le jeu.
     * Charge la scène de jeu avec le nombre de joueurs humains sélectionné.
     *
     * @param event L'événement déclenché par l'action sur le bouton démarrer.
     */
    @FXML
    private void handleStartGame(ActionEvent event) {
        int humanPlayers = getSelectedPlayerCount();
        if (humanPlayers > 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
                Parent root = loader.load();

                // Passer le nombre de joueurs humains au contrôleur de jeu
                GameController gameController = loader.getController();
                gameController.setHumanPlayerCount(humanPlayers);

                Scene scene = new Scene(root, 850, 650);
                scene.getStylesheets().add(getClass().getResource("/css/game.css").toExternalForm());

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();

                // Focus sur la scène pour les événements clavier
                scene.getRoot().requestFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gère l'action du bouton pour revenir au menu principal.
     *
     * @param event L'événement déclenché par l'action sur le bouton retour.
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/MainMenu.css").toExternalForm());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            // Focus sur la scène pour les événements clavier
            scene.getRoot().requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupère le nombre de joueurs sélectionnés.
     *
     * @return Le nombre de joueurs humains sélectionnés.
     */
    private int getSelectedPlayerCount() {
        if (onePlayerRadio.isSelected()) return 1;
        if (twoPlayersRadio.isSelected()) return 2;
        if (threePlayersRadio.isSelected()) return 3;
        if (fourPlayersRadio.isSelected()) return 4;
        return 2; // Valeur par défaut
    }
}

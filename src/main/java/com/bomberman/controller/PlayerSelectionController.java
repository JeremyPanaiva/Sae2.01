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

public class PlayerSelectionController {
    @FXML
    private RadioButton onePlayerRadio;
    @FXML
    private RadioButton twoPlayersRadio;
    @FXML
    private RadioButton threePlayersRadio;
    @FXML
    private RadioButton fourPlayersRadio;
    @FXML
    private Button startGameButton;
    @FXML
    private Button backButton;

    private ToggleGroup playerCountGroup;

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

                // Focus sur la scene pour les événements clavier
                scene.getRoot().requestFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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

            // Focus sur la scene pour les événements clavier
            scene.getRoot().requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getSelectedPlayerCount() {
        if (onePlayerRadio.isSelected()) return 1;
        if (twoPlayersRadio.isSelected()) return 2;
        if (threePlayersRadio.isSelected()) return 3;
        if (fourPlayersRadio.isSelected()) return 4;
        return 2; // Défaut
    }
}
package com.bomberman.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Contrôleur pour le menu principal du jeu Bomberman.
 * Gère les actions des boutons du menu principal.
 */
public class MainMenuController {

    @FXML
    private Button Quit; // Bouton pour quitter le jeu

    @FXML
    private Button Play; // Bouton pour commencer à jouer

    @FXML
    private Button Avatar; // Bouton pour accéder à la sélection d'avatar

    /**
     * Ferme l'application lorsque le bouton Quitter est actionné.
     *
     * @param event L'événement déclenché par l'action sur le bouton Quitter.
     */
    @FXML
    private void Quitter(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Charge la scène de sélection des joueurs lorsque le bouton Jouer est actionné.
     *
     * @param event L'événement déclenché par l'action sur le bouton Jouer.
     */
    @FXML
    private void handlePlay(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/playerSelection.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/playerSelection.css").toExternalForm());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

            // Focus sur la scène pour les événements clavier
            scene.getRoot().requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la sélection de joueurs: " + e.getMessage());
        }
    }

    /**
     * Charge la scène de sélection d'avatar lorsque le bouton Avatar est actionné.
     *
     * @param event L'événement déclenché par l'action sur le bouton Avatar.
     */
    @FXML
    private void handleAvatar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Avatar.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/Avatar.css").toExternalForm());

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

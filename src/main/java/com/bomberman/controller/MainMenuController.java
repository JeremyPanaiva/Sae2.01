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

public class MainMenuController {

    //appel les bouton du FXML
    @FXML
    private Button Quit;
    @FXML
    private Button Play;
    @FXML
    private Button Avatar;

    //fonction pour quitter le jeu une fois appyer sur le bouton quit
    @FXML
    private void Quitter (ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        //ferme la fenêtre
        stage.close();
    }

    @FXML
    //charge le jeu une fois appyer sur le bouton play
    private void handlePlay(ActionEvent event) {
        try {
            //chager le FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
            Parent root = loader.load();
            //défini la taille de la fenêtre et la créer
            Scene scene = new Scene(root, 900, 700);
            //charge le css
            scene.getStylesheets().add(getClass().getResource("/css/game.css").toExternalForm());

            //définit la nouvelle scene
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


    @FXML
    //charge le jeu une fois appyer sur le bouton play
    private void handleAvatar(ActionEvent event) {
        try {
            //chager le FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Avatar.fxml"));
            Parent root = loader.load();
            //défini la taille de la fenêtre et la créer
            Scene scene = new Scene(root, 800, 600);
            //charge le css
            scene.getStylesheets().add(getClass().getResource("/css/Avatar.css").toExternalForm());

            //définit la nouvelle scene
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

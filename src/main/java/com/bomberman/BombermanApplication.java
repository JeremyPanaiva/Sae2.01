package com.bomberman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BombermanApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/MainMenu.css").toExternalForm());

        stage.setTitle("Bomberman");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // Focus sur la scene pour les événements clavier
        scene.getRoot().requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
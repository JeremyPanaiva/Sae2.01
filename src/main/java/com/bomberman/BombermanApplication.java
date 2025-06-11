package com.bomberman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale de l'application Bomberman.
 * Cette classe étend JavaFX Application et est le point d'entrée de l'application.
 */
public class BombermanApplication extends Application {

    /**
     * Méthode appelée au démarrage de l'application JavaFX.
     * Charge le menu principal et configure la scène initiale.
     *
     * @param stage Le stage principal de l'application JavaFX.
     * @throws Exception Si le chargement du fichier FXML échoue.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Charger le fichier FXML pour le menu principal
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);

        // Ajouter une feuille de style CSS pour le menu principal
        scene.getStylesheets().add(getClass().getResource("/css/MainMenu.css").toExternalForm());

        // Configurer le stage
        stage.setTitle("Bomberman");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // Mettre le focus sur la scène pour capturer les événements clavier
        scene.getRoot().requestFocus();
    }

    /**
     * Méthode principale pour lancer l'application.
     *
     * @param args Les arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        launch(args);
    }
}

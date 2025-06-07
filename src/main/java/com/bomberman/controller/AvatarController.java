package com.bomberman.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class AvatarController implements Initializable {
    @FXML
    private TextField speudo1;
   /* @FXML
    private TextField speudo2;
    @FXML
    private TextField speudo3;
    @FXML
    private TextField speudo4;*/

    private static final String PSEUDO_KEY_1 = "pseudo1";
   /* private static final String PSEUDO_KEY_2 = "pseudo2";
    private static final String PSEUDO_KEY_3 = "pseudo3";
    private static final String PSEUDO_KEY_4 = "pseudo4";*/

    private static final String MATCHES_KEY_1 = "matches1";
    /*private static final String MATCHES_KEY_2 = "matches2";
    private static final String MATCHES_KEY_3 = "matches3";
    private static final String MATCHES_KEY_4 = "matches4";*/

    private static final String MATCHES_WON_KEY_1 = "matchesWon1";
    /*private static final String MATCHES_WON_KEY_2 = "matchesWon2";
    private static final String MATCHES_WON_KEY_3 = "matchesWon3";
    private static final String MATCHES_WON_KEY_4 = "matchesWon4";*/

    private Map<String, Integer> totalMatches;
    private Map<String, Integer> matchesWon;


    @FXML
    private Label nbMatch;
    @FXML
    private Label nbMatchGagner;
    @FXML
    private Button closeButton;

    @FXML
    private void closeButtonAction(ActionEvent event) {
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

    @FXML
    private void savePseudos() {
        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
        prefs.put(PSEUDO_KEY_1, speudo1.getText());
       /* prefs.put(PSEUDO_KEY_2, speudo2.getText());
        prefs.put(PSEUDO_KEY_3, speudo3.getText());
        prefs.put(PSEUDO_KEY_4, speudo4.getText());*/

        //score
        prefs.putInt(MATCHES_KEY_1, totalMatches.getOrDefault(speudo1.getText(), 0));
        prefs.putInt(MATCHES_WON_KEY_1, matchesWon.getOrDefault(speudo1.getText(), 0));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        totalMatches = new HashMap<>();
        matchesWon = new HashMap<>();

        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
        speudo1.setText(prefs.get(PSEUDO_KEY_1, ""));
        /*speudo2.setText(prefs.get(PSEUDO_KEY_2, ""));
        speudo3.setText(prefs.get(PSEUDO_KEY_3, ""));
        speudo4.setText(prefs.get(PSEUDO_KEY_4, ""));*/

        String pseudo = speudo1.getText();
        totalMatches.put(pseudo, prefs.getInt(MATCHES_KEY_1, 0));
        matchesWon.put(pseudo, prefs.getInt(MATCHES_WON_KEY_1, 0));

        // Afficher les scores
        nbMatch.setText(String.valueOf(totalMatches.getOrDefault(pseudo, 0)));
        nbMatchGagner.setText(String.valueOf(matchesWon.getOrDefault(pseudo, 0)));

    }


}

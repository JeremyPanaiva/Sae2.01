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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class AvatarController implements Initializable {
    @FXML
    private TextField speudo1;
    @FXML
    private TextField speudo2;
   /* @FXML
    private TextField speudo3;
    @FXML
    private TextField speudo4;*/

    @FXML
    public Label nbMatchGagner;
    @FXML
    public Label nbMatch;

    @FXML
    private ImageView avatarImageView;
    @FXML
    private Button choisirImage;
    private Stage stage;


    private static final String PSEUDO_KEY_1 = "pseudo1";
    private static final String PSEUDO_KEY_2 = "pseudo2";
    /*private static final String PSEUDO_KEY_3 = "pseudo3";
    private static final String PSEUDO_KEY_4 = "pseudo4";*/

    public static final String TOTAL_MATCH_KEY = "nbMatch";
    public static final String TOTAL_MATCH_GAGNER_KEY1 = "nbMatchGagner1";
    public static final String TOTAL_MATCH_GAGNER_KEY2 = "nbMatchGager2";
    /*public static final String TOTAL_MATCH_GAGNER_KEY3 = "nbMatchGager3";
    public static final String TOTAL_MATCH_GAGNER_KEY4 = "nbMatchGager4";*/

    private static final String IMAGE_PATH_KEY1 = "imageChoisi1";
    private static final String IMAGE_PATH_KEY2 = "imageChoisi2";
    /*private static final String IMAGE_PATH_KEY3 = "imageChoisi3";
    private static final String IMAGE_PATH_KEY4 = "imageChoisi4";*/


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
        prefs.put(PSEUDO_KEY_2, speudo2.getText());
       /* prefs.put(PSEUDO_KEY_3, speudo3.getText());
        prefs.put(PSEUDO_KEY_4, speudo4.getText());*/

    }

    public static void incrementTotalMatch() {
        Preferences matchPrefs = Preferences.userRoot().node(AvatarController.class.getSimpleName());
        int total = matchPrefs.getInt(TOTAL_MATCH_KEY, 0);
        matchPrefs.putInt(TOTAL_MATCH_KEY, total + 1);
        System.out.println(total);
    }

    public static void incrementNbMatchGagner(int PlayerGagnant) {
        Preferences matchGagnerPrefs = Preferences.userRoot().node(AvatarController.class.getName());
        String key = TOTAL_MATCH_GAGNER_KEY1; // Utilisez la clé appropriée en fonction du joueur
        int wins = matchGagnerPrefs.getInt(key, 0);
        matchGagnerPrefs.putInt(key, wins + 1);
        System.out.println("Player " + PlayerGagnant + " wins: " + (wins + 1));
    }

    @FXML
    private void changerImage(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("choisir une image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            avatarImageView.setImage(image);
        }

        Preferences ImagePref = Preferences.userRoot().node(this.getClass().getName());
        ImagePref.put(IMAGE_PATH_KEY1, file.getAbsolutePath());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //speudo
        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
        speudo1.setText(prefs.get(PSEUDO_KEY_1, ""));
        /*speudo2.setText(prefs.get(PSEUDO_KEY_2, ""));
        speudo3.setText(prefs.get(PSEUDO_KEY_3, ""));
        speudo4.setText(prefs.get(PSEUDO_KEY_4, ""));*/

        //competeur match total
        Preferences matchPrefs = Preferences.userRoot().node(AvatarController.class.getSimpleName());
        int total = matchPrefs.getInt(TOTAL_MATCH_KEY, 0);
        nbMatch.setText(String.valueOf("nombre matchs total : " + total));

        //compteur match gagner
        Preferences matchGagnerPrefs = Preferences.userRoot().node(AvatarController.class.getName());
        int WinsPlayer = matchGagnerPrefs.getInt(TOTAL_MATCH_GAGNER_KEY1, 0);
        nbMatchGagner.setText(String.valueOf("nombre match gagner : " + WinsPlayer));

        //charger image
        Preferences ImagePref = Preferences.userRoot().node(this.getClass().getName());
        String imagePath = ImagePref.get(IMAGE_PATH_KEY1, null);
        if (imagePath != null) {
            try {
                Image Saveimage = new Image(new File(imagePath).toURI().toString());
                avatarImageView.setImage(Saveimage);
            } catch (Exception e) {
                Image defaultImage = new Image(this.getClass().getClassLoader().getResourceAsStream("/image/background.png").toString());
                avatarImageView.setImage(defaultImage);
            }
        } else {
            Image defaultImage = new Image(getClass().getResourceAsStream("/image/background.png"));
            avatarImageView.setImage(defaultImage);
        }
    }


}

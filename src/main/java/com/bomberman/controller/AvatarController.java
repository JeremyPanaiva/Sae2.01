package com.bomberman.controller;

import com.bomberman.model.Texture;
import com.bomberman.view.GameView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class AvatarController implements Initializable {
    // initialise les textfiel pour les speudo
    @FXML
    private TextField speudo1;
    @FXML
    private TextField speudo2;
    @FXML
    private TextField speudo3;
    @FXML
    private TextField speudo4;

    // initialise Les Labels pour les compteurs de matchs totals
    @FXML
    public Label nbMatchGagner1;
    @FXML
    public Label nbMatchGagner2;
    @FXML
    public Label nbMatchGagner3;
    @FXML
    public Label nbMatchGagner4;

    // initialise les compteurs nombre de matchs gagné
    @FXML
    public Label nbMatch1;
    @FXML
    public Label nbMatch2;
    @FXML
    public Label nbMatch3;
    @FXML
    public Label nbMatch4;

    // initialise les images des Avatars
    @FXML
    private ImageView avatarImageView1;
    @FXML
    private ImageView avatarImageView2;
    @FXML
    private ImageView avatarImageView3;
    @FXML
    private ImageView avatarImageView4;

    // initialise le ressource pack
    @FXML
    private ComboBox<Texture> texturePackComboBox;
    @FXML
    private Button sauvegarderTexture;

    @FXML
    private Button choisirImage;
    private Stage stage;

    public  List<Texture> textures;
    private GameView gameView;


    //créer les clef pour les pref utilisateur
    private static final String PSEUDO_KEY_1 = "pseudo1";
    private static final String PSEUDO_KEY_2 = "pseudo2";
    private static final String PSEUDO_KEY_3 = "pseudo3";
    private static final String PSEUDO_KEY_4 = "pseudo4";

    public static final String TOTAL_MATCH_KEY1 = "nbMatch1";
    public static final String TOTAL_MATCH_KEY2 = "nbMatch2";
    public static final String TOTAL_MATCH_KEY3 = "nbMatch3";
    public static final String TOTAL_MATCH_KEY4 = "nbMatch4";
    public static final String TOTAL_MATCH_GAGNER_KEY1 = "nbMatchGagner1";
    public static final String TOTAL_MATCH_GAGNER_KEY2 = "nbMatchGager2";
    public static final String TOTAL_MATCH_GAGNER_KEY3 = "nbMatchGager3";
    public static final String TOTAL_MATCH_GAGNER_KEY4 = "nbMatchGager4";

    private static final String IMAGE_PATH_KEY1 = "imageChoisi1";
    private static final String IMAGE_PATH_KEY2 = "imageChoisi2";
    private static final String IMAGE_PATH_KEY3 = "imageChoisi3";
    private static final String IMAGE_PATH_KEY4 = "imageChoisi4";

    private static final String DEFAULT_IMAGE_1 = "/image/Avatar1.png";
    private static final String DEFAULT_IMAGE_2 = "/image/Avatar2.png";
    private static final String DEFAULT_IMAGE_3 = "/image/Avatar3.png";
    private static final String DEFAULT_IMAGE_4 = "/image/Avatar4.png";

    public static final String TEXTURE_PACK_KEY = "texturePack";

    @FXML
    private Button closeButton;

    @FXML
    //charge le jeu une fois appyer sur le bouton close
    private void closeButtonAction(ActionEvent event) {
        try {
            //chager le FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
            Parent root = loader.load();
            //défini la taille de la fenêtre et la créer
            Scene scene = new Scene(root, 800, 600);
            //charge le css
            scene.getStylesheets().add(getClass().getResource("/css/MainMenu.css").toExternalForm());

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
    //permet de sauvegarder les speudo
    private void savePseudos() {
        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
        prefs.put(PSEUDO_KEY_1, speudo1.getText());
        prefs.put(PSEUDO_KEY_2, speudo2.getText());
        prefs.put(PSEUDO_KEY_3, speudo3.getText());
        prefs.put(PSEUDO_KEY_4, speudo4.getText());

    }

    //incrémenter le nombre de match total
    public static void incrementTotalMatch(String key) {
        Preferences matchPrefs = Preferences.userRoot().node(AvatarController.class.getSimpleName());
        int total = matchPrefs.getInt(key, 0);
        matchPrefs.putInt(key, total + 1);
    }

    //incrémente le nombre de match gagné
    public static void incrementNbMatchGagner(int playerNumber) {
        Preferences matchGagnerPrefs = Preferences.userRoot().node(AvatarController.class.getName());
        //rajoute +1 en fonction de l'ID
        String key = switch (playerNumber) {
            case 0 -> TOTAL_MATCH_GAGNER_KEY1;
            case 1 -> TOTAL_MATCH_GAGNER_KEY2;
            case 2 -> TOTAL_MATCH_GAGNER_KEY3;
            case 3 -> TOTAL_MATCH_GAGNER_KEY4;
            default -> throw new IllegalStateException("Unexpected value: " + playerNumber);
        };

        int wins = matchGagnerPrefs.getInt(key, 0);
        matchGagnerPrefs.putInt(key, wins + 1);
        System.out.println("Player " + playerNumber + " wins: " + (wins + 1));
    }

    @FXML
    //permet de charger les image
    private void changerImage1() {
        selectImageForPlayer(avatarImageView1, IMAGE_PATH_KEY1);
    }

    @FXML
    //permet de charger les image
    private void changerImage2() {
        selectImageForPlayer(avatarImageView2, IMAGE_PATH_KEY2);
    }

    @FXML
    //permet de charger les image
    private void changerImage3() {
        selectImageForPlayer(avatarImageView3, IMAGE_PATH_KEY3);
    }

    @FXML
    //permet de charger les image
    private void changerImage4() {
        selectImageForPlayer(avatarImageView4, IMAGE_PATH_KEY4);
    }


    //permet de selectionner une image pour un joueur
    private void selectImageForPlayer(ImageView imageView, String key) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("choisir une image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
        }

        Preferences ImagePref = Preferences.userRoot().node(this.getClass().getName());
        ImagePref.put(key, file.getAbsolutePath());
    }

    //permet de charger soir l'image selectionner soit l'image par défaut
    private void loadImage(ImageView imageView, String key, String defaultImage) {
        Preferences ImagePref = Preferences.userRoot().node(this.getClass().getName());
        String imagePath = ImagePref.get(key, null);
        if (imagePath != null) {
            try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                imageView.setImage(image);
                return;
            }
            } catch (Exception e) {
                System.err.println("erreur charger une image");
            }
        }

        try{
            Image defaultImages = new Image(getClass().getResourceAsStream(defaultImage));
            imageView.setImage(defaultImages);
        } catch (Exception e) {
            System.err.println("erreur charger une image");
        }
    }

    //charge les pack de texture disponible
    private void loadTexturePack() {
        textures = new ArrayList<>();
        textures.add(new Texture("defaut", "/image/defaut/"));
        textures.add(new Texture("mario", "/image/mario/"));
        texturePackComboBox.setItems(FXCollections.observableArrayList(textures));

        Preferences TexturePref = Preferences.userRoot().node(this.getClass().getName());
        String textureName = TexturePref.get(TEXTURE_PACK_KEY, "defaut");

        for(Texture texture : textures) {
            if(texture.getNom().equals(textureName)) {
                texturePackComboBox.getSelectionModel().select(texture);
                break;
            }
        }
    }

    @FXML
    //sauvegarde le pack de texture pour le charger plus tard
    private void sauvegarderTexturePack() {
        Texture selectedTexture = texturePackComboBox.getSelectionModel().getSelectedItem();
        if(selectedTexture != null) {
            Preferences TexturePref = Preferences.userRoot().node(this.getClass().getName());
            TexturePref.put(TEXTURE_PACK_KEY, selectedTexture.getNom());
        }
    }


    //méthode d'initilisation charger automatiquement
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //speudo
        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
        speudo1.setText(prefs.get(PSEUDO_KEY_1, ""));
        speudo2.setText(prefs.get(PSEUDO_KEY_2, ""));
        speudo3.setText(prefs.get(PSEUDO_KEY_3, ""));
        speudo4.setText(prefs.get(PSEUDO_KEY_4, ""));

        //competeur match total
        Preferences matchPrefs = Preferences.userRoot().node(AvatarController.class.getSimpleName());
        int total = matchPrefs.getInt(TOTAL_MATCH_KEY1, 0);
        int total2 = matchPrefs.getInt(TOTAL_MATCH_KEY2, 0);
        int total3 = matchPrefs.getInt(TOTAL_MATCH_KEY3, 0);
        int total4 = matchPrefs.getInt(TOTAL_MATCH_KEY4, 0);
        nbMatch1.setText(String.valueOf("nombre matchs total : " + total));
        nbMatch2.setText(String.valueOf("nombre matchs total : " + total2));
        nbMatch3.setText(String.valueOf("nombre matchs total : " + total3));
        nbMatch4.setText(String.valueOf("nombre matchs total : " + total4));

        //compteur match gagner
        Preferences matchGagnerPrefs = Preferences.userRoot().node(AvatarController.class.getName());
        int WinsPlayer = matchGagnerPrefs.getInt(TOTAL_MATCH_GAGNER_KEY1, 0);
        int WinsPlayer2 = matchGagnerPrefs.getInt(TOTAL_MATCH_GAGNER_KEY2, 0);
        int WinsPlayer3 = matchGagnerPrefs.getInt(TOTAL_MATCH_GAGNER_KEY3, 0);
        int Winsplayer4 = matchGagnerPrefs.getInt(TOTAL_MATCH_GAGNER_KEY4, 0);
        nbMatchGagner1.setText(String.valueOf("nombre matchs gagné : " + WinsPlayer));
        nbMatchGagner2.setText(String.valueOf("nombre matchs gagné : " + WinsPlayer2));
        nbMatchGagner3.setText(String.valueOf("nombre matchs gagné : " + WinsPlayer3));
        nbMatchGagner4.setText(String.valueOf("nombre matchs gagné : " + Winsplayer4));


        //charger image
        loadImage(avatarImageView1, IMAGE_PATH_KEY1, DEFAULT_IMAGE_1);
        loadImage(avatarImageView2, IMAGE_PATH_KEY2, DEFAULT_IMAGE_2);
        loadImage(avatarImageView3, IMAGE_PATH_KEY3, DEFAULT_IMAGE_3);
        loadImage(avatarImageView4, IMAGE_PATH_KEY4, DEFAULT_IMAGE_4);

        //changer de Texture
        loadTexturePack();

    }
}
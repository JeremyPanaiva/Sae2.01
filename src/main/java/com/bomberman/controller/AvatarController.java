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

/**
 * Contrôleur pour la gestion des avatars et des pseudos des joueurs.
 * Permet la gestion des images d'avatars personnalisées, des pseudos,
 * des compteurs de matchs joués et gagnés, ainsi que la sélection d'un pack de textures.
 */

public class AvatarController implements Initializable {
    /** TextField pour le pseudo du joueur 1 */
    @FXML
    private TextField speudo1;
    /** TextField pour le pseudo du joueur 2 */
    @FXML
    private TextField speudo2;
    /** TextField pour le pseudo du joueur 3 */
    @FXML
    private TextField speudo3;
    /** TextField pour le pseudo du joueur 4 */
    @FXML
    private TextField speudo4;

    /** Label affichant le nombre total de matchs gagnés par le joueur 1 */
    @FXML
    public Label nbMatchGagner1;
    /** Label affichant le nombre total de matchs gagnés par le joueur 2 */
    @FXML
    public Label nbMatchGagner2;
    /** Label affichant le nombre total de matchs gagnés par le joueur 3 */
    @FXML
    public Label nbMatchGagner3;
    /** Label affichant le nombre total de matchs gagnés par le joueur 4 */
    @FXML
    public Label nbMatchGagner4;

    /** Label affichant le nombre total de matchs joués par le joueur 1 */
    @FXML
    public Label nbMatch1;
    /** Label affichant le nombre total de matchs joués par le joueur 2 */
    @FXML
    public Label nbMatch2;
    /** Label affichant le nombre total de matchs joués par le joueur 3 */
    @FXML
    public Label nbMatch3;
    /** Label affichant le nombre total de matchs joués par le joueur 4 */
    @FXML
    public Label nbMatch4;

    /** ImageView affichant l'avatar du joueur 1 */
    @FXML
    private ImageView avatarImageView1;
    /** ImageView affichant l'avatar du joueur 2 */
    @FXML
    private ImageView avatarImageView2;
    /** ImageView affichant l'avatar du joueur 3 */
    @FXML
    private ImageView avatarImageView3;
    /** ImageView affichant l'avatar du joueur 4 */
    @FXML
    private ImageView avatarImageView4;


    /** ComboBox pour la sélection du pack de textures */
    @FXML
    private ComboBox<Texture> texturePackComboBox;
    /** Bouton pour sauvegarder le pack de textures sélectionné */
    @FXML
    private Button sauvegarderTexture;

    /** Bouton pour réinitialiser les paramètres à leurs valeurs par défaut */
    @FXML
    private Button reset;

    /** Bouton pour choisir une image d'avatar personnalisée */
    @FXML
    private Button choisirImage;
    /** Stage de la fenêtre courante */
    private Stage stage;

    /** Liste des packs de textures disponibles */
    public List<Texture> textures;
    /** Vue du jeu (non utilisée dans ce contrôleur) */
    private GameView gameView;

    // Clés pour les préférences utilisateur (pseudos)
    private static final String PSEUDO_KEY_1 = "pseudo1";
    private static final String PSEUDO_KEY_2 = "pseudo2";
    private static final String PSEUDO_KEY_3 = "pseudo3";
    private static final String PSEUDO_KEY_4 = "pseudo4";

    // Clés pour le nombre total de matchs joués
    public static final String TOTAL_MATCH_KEY1 = "nbMatch1";
    public static final String TOTAL_MATCH_KEY2 = "nbMatch2";
    public static final String TOTAL_MATCH_KEY3 = "nbMatch3";
    public static final String TOTAL_MATCH_KEY4 = "nbMatch4";
    // Clés pour le nombre de matchs gagnés
    public static final String TOTAL_MATCH_GAGNER_KEY1 = "nbMatchGagner1";
    public static final String TOTAL_MATCH_GAGNER_KEY2 = "nbMatchGagner2";
    public static final String TOTAL_MATCH_GAGNER_KEY3 = "nbMatchGagner3";
    public static final String TOTAL_MATCH_GAGNER_KEY4 = "nbMatchGagner4";

    // Clés pour le chemin des images personnalisées
    private static final String IMAGE_PATH_KEY1 = "imageChoisi1";
    private static final String IMAGE_PATH_KEY2 = "imageChoisi2";
    private static final String IMAGE_PATH_KEY3 = "imageChoisi3";
    private static final String IMAGE_PATH_KEY4 = "imageChoisi4";

    // Chemins des images par défaut des avatars
    private static final String DEFAULT_IMAGE_1 = "/image/Avatar1.png";
    private static final String DEFAULT_IMAGE_2 = "/image/Avatar2.png";
    private static final String DEFAULT_IMAGE_3 = "/image/Avatar3.png";
    private static final String DEFAULT_IMAGE_4 = "/image/Avatar4.png";

    // Clé pour le pack de texture sélectionné
    public static final String TEXTURE_PACK_KEY = "texturePack";

    /** Bouton pour fermer la fenêtre */
    @FXML
    private Button closeButton;

    /**
     * Action déclenchée lors du clic sur le bouton de fermeture.
     * Sauvegarde les pseudos et le pack de textures, puis charge la scène du menu principal.
     *
     * @param event L'événement ActionEvent déclenché par le clic.
     */
    @FXML
    private void closeButtonAction(ActionEvent event) {
        try {
            // Sauvegarder avant de quitter
            savePseudos();
            sauvegarderTexturePack();

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

    /**
     * Sauvegarde les pseudos des joueurs dans les préférences utilisateur.
     */
    @FXML
    private void savePseudos() {
        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
        prefs.put(PSEUDO_KEY_1, speudo1.getText());
        prefs.put(PSEUDO_KEY_2, speudo2.getText());
        prefs.put(PSEUDO_KEY_3, speudo3.getText());
        prefs.put(PSEUDO_KEY_4, speudo4.getText());
    }

    /**
     * Incrémente le nombre total de matchs joués pour une clé donnée.
     *
     * @param key La clé représentant le joueur (ex : TOTAL_MATCH_KEY1).
     */
    public static void incrementTotalMatch(String key) {
        Preferences matchPrefs = Preferences.userRoot().node(AvatarController.class.getName());
        int total = matchPrefs.getInt(key, 0);
        matchPrefs.putInt(key, total + 1);
    }

    /**
     * Incrémente le nombre de matchs gagnés pour un joueur donné.
     *
     * @param playerNumber Le numéro du joueur (0 à 3).
     * @throws IllegalStateException Si le numéro de joueur est invalide.
     */
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

    /**
     * Permet à l'utilisateur de sélectionner une image d'avatar pour le joueur 1.
     */
    @FXML
    private void changerImage1() {
        selectImageForPlayer(avatarImageView1, IMAGE_PATH_KEY1);
    }

    /**
     * Permet à l'utilisateur de sélectionner une image d'avatar pour le joueur 2.
     */
    @FXML
    private void changerImage2() {
        selectImageForPlayer(avatarImageView2, IMAGE_PATH_KEY2);
    }

    /**
     * Permet à l'utilisateur de sélectionner une image d'avatar pour le joueur 3.
     */
    @FXML
    private void changerImage3() {
        selectImageForPlayer(avatarImageView3, IMAGE_PATH_KEY3);
    }

    /**
     * Permet à l'utilisateur de sélectionner une image d'avatar pour le joueur 4.
     */
    @FXML
    private void changerImage4() {
        selectImageForPlayer(avatarImageView4, IMAGE_PATH_KEY4);
    }

    /**
     * Ouvre une boîte de dialogue pour choisir une image et l'affecte à l'ImageView spécifié.
     * Enregistre le chemin de l'image sélectionnée dans les préférences utilisateur.
     *
     * @param imageView L'ImageView à mettre à jour.
     * @param preferenceKey La clé de préférence pour enregistrer le chemin de l'image.
     */
    private void selectImageForPlayer(ImageView imageView, String key) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("choisir une image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                Image image = new Image(file.toURI().toString());
                if (!image.isError()) {
                    imageView.setImage(image);
                    // Sauvegarder le chemin seulement si l'image se charge correctement
                    Preferences ImagePref = Preferences.userRoot().node(this.getClass().getName());
                    ImagePref.put(key, file.getAbsolutePath());
                } else {
                    System.err.println("Erreur: Image corrompue ou format non supporté");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
            }
        }
    }

    /**
     * Initialise le contrôleur, charge les pseudos et images enregistrés,
     * initialise les compteurs de matchs et configure la ComboBox des packs de textures.
     *
     * @param location  L'emplacement utilisé pour résoudre les chemins relatifs, ou null si inconnu.
     * @param resources Les ressources utilisées pour localiser les chaînes de caractères, ou null si indisponible.
     */
    private void loadImage(ImageView imageView, String key, String defaultImage) {
        Preferences ImagePref = Preferences.userRoot().node(this.getClass().getName());
        String imagePath = ImagePref.get(key, null);

        // Essayer de charger l'image personnalisée
        if (imagePath != null) {
            try {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    if (!image.isError()) {
                        imageView.setImage(image);
                        return;
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image personnalisée: " + e.getMessage());
            }
        }

        // Charger l'image par défaut si l'image personnalisée échoue
        try {
            Image defaultImages = new Image(getClass().getResourceAsStream(defaultImage));
            if (!defaultImages.isError()) {
                imageView.setImage(defaultImages);
            } else {
                System.err.println("Erreur: Image par défaut non trouvée: " + defaultImage);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image par défaut: " + e.getMessage());
        }
    }


    private void loadTexturePack() {
        textures = new ArrayList<>();
        textures.add(new Texture("defaut", "/image/defaut/"));
        textures.add(new Texture("mario", "/image/mario/"));
        // Ajoutez d'autres packs de texture ici si nécessaire

        texturePackComboBox.setItems(FXCollections.observableArrayList(textures));

        // Charger la texture sauvegardée
        Preferences TexturePref = Preferences.userRoot().node(this.getClass().getName());
        String textureName = TexturePref.get(TEXTURE_PACK_KEY, "defaut");

        // Sélectionner la texture correspondante
        for (Texture texture : textures) {
            if (texture.getNom().equals(textureName)) {
                texturePackComboBox.getSelectionModel().select(texture);
                break;
            }
        }

        // Si aucune texture trouvée, sélectionner la première (défaut)
        if (texturePackComboBox.getSelectionModel().getSelectedItem() == null) {
            texturePackComboBox.getSelectionModel().selectFirst();
        }
    }


    @FXML
    private void sauvegarderTexturePack() {
        Texture selectedTexture = texturePackComboBox.getSelectionModel().getSelectedItem();
        if (selectedTexture != null) {
            Preferences TexturePref = Preferences.userRoot().node(this.getClass().getName());
            TexturePref.put(TEXTURE_PACK_KEY, selectedTexture.getNom());
            System.out.println("Pack de texture sauvegardé: " + selectedTexture.getNom());
        }
    }

    //méthode d'initilisation chargée automatiquement
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //speudo
        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
        speudo1.setText(prefs.get(PSEUDO_KEY_1, "Joueur 1"));
        speudo2.setText(prefs.get(PSEUDO_KEY_2, "Joueur 2"));
        speudo3.setText(prefs.get(PSEUDO_KEY_3, "Joueur 3"));
        speudo4.setText(prefs.get(PSEUDO_KEY_4, "Joueur 4"));

        //compteur match total
        Preferences matchPrefs = Preferences.userRoot().node(AvatarController.class.getName());
        int total = matchPrefs.getInt(TOTAL_MATCH_KEY1, 0);
        int total2 = matchPrefs.getInt(TOTAL_MATCH_KEY2, 0);
        int total3 = matchPrefs.getInt(TOTAL_MATCH_KEY3, 0);
        int total4 = matchPrefs.getInt(TOTAL_MATCH_KEY4, 0);
        nbMatch1.setText("Nombre matchs total : " + total);
        nbMatch2.setText("Nombre matchs total : " + total2);
        nbMatch3.setText("Nombre matchs total : " + total3);
        nbMatch4.setText("Nombre matchs total : " + total4);

        //compteur match gagné
        Preferences matchGagnerPrefs = Preferences.userRoot().node(AvatarController.class.getName());
        int WinsPlayer = matchGagnerPrefs.getInt(TOTAL_MATCH_GAGNER_KEY1, 0);
        int WinsPlayer2 = matchGagnerPrefs.getInt(TOTAL_MATCH_GAGNER_KEY2, 0);
        int WinsPlayer3 = matchGagnerPrefs.getInt(TOTAL_MATCH_GAGNER_KEY3, 0);
        int Winsplayer4 = matchGagnerPrefs.getInt(TOTAL_MATCH_GAGNER_KEY4, 0);
        nbMatchGagner1.setText("Nombre matchs gagné : " + WinsPlayer);
        nbMatchGagner2.setText("Nombre matchs gagné : " + WinsPlayer2);
        nbMatchGagner3.setText("Nombre matchs gagné : " + WinsPlayer3);
        nbMatchGagner4.setText("Nombre matchs gagné : " + Winsplayer4);

        //charger images
        loadImage(avatarImageView1, IMAGE_PATH_KEY1, DEFAULT_IMAGE_1);
        loadImage(avatarImageView2, IMAGE_PATH_KEY2, DEFAULT_IMAGE_2);
        loadImage(avatarImageView3, IMAGE_PATH_KEY3, DEFAULT_IMAGE_3);
        loadImage(avatarImageView4, IMAGE_PATH_KEY4, DEFAULT_IMAGE_4);

        //charger pack de texture
        loadTexturePack();
    }

    @FXML
    public void resetDefaults(){
        //reset text
        speudo1.setText("");
        speudo2.setText("");
        speudo3.setText("");
        speudo4.setText("");

        //reset compteur
        nbMatch1.setText("");
        nbMatch2.setText("");
        nbMatch3.setText("");
        nbMatch4.setText("");

        nbMatchGagner1.setText("");
        nbMatchGagner2.setText("");
        nbMatchGagner3.setText("");
        nbMatchGagner4.setText("");

        //reset image
        loadImage(avatarImageView1, IMAGE_PATH_KEY1, DEFAULT_IMAGE_1);
        loadImage(avatarImageView2, IMAGE_PATH_KEY2, DEFAULT_IMAGE_2);
        loadImage(avatarImageView3, IMAGE_PATH_KEY3, DEFAULT_IMAGE_3);
        loadImage(avatarImageView4, IMAGE_PATH_KEY4, DEFAULT_IMAGE_4);

        reserPref();
    }

    private void reserPref(){
        Preferences Prefs = Preferences.userRoot().node(this.getClass().getName());
        Prefs.remove(PSEUDO_KEY_1);
        Prefs.remove(PSEUDO_KEY_2);
        Prefs.remove(PSEUDO_KEY_3);
        Prefs.remove(PSEUDO_KEY_4);

        Preferences matchPrefs = Preferences.userRoot().node(AvatarController.class.getName());
        matchPrefs.putInt(TOTAL_MATCH_KEY1, 0);
        matchPrefs.putInt(TOTAL_MATCH_KEY2, 0);
        matchPrefs.putInt(TOTAL_MATCH_KEY3, 0);
        matchPrefs.putInt(TOTAL_MATCH_KEY4, 0);

        Preferences matchGagnerPrefs = Preferences.userRoot().node(AvatarController.class.getName());
        matchGagnerPrefs.putInt(TOTAL_MATCH_GAGNER_KEY1, 0);
        matchGagnerPrefs.putInt(TOTAL_MATCH_GAGNER_KEY2, 0);
        matchGagnerPrefs.putInt(TOTAL_MATCH_GAGNER_KEY3, 0);
        matchGagnerPrefs.putInt(TOTAL_MATCH_GAGNER_KEY4, 0);

        Preferences ImagePref = Preferences.userRoot().node(AvatarController.class.getName());
        ImagePref.remove(IMAGE_PATH_KEY1);
        ImagePref.remove(IMAGE_PATH_KEY2);
        ImagePref.remove(IMAGE_PATH_KEY3);
        ImagePref.remove(IMAGE_PATH_KEY4);
    }

}
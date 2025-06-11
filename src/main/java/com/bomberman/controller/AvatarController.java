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

            // Charger le FXML du menu principal
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
    private void selectImageForPlayer(ImageView imageView, String preferenceKey) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image d'avatar");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Images (*.jpg, *.png, *.jpeg)", "*.jpg", "*.png", "*.jpeg");
        fileChooser.getExtensionFilters().add(imageFilter);

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);

            Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
            prefs.put(preferenceKey, selectedFile.toURI().toString());
        }
    }

    /**
     * Initialise le contrôleur, charge les pseudos et images enregistrés,
     * initialise les compteurs de matchs et configure la ComboBox des packs de textures.
     *
     * @param location  L'emplacement utilisé pour résoudre les chemins relatifs, ou null si inconnu.
     * @param resources Les ressources utilisées pour localiser les chaînes de caractères, ou null si indisponible.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());

        // Charger les pseudos sauvegardés
        speudo1.setText(prefs.get(PSEUDO_KEY_1, ""));
        speudo2.setText(prefs.get(PSEUDO_KEY_2, ""));
        speudo3.setText(prefs.get(PSEUDO_KEY_3, ""));
        speudo4.setText(prefs.get(PSEUDO_KEY_4, ""));

        // Initialiser la liste des textures disponibles
        textures = new ArrayList<>();
        textures.add(new Texture("Classic", "/image/textures1/"));
        textures.add(new Texture("Retro", "/image/textures2/"));
        textures.add(new Texture("Dark", "/image/textures3/"));
        textures.add(new Texture("Space", "/image/textures4/"));
        textures.add(new Texture("Mario", "/image/textures5/"));

        // Remplir la ComboBox avec la liste des textures
        texturePackComboBox.setItems(FXCollections.observableArrayList(textures));

        // Récupérer la texture sélectionnée précédemment, ou la texture par défaut
        String savedTexturePackName = prefs.get(TEXTURE_PACK_KEY, "Classic");
        Texture savedTexture = textures.stream()
                .filter(t -> t.getNom().equals(savedTexturePackName))
                .findFirst()
                .orElse(textures.get(0));

        texturePackComboBox.setValue(savedTexture);

        // Charger les images des avatars depuis les préférences ou images par défaut
        loadAvatarImage(avatarImageView1, prefs, IMAGE_PATH_KEY1, DEFAULT_IMAGE_1);
        loadAvatarImage(avatarImageView2, prefs, IMAGE_PATH_KEY2, DEFAULT_IMAGE_2);
        loadAvatarImage(avatarImageView3, prefs, IMAGE_PATH_KEY3, DEFAULT_IMAGE_3);
        loadAvatarImage(avatarImageView4, prefs, IMAGE_PATH_KEY4, DEFAULT_IMAGE_4);

        // Initialiser les compteurs de matchs joués et gagnés
        updateMatchCounters(prefs);

        // Bouton reset : remet les pseudos, images et compteurs par défaut
        reset.setOnAction(e -> {
            resetDefaults(prefs);
        });

        // Bouton sauvegarder pack texture
        sauvegarderTexture.setOnAction(e -> sauvegarderTexturePack());
    }

    /**
     * Charge une image d'avatar depuis les préférences ou charge une image par défaut.
     *
     * @param imageView L'ImageView à mettre à jour.
     * @param prefs Les préférences utilisateur.
     * @param preferenceKey La clé pour récupérer le chemin de l'image sauvegardée.
     * @param defaultImagePath Le chemin de l'image par défaut à utiliser si aucune sauvegarde.
     */
    private void loadAvatarImage(ImageView imageView, Preferences prefs, String preferenceKey, String defaultImagePath) {
        String imagePath = prefs.get(preferenceKey, null);
        Image image;

        if (imagePath != null) {
            image = new Image(imagePath);
        } else {
            image = new Image(getClass().getResourceAsStream(defaultImagePath));
        }
        imageView.setImage(image);
    }

    /**
     * Met à jour les labels des compteurs de matchs joués et gagnés à partir des préférences.
     *
     * @param prefs Les préférences utilisateur.
     */
    private void updateMatchCounters(Preferences prefs) {
        nbMatch1.setText(String.valueOf(prefs.getInt(TOTAL_MATCH_KEY1, 0)));
        nbMatch2.setText(String.valueOf(prefs.getInt(TOTAL_MATCH_KEY2, 0)));
        nbMatch3.setText(String.valueOf(prefs.getInt(TOTAL_MATCH_KEY3, 0)));
        nbMatch4.setText(String.valueOf(prefs.getInt(TOTAL_MATCH_KEY4, 0)));

        nbMatchGagner1.setText(String.valueOf(prefs.getInt(TOTAL_MATCH_GAGNER_KEY1, 0)));
        nbMatchGagner2.setText(String.valueOf(prefs.getInt(TOTAL_MATCH_GAGNER_KEY2, 0)));
        nbMatchGagner3.setText(String.valueOf(prefs.getInt(TOTAL_MATCH_GAGNER_KEY3, 0)));
        nbMatchGagner4.setText(String.valueOf(prefs.getInt(TOTAL_MATCH_GAGNER_KEY4, 0)));
    }

    /**
     * Réinitialise les pseudos, images, compteurs et pack de textures aux valeurs par défaut.
     *
     * @param prefs Les préférences utilisateur.
     */
    private void resetDefaults(Preferences prefs) {
        speudo1.setText("");
        speudo2.setText("");
        speudo3.setText("");
        speudo4.setText("");

        nbMatch1.setText("0");
        nbMatch2.setText("0");
        nbMatch3.setText("0");
        nbMatch4.setText("0");

        nbMatchGagner1.setText("0");
        nbMatchGagner2.setText("0");
        nbMatchGagner3.setText("0");
        nbMatchGagner4.setText("0");

        prefs.remove(PSEUDO_KEY_1);
        prefs.remove(PSEUDO_KEY_2);
        prefs.remove(PSEUDO_KEY_3);
        prefs.remove(PSEUDO_KEY_4);

        prefs.remove(TOTAL_MATCH_KEY1);
        prefs.remove(TOTAL_MATCH_KEY2);
        prefs.remove(TOTAL_MATCH_KEY3);
        prefs.remove(TOTAL_MATCH_KEY4);

        prefs.remove(TOTAL_MATCH_GAGNER_KEY1);
        prefs.remove(TOTAL_MATCH_GAGNER_KEY2);
        prefs.remove(TOTAL_MATCH_GAGNER_KEY3);
        prefs.remove(TOTAL_MATCH_GAGNER_KEY4);

        prefs.remove(IMAGE_PATH_KEY1);
        prefs.remove(IMAGE_PATH_KEY2);
        prefs.remove(IMAGE_PATH_KEY3);
        prefs.remove(IMAGE_PATH_KEY4);

        prefs.put(TEXTURE_PACK_KEY, "Classic");
        texturePackComboBox.setValue(textures.get(0));

        loadAvatarImage(avatarImageView1, prefs, IMAGE_PATH_KEY1, DEFAULT_IMAGE_1);
        loadAvatarImage(avatarImageView2, prefs, IMAGE_PATH_KEY2, DEFAULT_IMAGE_2);
        loadAvatarImage(avatarImageView3, prefs, IMAGE_PATH_KEY3, DEFAULT_IMAGE_3);
        loadAvatarImage(avatarImageView4, prefs, IMAGE_PATH_KEY4, DEFAULT_IMAGE_4);
    }

    /**
     * Sauvegarde le pack de textures sélectionné dans les préférences utilisateur.
     */
    private void sauvegarderTexturePack() {
        Texture selectedTexture = texturePackComboBox.getValue();
        if (selectedTexture != null) {
            Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
            prefs.put(TEXTURE_PACK_KEY, selectedTexture.getNom());
        }
    }
}

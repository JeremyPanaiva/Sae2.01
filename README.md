# Bomberman JavaFX

Un jeu Bomberman multijoueur local développé en JavaFX avec une architecture MVC claire.

## Javadocs

https://jeremypanaiva.github.io/Sae2.01/bomberman/module-summary.html

## Fonctionnalités

- Multijoueur local (jusqu'à 4 joueurs)
- Bombes avec timer et explosions
- Murs destructibles avec power-ups
- Détection de collision
- Système de victoire
- Redémarrage de partie
- Interface graphique avec animations
- Séparation claire MVC
- IA bot jouable intégrée pour un ou plusieurs joueurs
- Changement Thème Mario
- Menu principal avec sélection du nombre de joueurs
- Sauvegarde des scores


## 🎮 Contrôles

### Joueur 1 (Rouge)
- **Mouvement** : Z (Haut), S (Bas), Q (Gauche), D (Droite)
- **Bombe** : A

### Joueur 2 (Bleu)
- **Mouvement** : Flèches directionnelles
- **Bombe** : Entrée

### Joueur 3 (Vert)
- **Mouvement** : I (Haut), K (Bas), J (Gauche), L (Droite)
- **Bombe** : U

### Joueur 4 (Jaune)
- **Mouvement** : Pavé numérique (8, 5, 4, 6)
- **Bombe** : 0 (pavé numérique)

### Contrôles généraux
- **R** : Redémarrer la partie

## Objectif du jeu

Éliminez tous les autres joueurs en utilisant vos bombes stratégiquement. Le dernier joueur survivant remporte la partie !

## Éléments du jeu

- **Murs gris** : Indestructibles
- **Murs marron** : Destructibles (peuvent cacher des power-ups)
- **Bombes noires** : Explosent après 3 secondes
- **Explosions jaunes/orange** : Détruisent les murs et éliminent les joueurs

## Power-ups

- **Double Bombes** : Bombe supplémentaire
- **Bombe Nucelaire** : Portée d'explosion augmentée


## 🏗Architecture du projet

### Modèle (Model)
- `Game.java` : Logique principale du jeu
- `GameBoard.java` : Plateau de jeu et gestion des éléments
- `Player.java` : Représentation des joueurs
- `Bomb.java` : Logique des bombes
- `Explosion.java` : Gestion des explosions
- `Wall.java` : Murs du jeu
- `PowerUp.java` : Bonus collectables
- `Bot.java`: IA contrôlant un joueur automatique

### Vue (View)
- `GameView.java` : Rendu graphique du jeu
- `game.fxml` : Interface utilisateur
- `game.css` : Styles visuels

### Contrôleur (Controller)
- `GameController.java` : Gestion des entrées et coordination

### Utilitaires
- `GameConstants.java` : Constantes du jeu
- `Direction.java` : Énumération des directions
- `Position.java` : Gestion des positions

## Compilation et exécution

### Prérequis
- Java 11 ou supérieur
- JavaFX SDK
- IntelliJ IDEA 2024.3

### Configuration IntelliJ IDEA

1. **Créer un nouveau projet JavaFX**
2. **Ajouter JavaFX au classpath** :
   - File → Project Structure → Libraries
   - Ajouter JavaFX SDK

3. **Arborescence** :
```bash
.idea
├───.mvn
│   └───wrapper
├───docs
│   ├───bomberman
│   │   └───com
│   │       └───bomberman
│   │           ├───controller
│   │           ├───model
│   │           ├───util
│   │           └───view
│   ├───legal
│   ├───resource-files
│   └───script-files
└───src
    ├───main
    │   ├───java
    │   │   └───com
    │   │       └───bomberman
    │   │           ├───controller
    │   │           ├───model
    │   │           ├───util
    │   │           └───view
    │   └───resources
    │       ├───css
    │       ├───fxml
    │       └───image
    │           ├───defaut
    │           └───mario
    └───test
        └───java
            └───com
                └───bomberman
                    └───model

 ```

4. **Structure des dossiers** :
   - Créer les packages selon l'arborescence fournie
   - Placer les fichiers FXML dans `src/main/resources/fxml/`
   - Placer les CSS dans `src/main/resources/css/`

### Exécution
Lancer la classe `BombermanApplication.main()`

## Améliorations possibles

- Ajout d'effets sonores
- Niveaux avec différents layouts
- Effets visuels avancés

  #### Commande pour generé la javadoc

```bash
javadoc -d docs \
-sourcepath src/main/java \
--module-path "$HOME/Bureau/javafx-sdk-24.0.1/lib" \
--add-modules javafx.controls,javafx.fxml \
-subpackages com.bomberman
```

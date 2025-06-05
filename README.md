# Bomberman JavaFX

Un jeu Bomberman multijoueur local développé en JavaFX avec une architecture MVC claire.

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
- **Mouvement** : Pavé numérique (8, 2, 4, 6)
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

- **B (Orange)** : Bombe supplémentaire
- **E (Rouge)** : Portée d'explosion augmentée
- **S (Cyan)** : Vitesse augmentée

## 🏗Architecture du projet

### Modèle (Model)
- `Game.java` : Logique principale du jeu
- `GameBoard.java` : Plateau de jeu et gestion des éléments
- `Player.java` : Représentation des joueurs
- `Bomb.java` : Logique des bombes
- `Explosion.java` : Gestion des explosions
- `Wall.java` : Murs du jeu
- `PowerUp.java` : Bonus collectables

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

3. **Configuration VM Options** :
```bash
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── bomberman/
│   │           ├── BombermanApplication.java
│   │           ├── controller/
│   │           │   └── GameController.java
│   │           ├── model/
│   │           │   ├── Game.java
│   │           │   ├── Player.java
│   │           │   ├── Bomb.java
│   │           │   ├── Explosion.java
│   │           │   ├── Wall.java
│   │           │   ├── PowerUp.java
│   │           │   └── GameBoard.java
│   │           ├── view/
│   │           │   └── GameView.java
│   │           └── util/
│   │               ├── Direction.java
│   │               ├── GameConstants.java
│   │               └── Position.java
│   └── resources/
│       ├── fxml/
│       │   └── game.fxml
│       ├── css/
│       │   └── game.css
│       └── images/
│           ├── player1.png
│           ├── player2.png
│           ├── player3.png
│           ├── player4.png
│           ├── bomb.png
│           ├── explosion.png
│           ├── wall.png
│           ├── destructible_wall.png
│           └── powerup.png

 ```

4. **Structure des dossiers** :
   - Créer les packages selon l'arborescence fournie
   - Placer les fichiers FXML dans `src/main/resources/fxml/`
   - Placer les CSS dans `src/main/resources/css/`

### Exécution
Lancer la classe `BombermanApplication.main()`

## Fonctionnalités

- Multijoueur local (jusqu'à 4 joueurs)
- Bombes avec timer et explosions
- Murs destructibles avec power-ups
- Détection de collision
- Système de victoire
- Redémarrage de partie
- Interface graphique avec animations
- Séparation claire MVC

## Améliorations possibles

- Ajout d'effets sonores
- Menu principal avec sélection du nombre de joueurs
- Niveaux avec différents layouts
- IA pour joueurs ordinateur
- Sauvegarde des scores
- Effets visuels avancés

## Arborecence 


src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── bomberman/
│   │           ├── BombermanApplication.java
│   │           ├── controller/
│   │           │   └── GameController.java
│   │           ├── model/
│   │           │   ├── Game.java
│   │           │   ├── Player.java
│   │           │   ├── Bomb.java
│   │           │   ├── Explosion.java
│   │           │   ├── Wall.java
│   │           │   ├── PowerUp.java
│   │           │   └── GameBoard.java
│   │           ├── view/
│   │           │   └── GameView.java
│   │           └── util/
│   │               ├── Direction.java
│   │               ├── GameConstants.java
│   │               └── Position.java
│   └── resources/
│       ├── fxml/
│       │   └── game.fxml
│       ├── css/
│       │   └── game.css
│       └── images/
│           ├── player1.png
│           ├── player2.png
│           ├── player3.png
│           ├── player4.png
│           ├── bomb.png
│           ├── explosion.png
│           ├── wall.png
│           ├── destructible_wall.png
│           └── powerup.png

package com.bomberman.model;

import com.bomberman.util.*;
import java.util.*;

public class Game {
    private GameBoard board;
    private List<Player> players;
    private boolean gameRunning;
    private Player winner;

    public Game(int numPlayers) {
        board = new GameBoard();
        players = new ArrayList<>();
        gameRunning = true;
        winner = null;

        initializePlayers(numPlayers);
    }

    private void initializePlayers(int numPlayers) {
        Position[] startPositions = {
                new Position(1, 1),
                new Position(GameConstants.BOARD_WIDTH - 2, 1),
                new Position(1, GameConstants.BOARD_HEIGHT - 2),
                new Position(GameConstants.BOARD_WIDTH - 2, GameConstants.BOARD_HEIGHT - 2)
        };

        for (int i = 0; i < Math.min(numPlayers, GameConstants.MAX_PLAYERS); i++) {
            players.add(new Player(i, startPositions[i], GameConstants.PLAYER_COLORS[i]));
        }
    }

    public void movePlayer(int playerId, Direction direction) {
        if (!gameRunning) return;

        Player player = players.get(playerId);
        if (player != null && player.isAlive()) {
            player.move(direction, board);
            checkPowerUpCollection(player);
        }
    }

    public void placeBomb(int playerId) {
        if (!gameRunning) return;

        Player player = players.get(playerId);
        if (player != null && player.isAlive() && player.canPlaceBomb()) {
            Bomb bomb = new Bomb(player.getPosition(), player);
            board.placeBomb(bomb);
            player.placeBomb();
        }
    }

    public void update() {
        if (!gameRunning) return;

        // Vérifier les bombes qui doivent exploser
        List<Bomb> bombsToExplode = new ArrayList<>();
        for (Bomb bomb : board.getBombs()) {
            if (bomb.shouldExplode()) {
                bombsToExplode.add(bomb);
            }
        }

        // Faire exploser les bombes
        for (Bomb bomb : bombsToExplode) {
            explodeBomb(bomb);
        }

        // Nettoyer les explosions finies
        board.removeFinishedExplosions();

        // Vérifier si des joueurs sont morts dans les explosions
        checkPlayerDeaths();

        // Vérifier les conditions de victoire
        checkWinCondition();
    }

    private void explodeBomb(Bomb bomb) {
        board.removeBomb(bomb.getPosition());
        bomb.getOwner().bombExploded();

        List<Position> explosionPositions = calculateExplosionPositions(bomb);
        board.addExplosion(new Explosion(explosionPositions));

        // Détruire les murs destructibles
        for (Position pos : explosionPositions) {
            if (board.hasDestructibleWall(pos)) {
                board.destroyWall(pos);
            }
        }
    }

    private List<Position> calculateExplosionPositions(Bomb bomb) {
        List<Position> positions = new ArrayList<>();
        Position center = bomb.getPosition();
        positions.add(center);

        // Explosion dans les 4 directions
        for (Direction dir : Direction.values()) {
            for (int i = 1; i <= bomb.getExplosionRange(); i++) {
                Position pos = new Position(center.getX() + dir.getDeltaX() * i,
                        center.getY() + dir.getDeltaY() * i);

                if (!board.isValidPosition(pos) || board.hasWall(pos)) {
                    if (board.hasDestructibleWall(pos)) {
                        positions.add(pos);
                    }
                    break;
                }

                positions.add(pos);
            }
        }

        return positions;
    }

    private void checkPowerUpCollection(Player player) {
        PowerUp powerUp = board.getPowerUp(player.getPosition());
        if (powerUp != null) {
            powerUp.applyTo(player);
        }
    }

    private void checkPlayerDeaths() {
        for (Player player : players) {
            if (player.isAlive()) {
                for (Explosion explosion : board.getExplosions()) {
                    if (explosion.contains(player.getPosition())) {
                        player.kill();
                        break;
                    }
                }
            }
        }
    }

    private void checkWinCondition() {
        List<Player> alivePlayers = players.stream()
                .filter(Player::isAlive)
                .toList();

        if (alivePlayers.size() <= 1) {
            gameRunning = false;
            if (alivePlayers.size() == 1) {
                winner = alivePlayers.get(0);
            }
        }
    }

    public void resetGame() {
        board = new GameBoard();
        gameRunning = true;
        winner = null;

        // Réinitialiser les joueurs
        Position[] startPositions = {
                new Position(1, 1),
                new Position(GameConstants.BOARD_WIDTH - 2, 1),
                new Position(1, GameConstants.BOARD_HEIGHT - 2),
                new Position(GameConstants.BOARD_WIDTH - 2, GameConstants.BOARD_HEIGHT - 2)
        };

        for (int i = 0; i < players.size(); i++) {
            players.set(i, new Player(i, startPositions[i], GameConstants.PLAYER_COLORS[i]));
        }
    }

    // Getters
    public GameBoard getBoard() { return board; }
    public List<Player> getPlayers() { return new ArrayList<>(players); }
    public boolean isGameRunning() { return gameRunning; }
    public Player getWinner() { return winner; }
}
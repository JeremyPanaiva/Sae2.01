package com.bomberman.model;

import com.bomberman.model.Game;
import com.bomberman.model.Player;
import com.bomberman.util.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        // Par exemple, 4 joueurs dont 2 humains
        game = new Game(4, 2);
    }

    @Test
    void movePlayer() {
        // Exemple test movePlayer
        Player playerBefore = game.getPlayer(0);
        int xBefore = playerBefore.getPosition().getX();
        int yBefore = playerBefore.getPosition().getY();

        game.movePlayer(0, Direction.RIGHT);

        Player playerAfter = game.getPlayer(0);
        assertEquals(xBefore + 1, playerAfter.getPosition().getX());
        assertEquals(yBefore, playerAfter.getPosition().getY());
    }

}

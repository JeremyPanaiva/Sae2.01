package com.bomberman.model;

import com.bomberman.util.Direction;
import com.bomberman.util.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void canPlaceBomb() {
        Player player = new Player(1, new Position(0, 0), "red");
        assertTrue(player.canPlaceBomb());
        player.placeBomb();
        assertFalse(player.canPlaceBomb()); // maxBombs par défaut = 1
    }

    @Test
    void placeBomb() {
        Player player = new Player(1, new Position(0, 0), "blue");
        assertEquals(0, player.getBombCount());
        player.placeBomb();
        assertEquals(1, player.getBombCount());
        // Ne doit pas dépasser maxBombs
        player.placeBomb();
        assertEquals(1, player.getBombCount());
    }

    @Test
    void bombExploded() {
        Player player = new Player(1, new Position(0, 0), "green");
        player.placeBomb();
        assertEquals(1, player.getBombCount());
        player.bombExploded();
        assertEquals(0, player.getBombCount());
        // Pas de bombCount négatif
        player.bombExploded();
        assertEquals(0, player.getBombCount());
    }

    @Test
    void move() {
        GameBoard board = new GameBoard();
        Player player = new Player(1, new Position(1, 1), "yellow");
        Position initialPos = player.getPosition();

        // On déplace vers la droite (si possible)
        player.move(Direction.RIGHT, board);
        Position newPos = player.getPosition();
        assertNotEquals(initialPos, newPos);

        // On tue le joueur, il ne doit plus bouger
        player.kill();
        Position posBeforeMove = player.getPosition();
        player.move(Direction.LEFT, board);
        assertEquals(posBeforeMove, player.getPosition());
    }

    @Test
    void kill() {
        Player player = new Player(1, new Position(0, 0), "pink");
        assertTrue(player.isAlive());
        player.kill();
        assertFalse(player.isAlive());
    }

    @Test
    void getId() {
        Player player = new Player(42, new Position(0, 0), "black");
        assertEquals(42, player.getId());
    }

    @Test
    void getPosition() {
        Position pos = new Position(3, 4);
        Player player = new Player(1, pos, "white");
        Position returnedPos = player.getPosition();
        assertEquals(pos.getX(), returnedPos.getX());
        assertEquals(pos.getY(), returnedPos.getY());
        assertNotSame(pos, returnedPos);
    }

    @Test
    void isAlive() {
        Player player = new Player(1, new Position(0, 0), "orange");
        assertTrue(player.isAlive());
        player.kill();
        assertFalse(player.isAlive());
    }

    @Test
    void getBombCount() {
        Player player = new Player(1, new Position(0, 0), "purple");
        assertEquals(0, player.getBombCount());
        player.placeBomb();
        assertEquals(1, player.getBombCount());
    }

    @Test
    void getMaxBombs() {
        Player player = new Player(1, new Position(0, 0), "cyan");
        assertEquals(1, player.getMaxBombs());
        player.setMaxBombs(3);
        assertEquals(3, player.getMaxBombs());
    }

    @Test
    void getExplosionRange() {
        Player player = new Player(1, new Position(0, 0), "gray");
        assertEquals(2, player.getExplosionRange());
        player.setExplosionRange(5);
        assertEquals(5, player.getExplosionRange());
    }

    @Test
    void getColor() {
        Player player = new Player(1, new Position(0, 0), "violet");
        assertEquals("violet", player.getColor());
    }

    @Test
    void setMaxBombs() {
        Player player = new Player(1, new Position(0, 0), "silver");
        player.setMaxBombs(4);
        assertEquals(4, player.getMaxBombs());
    }

    @Test
    void setExplosionRange() {
        Player player = new Player(1, new Position(0, 0), "gold");
        player.setExplosionRange(6);
        assertEquals(6, player.getExplosionRange());
    }
}

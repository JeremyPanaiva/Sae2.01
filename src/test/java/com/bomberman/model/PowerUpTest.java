package com.bomberman.model;

import com.bomberman.util.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PowerUpTest {

    @Test
    void applyTo() {
        Player player = new Player(1, new Position(0, 0), "red");
        int initialMaxBombs = player.getMaxBombs();
        int initialExplosionRange = player.getExplosionRange();

        PowerUp extraBomb = new PowerUp(new Position(1, 1), PowerUp.Type.EXTRA_BOMB);
        extraBomb.applyTo(player);
        assertEquals(initialMaxBombs + 1, player.getMaxBombs(), "Le maxBombs doit augmenter de 1");

        PowerUp biggerExplosion = new PowerUp(new Position(2, 2), PowerUp.Type.BIGGER_EXPLOSION);
        biggerExplosion.applyTo(player);
        assertEquals(initialExplosionRange + 1, player.getExplosionRange(), "L'explosionRange doit augmenter de 1");
    }

    @Test
    void getPosition() {
        Position pos = new Position(5, 6);
        PowerUp powerUp = new PowerUp(pos, PowerUp.Type.EXTRA_BOMB);
        Position returnedPos = powerUp.getPosition();

        assertEquals(pos.getX(), returnedPos.getX());
        assertEquals(pos.getY(), returnedPos.getY());

        // Vérifie que la position retournée est une copie (pas la même référence)
        assertNotSame(pos, returnedPos);
    }

    @Test
    void getType() {
        PowerUp powerUp = new PowerUp(new Position(0, 0), PowerUp.Type.BIGGER_EXPLOSION);
        assertEquals(PowerUp.Type.BIGGER_EXPLOSION, powerUp.getType());
    }
}

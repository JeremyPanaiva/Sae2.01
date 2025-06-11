package com.bomberman.model;

import com.bomberman.util.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WallTest {

    @Test
    void getPosition() {
        Position pos = new Position(3, 5);
        Wall wall = new Wall(pos, true);

        Position returnedPos = wall.getPosition();

        // Vérifier que la position retournée a les mêmes coordonnées
        assertEquals(pos.getX(), returnedPos.getX());
        assertEquals(pos.getY(), returnedPos.getY());

        // Vérifier que ce n'est pas la même instance (copie)
        assertNotSame(pos, returnedPos);
    }

    @Test
    void isDestructible() {
        Wall destructibleWall = new Wall(new Position(3, 5), false);
        Wall indestructibleWall = new Wall(new Position(3, 5), true);

        assertFalse(destructibleWall.isDestructible());
        assertTrue(indestructibleWall.isDestructible());
    }
}
package ru.elynx.battlesnake.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Internals")
class CoordinatesTest {
    private static final Coordinates p1 = new Coordinates(1, 1);
    private static final Coordinates p2 = new Coordinates(1, 2);
    private static final Coordinates p3 = new Coordinates(100, 100);

    @Test
    void test_manhattan_of_coordinate() {
        assertEquals(0, p1.manhattanDistance(p1));
        assertEquals(0, p2.manhattanDistance(p2));
        assertEquals(0, p3.manhattanDistance(p3));

        assertEquals(p1.manhattanDistance(p2), p2.manhattanDistance(p1));
        assertEquals(p1.manhattanDistance(p3), p3.manhattanDistance(p1));
        assertEquals(p2.manhattanDistance(p3), p3.manhattanDistance(p2));

        assertEquals(1, p1.manhattanDistance(p2));
        assertEquals(99 + 99, p1.manhattanDistance(p3));
        assertEquals(99 + 98, p2.manhattanDistance(p3));
    }
}

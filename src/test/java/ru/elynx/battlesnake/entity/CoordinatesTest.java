package ru.elynx.battlesnake.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.elynx.battlesnake.entity.MoveCommand.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Internals")
class CoordinatesTest {
    private static final int x0 = -1;
    private static final int x1 = 11;
    private static final int y0 = -1;
    private static final int y1 = 11;

    private static final Coordinates p1 = new Coordinates(1, 1);
    private static final Coordinates p2 = new Coordinates(1, 2);
    private static final Coordinates p3 = new Coordinates(100, 100);

    @Test
    void test_zero_constant() {
        assertEquals(0, Coordinates.ZERO.getX());
        assertEquals(0, Coordinates.ZERO.getY());

        // test that constant is equals to created
        assertEquals(Coordinates.ZERO, new Coordinates(0, 0));
    }

    @Test
    void test_move() {
        for (int x = x0; x <= x1; ++x) {
            for (int y = y0; y <= y1; ++y) {
                Coordinates tested = new Coordinates(x, y);

                Coordinates down = tested.move(DOWN);
                Coordinates left = tested.move(LEFT);
                Coordinates right = tested.move(RIGHT);
                Coordinates up = tested.move(UP);

                assertEquals(x, down.getX());
                assertEquals(x, up.getX());

                assertEquals(y, left.getY());
                assertEquals(y, right.getY());

                assertEquals(y - 1, down.getY());
                assertEquals(x - 1, left.getX());
                assertEquals(x + 1, right.getX());
                assertEquals(y + 1, up.getY());
            }
        }
    }

    @Test
    void test_side_neighbours() {
        for (int x = x0; x <= x1; ++x) {
            for (int y = y0; y <= y1; ++y) {
                Coordinates tested = new Coordinates(x, y);

                // tested above
                Coordinates down = tested.move(DOWN);
                Coordinates left = tested.move(LEFT);
                Coordinates right = tested.move(RIGHT);
                Coordinates up = tested.move(UP);

                assertThat(tested.sideNeighbours(), containsInAnyOrder(down, left, right, up));
            }
        }
    }

    @Test
    void test_angle_neighbours() {
        for (int x = x0; x <= x1; ++x) {
            for (int y = y0; y <= y1; ++y) {
                Coordinates tested = new Coordinates(x, y);

                // tested above
                Coordinates one = tested.move(DOWN).move(LEFT);
                Coordinates two = tested.move(DOWN).move(RIGHT);
                Coordinates three = tested.move(UP).move(LEFT);
                Coordinates four = tested.move(UP).move(RIGHT);

                assertThat(tested.cornerNeighbours(), containsInAnyOrder(one, two, three, four));
            }
        }
    }

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

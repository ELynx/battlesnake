package ru.elynx.battlesnake.entity;

import static org.junit.jupiter.api.Assertions.*;
import static ru.elynx.battlesnake.entity.MoveCommand.*;
import static ru.elynx.battlesnake.entity.MoveCommand.UP;

import org.junit.jupiter.api.Test;

class CoordinatesWithDirectionTest {
    private static final int x0 = -1;
    private static final int x1 = 11;
    private static final int y0 = -1;
    private static final int y1 = 11;

    @Test
    void test_from_coordinates() {
        for (int x = x0; x <= x1; ++x) {
            for (int y = y0; y <= y1; ++y) {
                Coordinates from = new Coordinates(x, y);

                CoordinatesWithDirection down = CoordinatesWithDirection.fromCoordinates(from, DOWN);
                CoordinatesWithDirection left = CoordinatesWithDirection.fromCoordinates(from, LEFT);
                CoordinatesWithDirection right = CoordinatesWithDirection.fromCoordinates(from, RIGHT);
                CoordinatesWithDirection up = CoordinatesWithDirection.fromCoordinates(from, UP);

                assertEquals(DOWN, down.getDirection());
                assertEquals(LEFT, left.getDirection());
                assertEquals(RIGHT, right.getDirection());
                assertEquals(UP, up.getDirection());

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
    void test_equals() {
        for (int x = x0; x <= x1; ++x) {
            for (int y = y0; y <= y1; ++y) {
                Coordinates from = new Coordinates(x, y);

                CoordinatesWithDirection down = CoordinatesWithDirection.fromCoordinates(from, DOWN);
                CoordinatesWithDirection left = CoordinatesWithDirection.fromCoordinates(from, LEFT);
                CoordinatesWithDirection right = CoordinatesWithDirection.fromCoordinates(from, RIGHT);
                CoordinatesWithDirection up = CoordinatesWithDirection.fromCoordinates(from, UP);

                Coordinates manualDown = new Coordinates(down.getX(), down.getY());
                Coordinates manualLeft = new Coordinates(left.getX(), left.getY());
                Coordinates manualRight = new Coordinates(right.getX(), right.getY());
                Coordinates manualUp = new Coordinates(up.getX(), up.getY());

                assertEquals(down, manualDown);
                assertEquals(left, manualLeft);
                assertEquals(right, manualRight);
                assertEquals(up, manualUp);

                assertEquals(manualDown, down);
                assertEquals(manualLeft, left);
                assertEquals(manualRight, right);
                assertEquals(manualUp, up);
            }
        }
    }

    @Test
    void test_hash_code() {
        for (int x = x0; x <= x1; ++x) {
            for (int y = y0; y <= y1; ++y) {
                Coordinates from = new Coordinates(x, y);

                CoordinatesWithDirection down = CoordinatesWithDirection.fromCoordinates(from, DOWN);
                CoordinatesWithDirection left = CoordinatesWithDirection.fromCoordinates(from, LEFT);
                CoordinatesWithDirection right = CoordinatesWithDirection.fromCoordinates(from, RIGHT);
                CoordinatesWithDirection up = CoordinatesWithDirection.fromCoordinates(from, UP);

                Coordinates manualDown = new Coordinates(down.getX(), down.getY());
                Coordinates manualLeft = new Coordinates(left.getX(), left.getY());
                Coordinates manualRight = new Coordinates(right.getX(), right.getY());
                Coordinates manualUp = new Coordinates(up.getX(), up.getY());

                assertEquals(down.hashCode(), manualDown.hashCode());
                assertEquals(left.hashCode(), manualLeft.hashCode());
                assertEquals(right.hashCode(), manualRight.hashCode());
                assertEquals(up.hashCode(), manualUp.hashCode());
            }
        }
    }

    @Test
    void test_move_command_integration() {
        for (MoveCommand moveCommand : MoveCommand.values()) {
            Coordinates coordinates = new Coordinates(x0, y0);

            switch (moveCommand) {
                case DOWN :
                case LEFT :
                case RIGHT :
                case UP :
                    assertDoesNotThrow(() -> CoordinatesWithDirection.fromCoordinates(coordinates, moveCommand));
                    break;
                default :
                    assertThrows(IllegalArgumentException.class,
                            () -> CoordinatesWithDirection.fromCoordinates(coordinates, moveCommand));
                    break;
            }
        }
    }
}

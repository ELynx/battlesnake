package ru.elynx.battlesnake.engine.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Dimensions;

class FlagMatrixTest {
    @Test
    void test_uninitialized_matrix() {
        int width = 11;
        int height = 15;
        Dimensions dimensions = new Dimensions(width, height);

        FlagMatrix tested = FlagMatrix.uninitializedMatrix(dimensions);

        for (int x = -1; x <= width; ++x) {
            for (int y = -1; y <= height; ++y) {
                assertFalse(tested.isSet(x, y));
            }
        }
    }

    @Test
    void test_unset_matrix() {
        int width = 11;
        int height = 15;
        Dimensions dimensions = new Dimensions(width, height);

        FlagMatrix tested = FlagMatrix.unsetMatrix(dimensions);

        for (int x = -1; x <= width; ++x) {
            for (int y = -1; y <= height; ++y) {
                assertFalse(tested.isSet(x, y));
            }
        }
    }

    @Test
    void test_unset_all() {
        int width = 11;
        int height = 15;
        Dimensions dimensions = new Dimensions(width, height);

        FlagMatrix tested = FlagMatrix.uninitializedMatrix(dimensions);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertTrue(tested.set(x, y));
                assertTrue(tested.set(new Coordinates(x, y)));
            }
        }

        tested.unsetAll();

        for (int x = -1; x <= width; ++x) {
            for (int y = -1; y <= height; ++y) {
                assertFalse(tested.isSet(x, y));
            }
        }
    }

    @Test
    void test_set_outside() {
        int width = 11;
        int height = 15;
        Dimensions dimensions = new Dimensions(width, height);

        FlagMatrix tested = FlagMatrix.unsetMatrix(dimensions);

        for (int x = 0; x < width; ++x) {
            assertFalse(tested.set(x, -1));
            assertFalse(tested.set(x, height));

            assertFalse(tested.set(new Coordinates(x, -1)));
            assertFalse(tested.set(new Coordinates(x, height)));
        }

        for (int y = 0; y < height; ++y) {
            assertFalse(tested.set(-1, y));
            assertFalse(tested.set(width, y));

            assertFalse(tested.set(new Coordinates(-1, y)));
            assertFalse(tested.set(new Coordinates(width, y)));
        }

        for (int x = -1; x <= width; ++x) {
            for (int y = -1; y <= height; ++y) {
                assertFalse(tested.isSet(x, y));
            }
        }
    }

    @Test
    void test_set_x_y_and_test() {
        int width = 11;
        int height = 15;
        Dimensions dimensions = new Dimensions(width, height);

        FlagMatrix tested = FlagMatrix.unsetMatrix(dimensions);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertTrue(tested.set(x, y));
            }
        }

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertTrue(tested.isSet(x, y));
            }
        }
    }

    @Test
    void test_set_coordinates_and_test() {
        int width = 11;
        int height = 15;
        Dimensions dimensions = new Dimensions(width, height);

        FlagMatrix tested = FlagMatrix.unsetMatrix(dimensions);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertTrue(tested.set(new Coordinates(x, y)));
            }
        }

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertTrue(tested.isSet(x, y));
            }
        }
    }
}

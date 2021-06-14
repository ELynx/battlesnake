package ru.elynx.battlesnake.engine.math;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Dimensions;

class FlagMatrixTest {
    @Test
    void test_uninitialized_matrix() {
        int width = 11;
        int height = 15;
        Dimensions dimensions = new Dimensions(width, height);

        FlagMatrix tested = FlagMatrix.uninitializedMatrix(dimensions, false);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertFalse(tested.isSet(x, y));
                assertFalse(tested.isSet(new Coordinates(x, y)));
            }
        }

        tested = FlagMatrix.uninitializedMatrix(dimensions, true);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertFalse(tested.isSet(x, y));
                assertFalse(tested.isSet(new Coordinates(x, y)));
            }
        }
    }

    @Test
    void test_unset_matrix() {
        int width = 11;
        int height = 15;
        Dimensions dimensions = new Dimensions(width, height);

        FlagMatrix tested = FlagMatrix.unsetMatrix(dimensions, false);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertFalse(tested.isSet(x, y));
                assertFalse(tested.isSet(new Coordinates(x, y)));
            }
        }

        tested = FlagMatrix.unsetMatrix(dimensions, true);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertFalse(tested.isSet(x, y));
                assertFalse(tested.isSet(new Coordinates(x, y)));
            }
        }
    }

    @Test
    void test_unset_all() {
        int width = 11;
        int height = 15;
        Dimensions dimensions = new Dimensions(width, height);

        FlagMatrix tested = FlagMatrix.uninitializedMatrix(dimensions, false);

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
                assertFalse(tested.isSet(new Coordinates(x, y)));
            }
        }
    }

    @Test
    void test_set_outside() {
        int width = 11;
        int height = 15;
        Dimensions dimensions = new Dimensions(width, height);

        FlagMatrix tested = FlagMatrix.unsetMatrix(dimensions, false);

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
                assertFalse(tested.isSet(new Coordinates(x, y)));
            }
        }
    }

    @Test
    void test_set_x_y_and_test() {
        int width = 11;
        int height = 15;
        Dimensions dimensions = new Dimensions(width, height);

        FlagMatrix tested = FlagMatrix.unsetMatrix(dimensions, false);

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

        FlagMatrix tested = FlagMatrix.unsetMatrix(dimensions, false);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertTrue(tested.set(new Coordinates(x, y)));
            }
        }

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertTrue(tested.isSet(new Coordinates(x, y)));
            }
        }
    }

    @Test
    void test_outside_value() {
        int width = 11;
        int height = 15;
        Dimensions dimensions = new Dimensions(width, height);

        FlagMatrix tested = FlagMatrix.unsetMatrix(dimensions, false);

        for (int x = 0; x < width; ++x) {
            assertFalse(tested.isSet(x, -1));
            assertFalse(tested.isSet(x, height));

            assertFalse(tested.isSet(new Coordinates(x, -1)));
            assertFalse(tested.isSet(new Coordinates(x, height)));
        }

        for (int y = 0; y < height; ++y) {
            assertFalse(tested.isSet(-1, y));
            assertFalse(tested.isSet(width, y));

            assertFalse(tested.isSet(new Coordinates(-1, y)));
            assertFalse(tested.isSet(new Coordinates(width, y)));
        }

        tested = FlagMatrix.unsetMatrix(dimensions, true);

        for (int x = 0; x < width; ++x) {
            assertTrue(tested.isSet(x, -1));
            assertTrue(tested.isSet(x, height));

            assertTrue(tested.isSet(new Coordinates(x, -1)));
            assertTrue(tested.isSet(new Coordinates(x, height)));
        }

        for (int y = 0; y < height; ++y) {
            assertTrue(tested.isSet(-1, y));
            assertTrue(tested.isSet(width, y));

            assertTrue(tested.isSet(new Coordinates(-1, y)));
            assertTrue(tested.isSet(new Coordinates(width, y)));
        }
    }
}

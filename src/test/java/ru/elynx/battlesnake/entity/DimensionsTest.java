package ru.elynx.battlesnake.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Internals")
class DimensionsTest {
    @Test
    void test_ctor() {
        for (int width = 1; width < 100; ++width) {
            for (int height = 1; height < 100; ++height) {
                Dimensions tested = new Dimensions(width, height);

                assertEquals(width, tested.getWidth());
                assertEquals(height, tested.getHeight());
            }
        }
    }

    @Test
    void test_area() {
        for (int width = 1; width < 100; ++width) {
            for (int height = 1; height < 100; ++height) {
                Dimensions tested = new Dimensions(width, height);

                assertEquals(width * height, tested.getArea());
            }
        }
    }

    @Test
    void test_out_of_bounds() {
        int width = 11;
        int height = 11;
        Dimensions tested = new Dimensions(width, height);

        for (int x = -1; x <= width; ++x) {
            for (int y = -1; y <= height; ++y) {
                boolean byCoordinates = tested.isOutOfBounds(new Coordinates(x, y));
                boolean byXY = tested.isOutOfBounds(x, y);

                assertThat(byXY, equalTo(byCoordinates));

                if (x == -1 || x == width || y == -1 || y == height) {
                    assertTrue(byXY);
                    assertTrue(byCoordinates);
                } else {
                    assertFalse(byXY);
                    assertFalse(byCoordinates);
                }
            }
        }
    }

    @Test
    void test_center() {
        for (int width = 1; width < 100; ++width) {
            for (int height = 1; height < 100; ++height) {
                Dimensions tested = new Dimensions(width, height);

                Coordinates center = tested.getCenter();

                assertEquals(width / 2, center.getX());
                assertEquals(height / 2, center.getY());
            }
        }
    }
}

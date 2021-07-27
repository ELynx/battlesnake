package ru.elynx.battlesnake.engine.math;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Dimensions;

@Tag("Internals")
class MatrixTest {
    @Test
    void test_get_dimensions() {
        for (int width = 1; width < 100; ++width) {
            for (int height = 1; height < 100; ++height) {
                Dimensions dimensions = new Dimensions(width, height);

                Matrix tested = new Matrix(dimensions);

                assertEquals(dimensions, tested.getDimensions());
            }
        }
    }

    @Test
    void test_calculate_bound_index() {
        int width = 11;
        int height = 11;
        Dimensions dimensions = new Dimensions(width, height);

        Matrix tested = new Matrix(dimensions);

        for (int x = -1; x <= width; ++x) {
            for (int y = -1; y <= height; ++y) {
                int byXY = tested.calculateBoundIndex(x, y);
                int byCoordinates = tested.calculateBoundIndex(new Coordinates(x, y));

                assertThat(byXY, equalTo(byCoordinates));

                if (x == -1 || x == width || y == -1 || y == height) {
                    assertEquals(-1, byXY);
                    assertEquals(-1, byCoordinates);
                } else {
                    assertThat(byXY, is(both(greaterThanOrEqualTo(0)).and(lessThan(dimensions.getArea()))));
                    assertThat(byCoordinates, is(both(greaterThanOrEqualTo(0)).and(lessThan(dimensions.getArea()))));
                }
            }
        }
    }
}

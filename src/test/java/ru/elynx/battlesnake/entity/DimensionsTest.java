package ru.elynx.battlesnake.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Internals")
class DimensionsTest {
    @Test
    void test_area() {
        for (int width = 1; width < 100; ++width) {
            for (int height = 1; height < 100; ++height) {
                Dimensions tested = new Dimensions(width, height);

                assertEquals(width, tested.getWidth());
                assertEquals(height, tested.getHeight());

                assertEquals(width * height, tested.area());
            }
        }
    }
}

package ru.elynx.battlesnake.engine.math;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Dimensions;

@Tag("Internals")
class DoubleMatrixTest {
    private static final double fuzz = 0.0001d;

    @Test
    void test_uninitialized_matrix() {
        int width = 11;
        int height = 15;
        Dimensions dimensions = new Dimensions(width, height);
        double outsideValue = -2.0d;

        DoubleMatrix matrix = DoubleMatrix.uninitializedMatrix(dimensions, outsideValue);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertThat(matrix.getValue(x, y), is(equalTo(0.0d)));
                assertThat(matrix.getValue(new Coordinates(x, y)), is(equalTo(0.0d)));
            }
        }
    }

    @Test
    void test_zero_matrix() {
        int width = 11, height = 15;
        Dimensions dimensions = new Dimensions(width, height);
        double outsideValue = -2.0d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(dimensions, outsideValue);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertThat(matrix.getValue(x, y), is(equalTo(0.0d)));
                assertThat(matrix.getValue(new Coordinates(x, y)), is(equalTo(0.0d)));
            }
        }
    }

    @Test
    void test_zero() {
        int width = 11, height = 15;
        Dimensions dimensions = new Dimensions(width, height);
        double outsideValue = -2.0d;
        double insideValue = 123.4d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(dimensions, outsideValue);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertTrue(matrix.addValue(new Coordinates(x, y), insideValue));
            }
        }

        matrix.zero();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertThat(matrix.getValue(x, y), is(equalTo(0.0d)));
                assertThat(matrix.getValue(new Coordinates(x, y)), is(equalTo(0.0d)));
            }
        }
    }

    @Test
    void test_add_get() {
        int width = 11, height = 15;
        Dimensions dimensions = new Dimensions(width, height);
        double outsideValue = -2.0d;
        double insideValue = 123.4d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(dimensions, outsideValue);

        for (int x = -1; x < width + 1; ++x) {
            for (int y = -1; y < height + 1; ++y) {
                boolean isValueSet = matrix.addValue(new Coordinates(x, y), insideValue);
                double testedValue = matrix.getValue(x, y);
                double testedValue2 = matrix.getValue(new Coordinates(x, y));

                assertEquals(testedValue, testedValue2);
                assertEquals(isValueSet, (x >= 0 && x < width && y >= 0 && y < height));
                assertTrue((isValueSet && testedValue == insideValue) || (!isValueSet && testedValue == outsideValue));
            }
        }
    }

    @Test
    void test_splashes_zero_impact_has_no_effect() {
        int width = 99;
        int height = 99;
        Dimensions dimensions = new Dimensions(width, height);
        double outsideValue = -765.0d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(dimensions, outsideValue);

        assertFalse(matrix.splash1stOrder(new Coordinates(3, 3), 0.0d));
        assertFalse(matrix.splash1stOrder(new Coordinates(3, 3), 0.0d, 10.0d));

        assertFalse(matrix.splash2ndOrder(new Coordinates(3, 3), 0.0d));
        assertFalse(matrix.splash2ndOrder(new Coordinates(3, 3), 0.0d, 10.0d));
    }

    @Test
    void test_splash_1st_order() {
        int width = 4;
        int height = 4;
        Dimensions dimensions = new Dimensions(width, height);
        double outsideValue = -123.0d;
        double valueAtImpact = 1.0d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(dimensions, outsideValue);

        assertFalse(matrix.splash1stOrder(new Coordinates(-1, -1), valueAtImpact));
        assertFalse(matrix.splash1stOrder(new Coordinates(width, height), valueAtImpact));

        int impactX = 1;
        int impactY = 1;

        assertTrue(matrix.splash1stOrder(new Coordinates(impactX, impactY), valueAtImpact));

        assertThat(matrix.getValue(new Coordinates(impactX, impactY)), is(equalTo(valueAtImpact)));

        assertThat(matrix.getValue(new Coordinates(impactX, impactY - 1)), is(closeTo(valueAtImpact / 2.0d, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX - 1, impactY)), is(closeTo(valueAtImpact / 2.0d, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX + 1, impactY)), is(closeTo(valueAtImpact / 2.0d, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX, impactY + 1)), is(closeTo(valueAtImpact / 2.0d, fuzz)));
    }

    @Test
    void test_splash_2nd_order() {
        int width = 4;
        int height = 4;
        Dimensions dimensions = new Dimensions(width, height);
        double outsideValue = -123.0d;
        double valueAtImpact = 1.0d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(dimensions, outsideValue);

        assertFalse(matrix.splash2ndOrder(new Coordinates(-1, -1), valueAtImpact));
        assertFalse(matrix.splash2ndOrder(new Coordinates(width, height), valueAtImpact));

        int impactX = 1;
        int impactY = 1;

        assertTrue(matrix.splash2ndOrder(new Coordinates(impactX, impactY), valueAtImpact));

        assertThat(matrix.getValue(new Coordinates(impactX, impactY)), is(equalTo(valueAtImpact)));

        assertThat(matrix.getValue(new Coordinates(impactX, impactY - 1)), is(closeTo(valueAtImpact / 2.0d, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX - 1, impactY)), is(closeTo(valueAtImpact / 2.0d, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX + 1, impactY)), is(closeTo(valueAtImpact / 2.0d, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX, impactY + 1)), is(closeTo(valueAtImpact / 2.0d, fuzz)));

        assertThat(matrix.getValue(new Coordinates(impactX - 1, impactY - 1)), is(closeTo(valueAtImpact / 4.0d, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX - 1, impactY + 1)), is(closeTo(valueAtImpact / 4.0d, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX + 1, impactY - 1)), is(closeTo(valueAtImpact / 4.0d, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX + 1, impactY + 1)), is(closeTo(valueAtImpact / 4.0d, fuzz)));
    }

    @Test
    void test_splash_custom_denominator() {
        int width = 4;
        int height = 4;
        Dimensions dimensions = new Dimensions(width, height);
        double outsideValue = -456.0d;
        double valueAtImpact = 1.0d;
        double denominator = 4.0d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(dimensions, outsideValue);

        assertFalse(matrix.splash1stOrder(new Coordinates(-1, -1), valueAtImpact, denominator));
        assertFalse(matrix.splash1stOrder(new Coordinates(width, height), valueAtImpact, denominator));

        int impactX = 1;
        int impactY = 1;

        assertTrue(matrix.splash1stOrder(new Coordinates(impactX, impactY), valueAtImpact, denominator));

        assertThat(matrix.getValue(new Coordinates(impactX, impactY)), is(equalTo(valueAtImpact)));

        assertThat(matrix.getValue(new Coordinates(impactX, impactY - 1)),
                is(closeTo(valueAtImpact / denominator, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX - 1, impactY)),
                is(closeTo(valueAtImpact / denominator, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX + 1, impactY)),
                is(closeTo(valueAtImpact / denominator, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX, impactY + 1)),
                is(closeTo(valueAtImpact / denominator, fuzz)));

        matrix.zero();

        assertFalse(matrix.splash2ndOrder(new Coordinates(-1, -1), valueAtImpact, denominator));
        assertFalse(matrix.splash2ndOrder(new Coordinates(width, height), valueAtImpact, denominator));

        assertTrue(matrix.splash2ndOrder(new Coordinates(impactX, impactY), valueAtImpact, denominator));

        assertThat(matrix.getValue(new Coordinates(impactX, impactY)), is(equalTo(valueAtImpact)));

        assertThat(matrix.getValue(new Coordinates(impactX, impactY - 1)),
                is(closeTo(valueAtImpact / denominator, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX - 1, impactY)),
                is(closeTo(valueAtImpact / denominator, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX + 1, impactY)),
                is(closeTo(valueAtImpact / denominator, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX, impactY + 1)),
                is(closeTo(valueAtImpact / denominator, fuzz)));

        assertThat(matrix.getValue(new Coordinates(impactX - 1, impactY - 1)),
                is(closeTo(valueAtImpact / denominator / denominator, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX - 1, impactY + 1)),
                is(closeTo(valueAtImpact / denominator / denominator, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX + 1, impactY - 1)),
                is(closeTo(valueAtImpact / denominator / denominator, fuzz)));
        assertThat(matrix.getValue(new Coordinates(impactX + 1, impactY + 1)),
                is(closeTo(valueAtImpact / denominator / denominator, fuzz)));
    }

    @Test
    void test_additive() {
        int width = 2;
        int height = 2;
        Dimensions dimensions = new Dimensions(width, height);
        double outsideValue = -1.0d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(dimensions, outsideValue);

        assertTrue(matrix.splash2ndOrder(new Coordinates(0, 0), 4.0d));
        assertTrue(matrix.splash2ndOrder(new Coordinates(1, 1), -1.0d));

        assertThat(matrix.getValue(new Coordinates(0, 0)), is(closeTo(3.75d, fuzz)));
        assertThat(matrix.getValue(new Coordinates(0, 1)), is(closeTo(1.5d, fuzz)));
        assertThat(matrix.getValue(new Coordinates(1, 0)), is(closeTo(1.5d, fuzz)));
        assertThat(matrix.getValue(new Coordinates(1, 1)), is(closeTo(0.0d, fuzz)));
    }
}

package ru.elynx.battlesnake.engine.math;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Internals")
class DoubleMatrixTest {
    private static final double fuzz = 0.0001d;

    @Test
    void test_make_zero_matrix() {
        int width = 11, height = 15;
        double outsideValue = -2.0d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(width, height, outsideValue);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertThat(matrix.getValue(x, y), is(equalTo(0.0d)));
            }
        }
    }

    @Test
    void test_add_get() {
        int width = 11, height = 15;
        double outsideValue = -2.0d;
        double insideValue = 123.4d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(width, height, outsideValue);

        for (int x = -1; x < width + 1; ++x) {
            for (int y = -1; y < height + 1; ++y) {
                boolean isValueSet = matrix.addValue(x, y, insideValue);
                double testedValue = matrix.getValue(x, y);

                assertEquals(isValueSet, (x >= 0 && x < width && y >= 0 && y < height));
                assertTrue((isValueSet && testedValue == insideValue) || (!isValueSet && testedValue == outsideValue));
            }
        }
    }

    @Test
    void test_zero() {
        int width = 11, height = 15;
        double outsideValue = -2.0d;
        double insideValue = 123.4d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(width, height, outsideValue);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertTrue(matrix.addValue(x, y, insideValue));
            }
        }

        matrix.zero();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                assertThat(matrix.getValue(x, y), is(equalTo(0.0d)));
            }
        }
    }

    @Test
    void test_splash_1st_order() {
        int width = 4;
        int height = 4;
        double outsideValue = -123.0d;
        double valueAtImpact = 1.0d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(width, height, outsideValue);

        assertFalse(matrix.splash1stOrder(-1, -1, valueAtImpact));
        assertFalse(matrix.splash1stOrder(width, height, valueAtImpact));

        int impactX = 1;
        int impactY = 1;

        assertTrue(matrix.splash1stOrder(impactX, impactY, valueAtImpact));

        assertThat(matrix.getValue(impactX, impactY), is(equalTo(valueAtImpact)));

        assertThat(matrix.getValue(impactX, impactY - 1), is(closeTo(valueAtImpact / 2.0d, fuzz)));
        assertThat(matrix.getValue(impactX - 1, impactY), is(closeTo(valueAtImpact / 2.0d, fuzz)));
        assertThat(matrix.getValue(impactX + 1, impactY), is(closeTo(valueAtImpact / 2.0d, fuzz)));
        assertThat(matrix.getValue(impactX, impactY + 1), is(closeTo(valueAtImpact / 2.0d, fuzz)));
    }

    @Test
    void test_splash_2nd_order() {
        int width = 4;
        int height = 4;
        double outsideValue = -123.0d;
        double valueAtImpact = 1.0d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(width, height, outsideValue);

        assertFalse(matrix.splash2ndOrder(-1, -1, valueAtImpact));
        assertFalse(matrix.splash2ndOrder(width, height, valueAtImpact));

        int impactX = 1;
        int impactY = 1;

        assertTrue(matrix.splash2ndOrder(impactX, impactY, valueAtImpact));

        assertThat(matrix.getValue(impactX, impactY), is(equalTo(valueAtImpact)));

        assertThat(matrix.getValue(impactX, impactY - 1), is(closeTo(valueAtImpact / 2.0d, fuzz)));
        assertThat(matrix.getValue(impactX - 1, impactY), is(closeTo(valueAtImpact / 2.0d, fuzz)));
        assertThat(matrix.getValue(impactX + 1, impactY), is(closeTo(valueAtImpact / 2.0d, fuzz)));
        assertThat(matrix.getValue(impactX, impactY + 1), is(closeTo(valueAtImpact / 2.0d, fuzz)));

        assertThat(matrix.getValue(impactX - 1, impactY - 1), is(closeTo(valueAtImpact / 4.0d, fuzz)));
        assertThat(matrix.getValue(impactX - 1, impactY + 1), is(closeTo(valueAtImpact / 4.0d, fuzz)));
        assertThat(matrix.getValue(impactX + 1, impactY - 1), is(closeTo(valueAtImpact / 4.0d, fuzz)));
        assertThat(matrix.getValue(impactX + 1, impactY + 1), is(closeTo(valueAtImpact / 4.0d, fuzz)));
    }

    @Test
    void test_splash_custom_denominator() {
        // TODO base on constants, not hardcode
        double v = 1.0d;
        double d = 4.0d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(4, 4, -456.0d);

        assertFalse(matrix.splash1stOrder(-1, -1, v, d));
        assertFalse(matrix.splash1stOrder(4, 4, v, d));
        assertTrue(matrix.splash1stOrder(1, 1, v, d));

        assertThat(matrix.getValue(1, 1), is(equalTo(v)));

        assertThat(matrix.getValue(1, 0), is(closeTo(v / d, fuzz)));
        assertThat(matrix.getValue(0, 1), is(closeTo(v / d, fuzz)));
        assertThat(matrix.getValue(2, 1), is(closeTo(v / d, fuzz)));
        assertThat(matrix.getValue(1, 2), is(closeTo(v / d, fuzz)));

        matrix.zero();

        assertFalse(matrix.splash2ndOrder(-1, -1, v, d));
        assertFalse(matrix.splash2ndOrder(4, 4, v, d));
        assertTrue(matrix.splash2ndOrder(1, 1, v, d));

        assertThat(matrix.getValue(1, 1), is(equalTo(v)));

        assertThat(matrix.getValue(1, 0), is(closeTo(v / d, fuzz)));
        assertThat(matrix.getValue(0, 1), is(closeTo(v / d, fuzz)));
        assertThat(matrix.getValue(2, 1), is(closeTo(v / d, fuzz)));
        assertThat(matrix.getValue(1, 2), is(closeTo(v / d, fuzz)));

        assertThat(matrix.getValue(0, 0), is(closeTo(v / d / d, fuzz)));
        assertThat(matrix.getValue(0, 2), is(closeTo(v / d / d, fuzz)));
        assertThat(matrix.getValue(2, 0), is(closeTo(v / d / d, fuzz)));
        assertThat(matrix.getValue(2, 2), is(closeTo(v / d / d, fuzz)));
    }

    @Test
    void test_additive() {
        // TODO base on constants, not hardcode
        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(2, 2, -1.0d);

        assertTrue(matrix.splash2ndOrder(0, 0, 4.0d));
        assertTrue(matrix.splash2ndOrder(1, 1, -1.0d));

        assertThat(matrix.getValue(0, 0), is(closeTo(3.75d, fuzz)));
        assertThat(matrix.getValue(0, 1), is(closeTo(1.5d, fuzz)));
        assertThat(matrix.getValue(1, 0), is(closeTo(1.5d, fuzz)));
        assertThat(matrix.getValue(1, 1), is(closeTo(0.0d, fuzz)));
    }
}

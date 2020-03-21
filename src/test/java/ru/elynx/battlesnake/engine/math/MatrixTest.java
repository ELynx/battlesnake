package ru.elynx.battlesnake.engine.math;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class MatrixTest {
    private final static double fuzz = 0.0001d;

    @Test
    public void zeroMatrix() throws Exception {
        final int w = 11, h = 15;
        final double wl = -2.0d;

        Matrix matrix = Matrix.zeroMatrix(w, h, wl);

        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                assert (0.0d == matrix.getValue(x, y));
            }
        }
    }

    @Test
    public void getSetZero() throws Exception {
        final int w = 11, h = 15;
        final double wl = -2.0d;
        final double v = 123.4d;

        Matrix matrix = Matrix.zeroMatrix(w, h, wl);

        for (int x = -1; x < w + 1; ++x) {
            for (int y = -1; y < h + 1; ++y) {
                matrix.setValue(x, y, v);

                double v2 = matrix.getValue(x, y);

                assert (v2 == v || v2 == wl); // TODO indexes
            }
        }

        // TODO as separate test
        matrix.zero();

        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                assert (0.0d == matrix.getValue(x, y));
            }
        }
    }

    @Test
    public void splash1stOrder() throws Exception {
        final double v = 1.0d;

        Matrix matrix = Matrix.zeroMatrix(4, 4, -123.0d);

        assert (!matrix.splash1stOrder(-1, -1, v));
        assert (!matrix.splash1stOrder(4, 4, v));
        assert (matrix.splash1stOrder(1, 1, v));

        assert (v == matrix.getValue(1, 1));

        assertThat(v / 2.0d, is(closeTo(matrix.getValue(1, 0), fuzz)));
        assertThat(v / 2.0d, is(closeTo(matrix.getValue(0, 1), fuzz)));
        assertThat(v / 2.0d, is(closeTo(matrix.getValue(2, 1), fuzz)));
        assertThat(v / 2.0d, is(closeTo(matrix.getValue(1, 2), fuzz)));
    }

    @Test
    public void splash2ndOrder() throws Exception {
        final double v = 1.0d;

        Matrix matrix = Matrix.zeroMatrix(4, 4, -123.0d);

        assert (!matrix.splash2ndOrder(-1, -1, v));
        assert (!matrix.splash2ndOrder(4, 4, v));
        assert (matrix.splash2ndOrder(1, 1, v));

        assert (v == matrix.getValue(1, 1));

        assertThat(v / 2.0d, is(closeTo(matrix.getValue(1, 0), fuzz)));
        assertThat(v / 2.0d, is(closeTo(matrix.getValue(0, 1), fuzz)));
        assertThat(v / 2.0d, is(closeTo(matrix.getValue(2, 1), fuzz)));
        assertThat(v / 2.0d, is(closeTo(matrix.getValue(1, 2), fuzz)));

        assertThat(v / 4.0d, is(closeTo(matrix.getValue(0, 0), fuzz)));
        assertThat(v / 4.0d, is(closeTo(matrix.getValue(0, 2), fuzz)));
        assertThat(v / 4.0d, is(closeTo(matrix.getValue(2, 0), fuzz)));
        assertThat(v / 4.0d, is(closeTo(matrix.getValue(2, 2), fuzz)));
    }

    @Test
    public void splashCustomDenominator() throws Exception {
        final double v = 1.0d;
        final double d = 4.0d;

        Matrix matrix = Matrix.zeroMatrix(4, 4, -456.0d);

        assert (!matrix.splash1stOrder(-1, -1, v, d));
        assert (!matrix.splash1stOrder(4, 4, v, d));
        assert (matrix.splash1stOrder(1, 1, v, d));

        assert (v == matrix.getValue(1, 1));

        assertThat(v / d, is(closeTo(matrix.getValue(1, 0), fuzz)));
        assertThat(v / d, is(closeTo(matrix.getValue(0, 1), fuzz)));
        assertThat(v / d, is(closeTo(matrix.getValue(2, 1), fuzz)));
        assertThat(v / d, is(closeTo(matrix.getValue(1, 2), fuzz)));

        matrix.zero();

        assert (!matrix.splash2ndOrder(-1, -1, v, d));
        assert (!matrix.splash2ndOrder(4, 4, v, d));
        assert (matrix.splash2ndOrder(1, 1, v, d));

        assert (v == matrix.getValue(1, 1));

        assertThat(v / d, is(closeTo(matrix.getValue(1, 0), fuzz)));
        assertThat(v / d, is(closeTo(matrix.getValue(0, 1), fuzz)));
        assertThat(v / d, is(closeTo(matrix.getValue(2, 1), fuzz)));
        assertThat(v / d, is(closeTo(matrix.getValue(1, 2), fuzz)));

        assertThat(v / d / d, is(closeTo(matrix.getValue(0, 0), fuzz)));
        assertThat(v / d / d, is(closeTo(matrix.getValue(0, 2), fuzz)));
        assertThat(v / d / d, is(closeTo(matrix.getValue(2, 0), fuzz)));
        assertThat(v / d / d, is(closeTo(matrix.getValue(2, 2), fuzz)));
    }

    @Test
    public void splashImpactOrder() throws Exception {
        Matrix matrix = Matrix.zeroMatrix(2, 2, -1.0d);

        matrix.splash2ndOrder(0, 0, 4.0d);
        matrix.splash2ndOrder(1, 1, -1.0d);

        assertThat(3.75d, is(closeTo(matrix.getValue(0, 0), fuzz)));
        assertThat(1.5d, is(closeTo(matrix.getValue(0, 1), fuzz)));
        assertThat(1.5d, is(closeTo(matrix.getValue(1, 0), fuzz)));
        assertThat(-1.0d, is(closeTo(matrix.getValue(1, 1), fuzz)));
    }
}

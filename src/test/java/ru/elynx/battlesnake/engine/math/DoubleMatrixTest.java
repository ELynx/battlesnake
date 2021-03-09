package ru.elynx.battlesnake.engine.math;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class DoubleMatrixTest {
    private static final double fuzz = 0.0001d;

    @Test
    void zeroMatrix() {
        final int w = 11, h = 15;
        final double wl = -2.0d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(w, h, wl);

        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                assertThat(matrix.getValue(x, y), is(equalTo(0.0d)));
            }
        }
    }

    @Test
    void setGet() {
        final int w = 11, h = 15;
        final double wl = -2.0d;
        final double v = 123.4d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(w, h, wl);

        for (int x = -1; x < w + 1; ++x) {
            for (int y = -1; y < h + 1; ++y) {
                boolean vSet = matrix.setValue(x, y, v);
                double v2 = matrix.getValue(x, y);

                assert (vSet == (x >= 0 && x < w && y >= 0 && y < h));
                assert ((vSet && v2 == v) || (!vSet && v2 == wl));
            }
        }
    }

    @Test
    void zero() {
        final int w = 11, h = 15;
        final double wl = -2.0d;
        final double v = 123.4d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(w, h, wl);

        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                assert (matrix.setValue(x, y, v));
            }
        }

        matrix.zero();

        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                assertThat(matrix.getValue(x, y), is(equalTo(0.0d)));
            }
        }
    }

    @Test
    void splash1stOrder() {
        final double v = 1.0d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(4, 4, -123.0d);

        assert (!matrix.splash1stOrder(-1, -1, v));
        assert (!matrix.splash1stOrder(4, 4, v));
        assert (matrix.splash1stOrder(1, 1, v));

        assertThat(matrix.getValue(1, 1), is(equalTo(v)));

        assertThat(matrix.getValue(1, 0), is(closeTo(v / 2.0d, fuzz)));
        assertThat(matrix.getValue(0, 1), is(closeTo(v / 2.0d, fuzz)));
        assertThat(matrix.getValue(2, 1), is(closeTo(v / 2.0d, fuzz)));
        assertThat(matrix.getValue(1, 2), is(closeTo(v / 2.0d, fuzz)));
    }

    @Test
    void splash2ndOrder() {
        final double v = 1.0d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(4, 4, -123.0d);

        assert (!matrix.splash2ndOrder(-1, -1, v));
        assert (!matrix.splash2ndOrder(4, 4, v));
        assert (matrix.splash2ndOrder(1, 1, v));

        assertThat(matrix.getValue(1, 1), is(equalTo(v)));

        assertThat(matrix.getValue(1, 0), is(closeTo(v / 2.0d, fuzz)));
        assertThat(matrix.getValue(0, 1), is(closeTo(v / 2.0d, fuzz)));
        assertThat(matrix.getValue(2, 1), is(closeTo(v / 2.0d, fuzz)));
        assertThat(matrix.getValue(1, 2), is(closeTo(v / 2.0d, fuzz)));

        assertThat(matrix.getValue(0, 0), is(closeTo(v / 4.0d, fuzz)));
        assertThat(matrix.getValue(0, 2), is(closeTo(v / 4.0d, fuzz)));
        assertThat(matrix.getValue(2, 0), is(closeTo(v / 4.0d, fuzz)));
        assertThat(matrix.getValue(2, 2), is(closeTo(v / 4.0d, fuzz)));
    }

    @Test
    void splashCustomDenominator() {
        final double v = 1.0d;
        final double d = 4.0d;

        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(4, 4, -456.0d);

        assert (!matrix.splash1stOrder(-1, -1, v, d));
        assert (!matrix.splash1stOrder(4, 4, v, d));
        assert (matrix.splash1stOrder(1, 1, v, d));

        assertThat(matrix.getValue(1, 1), is(equalTo(v)));

        assertThat(matrix.getValue(1, 0), is(closeTo(v / d, fuzz)));
        assertThat(matrix.getValue(0, 1), is(closeTo(v / d, fuzz)));
        assertThat(matrix.getValue(2, 1), is(closeTo(v / d, fuzz)));
        assertThat(matrix.getValue(1, 2), is(closeTo(v / d, fuzz)));

        matrix.zero();

        assert (!matrix.splash2ndOrder(-1, -1, v, d));
        assert (!matrix.splash2ndOrder(4, 4, v, d));
        assert (matrix.splash2ndOrder(1, 1, v, d));

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
    void splashImpactOrder() {
        DoubleMatrix matrix = DoubleMatrix.zeroMatrix(2, 2, -1.0d);

        assert (matrix.splash2ndOrder(0, 0, 4.0d));
        assert (matrix.splash2ndOrder(1, 1, -1.0d));

        assertThat(matrix.getValue(0, 0), is(closeTo(4.0d, fuzz)));
        assertThat(matrix.getValue(0, 1), is(closeTo(1.5d, fuzz)));
        assertThat(matrix.getValue(1, 0), is(closeTo(1.5d, fuzz)));
        assertThat(matrix.getValue(1, 1), is(closeTo(-1.0d, fuzz)));
    }
}

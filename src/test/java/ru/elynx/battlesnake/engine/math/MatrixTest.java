package ru.elynx.battlesnake.engine.math;

import org.junit.jupiter.api.Test;

public class MatrixTest {
    @Test
    public void zeroMatrix() {
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
    public void getSetZero() {
        final int w = 11, h = 15;
        final double wl = -2.0d;
        final double v = 123.4;

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
    public void splash1stOrder() {
        final double v = 1.0d;

        Matrix matrix = Matrix.zeroMatrix(4, 4, -123.0);

        assert (!matrix.splash1stOrder(-1, -1, v));
        assert (!matrix.splash1stOrder(4, 4, v));
        assert (matrix.splash1stOrder(1, 1, v));

        assert (v == matrix.getValue(1, 1));

        assert (v / 2.0d == matrix.getValue(1, 0));
        assert (v / 2.0d == matrix.getValue(0, 1));
        assert (v / 2.0d == matrix.getValue(2, 1));
        assert (v / 2.0d == matrix.getValue(1, 2));
    }

    @Test
    public void splash2ndOrder() {
        final double v = 1.0d;

        Matrix matrix = Matrix.zeroMatrix(4, 4, -123.0);

        assert (!matrix.splash2ndOrder(-1, -1, v));
        assert (!matrix.splash2ndOrder(4, 4, v));
        assert (matrix.splash2ndOrder(1, 1, v));

        assert (v == matrix.getValue(1, 1));

        assert (v / 2.0d == matrix.getValue(1, 0));
        assert (v / 2.0d == matrix.getValue(0, 1));
        assert (v / 2.0d == matrix.getValue(2, 1));
        assert (v / 2.0d == matrix.getValue(1, 2));

        assert (v / 4.0d == matrix.getValue(0, 0));
        assert (v / 4.0d == matrix.getValue(0, 2));
        assert (v / 4.0d == matrix.getValue(2, 0));
        assert (v / 4.0d == matrix.getValue(2, 2));
    }
}

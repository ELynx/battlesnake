package ru.elynx.battlesnake.engine.math;

import org.junit.jupiter.api.Test;

public class FlagMatrixTest {
    @Test
    public void falseMatrix() throws Exception {
        final int w = 11, h = 15;
        final boolean wl = true;

        FlagMatrix matrix = FlagMatrix.resettedMatrix(w, h, wl);

        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                assert (false == matrix.getValue(x, y));
            }
        }
    }

    @Test
    public void getSet() throws Exception {
        final int w = 11, h = 15;
        final boolean wl = true;

        FlagMatrix matrix = FlagMatrix.resettedMatrix(w, h, wl);

        for (int x = -1; x < w + 1; ++x) {
            for (int y = -1; y < h + 1; ++y) {
                final boolean v = x % 2 == y % 2;

                matrix.setValue(x, y, v);

                boolean v2 = matrix.getValue(x, y);

                assert (v2 == v || v2 == wl); // TODO indexes
            }
        }
    }

    @Test
    public void reset() throws Exception {
        final int w = 11, h = 15;
        final boolean w1 = true;
        final boolean w2 = false;

        FlagMatrix matrix = FlagMatrix.resettedMatrix(w, h, w1);
        matrix.reset();
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                assert (w1 != matrix.getValue(x, y));
            }
        }

        matrix = FlagMatrix.resettedMatrix(w, h, w2);
        matrix.reset();
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                assert (w2 != matrix.getValue(x, y));
            }
        }
    }
}

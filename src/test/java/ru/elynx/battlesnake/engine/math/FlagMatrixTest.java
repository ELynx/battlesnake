package ru.elynx.battlesnake.engine.math;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlagMatrixTest {
    @Test
    void falseMatrix() {
        final int w = 11, h = 15;
        final boolean wl = true;

        FlagMatrix matrix = FlagMatrix.resettedMatrix(w, h, wl);

        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                assertThat(matrix.getValue(x, y), is(false));
            }
        }
    }

    @Test
    void setGet() {
        final int w = 11, h = 15;
        final boolean wl = true;

        FlagMatrix matrix = FlagMatrix.resettedMatrix(w, h, wl);

        for (int x = -1; x < w + 1; ++x) {
            for (int y = -1; y < h + 1; ++y) {
                final boolean v = x % 2 == y % 2;

                boolean vSet = matrix.setValue(x, y, v);
                boolean v2 = matrix.getValue(x, y);

                assertEquals(vSet, (x >= 0 && x < w && y >= 0 && y < h));
                assertTrue((vSet && v2 == v) || (!vSet && v2 == wl)); // for ease of read
            }
        }
    }

    @Test
    void reset() {
        final int w = 11, h = 15;
        final boolean w1 = true;
        final boolean w2 = false;

        FlagMatrix matrix = FlagMatrix.uninitializedMatrix(w, h, w1);
        matrix.reset();
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                assertThat(matrix.getValue(x, y), is(not(w1)));
            }
        }

        matrix = FlagMatrix.uninitializedMatrix(w, h, w2);
        matrix.reset();
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < h; ++y) {
                assertThat(matrix.getValue(x, y), is(not(w2)));
            }
        }
    }
}

package ru.elynx.battlesnake.engine.math;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilTest {
    private static final double a = -100.0d, b = -50.0d, c = 0.0d, d = 0.3d, e = 0.5d, f = 1.0d, j = 50.0d, h = 100.0d;
    private static final double fuzz = 0.01d;

    @Test
    void clamp() {
        assertTrue(b == Util.clamp(a, b, c));
        assertTrue(a == Util.clamp(a, a, c));
        assertTrue(c == Util.clamp(a, c, c));

        assertTrue(d == Util.clamp(c, d, f));
        assertTrue(e == Util.clamp(c, e, f));

        assertTrue(j == Util.clamp(c, j, h));
        assertTrue(c == Util.clamp(c, c, h));
        assertTrue(h == Util.clamp(c, h, h));

        assertTrue(c == Util.clamp(c, a, f));
        assertTrue(f == Util.clamp(c, h, f));
    }

    @Test
    void scaleNormalized() {
        assertThat(Util.scale(a, c, c), is(closeTo(a, fuzz)));
        assertThat(Util.scale(a, e, c), is(closeTo(b, fuzz)));
        assertThat(Util.scale(a, f, c), is(closeTo(c, fuzz)));

        assertThat(Util.scale(c, c, h), is(closeTo(c, fuzz)));
        assertThat(Util.scale(c, e, h), is(closeTo(j, fuzz)));
        assertThat(Util.scale(c, f, h), is(closeTo(h, fuzz)));

        assertThat(Util.scale(a, e, h), is(closeTo(c, fuzz)));
    }

    @Test
    void scaleValueOfMaximum() {
        assertThat(Util.scale(a, j, h, c), is(closeTo(b, fuzz)));
        assertThat(Util.scale(c, b, a, h), is(closeTo(j, fuzz)));
    }
}

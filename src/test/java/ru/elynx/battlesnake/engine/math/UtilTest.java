package ru.elynx.battlesnake.engine.math;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class UtilTest {
    private final static double a = -100.0d, b = -50.0d, c = 0.0d, d = 0.3d, e = 0.5d, f = 1.0d, j = 50.0d, h = 100.0d;
    private final static double fuzz = 0.01d;

    @Test
    public void clamp() throws Exception {
        assert (b == Util.clamp(a, b, c));
        assert (a == Util.clamp(a, a, c));
        assert (c == Util.clamp(a, c, c));

        assert (d == Util.clamp(c, d, f));
        assert (e == Util.clamp(c, e, f));

        assert (j == Util.clamp(c, j, h));
        assert (c == Util.clamp(c, c, h));
        assert (h == Util.clamp(c, h, h));

        assert (c == Util.clamp(c, a, f));
        assert (f == Util.clamp(c, h, f));
    }

    @Test
    public void scaleNormalized() throws Exception {
        assertThat(Util.scale(a, c, c), is(closeTo(a, fuzz)));
        assertThat(Util.scale(a, e, c), is(closeTo(b, fuzz)));
        assertThat(Util.scale(a, f, c), is(closeTo(c, fuzz)));

        assertThat(Util.scale(c, c, h), is(closeTo(c, fuzz)));
        assertThat(Util.scale(c, e, h), is(closeTo(j, fuzz)));
        assertThat(Util.scale(c, f, h), is(closeTo(h, fuzz)));

        assertThat(Util.scale(a, e, h), is(closeTo(c, fuzz)));
    }

    @Test
    public void scaleValueOfMaximum() throws Exception {
        assertThat(Util.scale(a, j, h, c), is(closeTo(b, fuzz)));
        assertThat(Util.scale(c, b, a, h), is(closeTo(j, fuzz)));
    }
}

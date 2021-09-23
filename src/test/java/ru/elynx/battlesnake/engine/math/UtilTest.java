package ru.elynx.battlesnake.engine.math;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Internals")
class UtilTest {
    private static final double A = -100.0d, B = -50.0d, C = 0.0d, D = 0.3d, E = 0.5d, F = 1.0d, J = 50.0d, H = 100.0d;
    private static final double FUZZ = 0.01d;

    @Test
    void test_clamp() {
        assertEquals(B, Util.clamp(A, B, C));
        assertEquals(A, Util.clamp(A, A, C));
        assertEquals(C, Util.clamp(A, C, C));

        assertEquals(D, Util.clamp(C, D, F));
        assertEquals(E, Util.clamp(C, E, F));

        assertEquals(J, Util.clamp(C, J, H));
        assertEquals(C, Util.clamp(C, C, H));
        assertEquals(H, Util.clamp(C, H, H));

        assertEquals(C, Util.clamp(C, A, F));
        assertEquals(F, Util.clamp(C, H, F));
    }

    @Test
    void test_scale_proportion() {
        assertThat(Util.scale(A, C, C), is(closeTo(A, FUZZ)));
        assertThat(Util.scale(A, E, C), is(closeTo(B, FUZZ)));
        assertThat(Util.scale(A, F, C), is(closeTo(C, FUZZ)));

        assertThat(Util.scale(C, C, H), is(closeTo(C, FUZZ)));
        assertThat(Util.scale(C, E, H), is(closeTo(J, FUZZ)));
        assertThat(Util.scale(C, F, H), is(closeTo(H, FUZZ)));

        assertThat(Util.scale(A, E, H), is(closeTo(C, FUZZ)));
    }

    @Test
    void test_scale_value_of_maximum() {
        assertThat(Util.scale(A, J, H, C), is(closeTo(B, FUZZ)));
        assertThat(Util.scale(C, B, A, H), is(closeTo(J, FUZZ)));
    }
}

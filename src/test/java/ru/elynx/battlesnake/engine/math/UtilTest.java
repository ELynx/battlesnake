package ru.elynx.battlesnake.engine.math;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.protocol.CoordsDto;

@Tag("Internals")
class UtilTest {
    private static final double a = -100.0d, b = -50.0d, c = 0.0d, d = 0.3d, e = 0.5d, f = 1.0d, j = 50.0d, h = 100.0d;
    private static final double fuzz = 0.01d;

    private static final CoordsDto p1 = new CoordsDto(1, 1);
    private static final CoordsDto p2 = new CoordsDto(1, 2);
    private static final CoordsDto p3 = new CoordsDto(100, 100);

    @Test
    void test_clamp() {
        assertEquals(b, Util.clamp(a, b, c));
        assertEquals(a, Util.clamp(a, a, c));
        assertEquals(c, Util.clamp(a, c, c));

        assertEquals(d, Util.clamp(c, d, f));
        assertEquals(e, Util.clamp(c, e, f));

        assertEquals(j, Util.clamp(c, j, h));
        assertEquals(c, Util.clamp(c, c, h));
        assertEquals(h, Util.clamp(c, h, h));

        assertEquals(c, Util.clamp(c, a, f));
        assertEquals(f, Util.clamp(c, h, f));
    }

    @Test
    void test_scale_proportion() {
        assertThat(Util.scale(a, c, c), is(closeTo(a, fuzz)));
        assertThat(Util.scale(a, e, c), is(closeTo(b, fuzz)));
        assertThat(Util.scale(a, f, c), is(closeTo(c, fuzz)));

        assertThat(Util.scale(c, c, h), is(closeTo(c, fuzz)));
        assertThat(Util.scale(c, e, h), is(closeTo(j, fuzz)));
        assertThat(Util.scale(c, f, h), is(closeTo(h, fuzz)));

        assertThat(Util.scale(a, e, h), is(closeTo(c, fuzz)));
    }

    @Test
    void test_scale_value_of_maximum() {
        assertThat(Util.scale(a, j, h, c), is(closeTo(b, fuzz)));
        assertThat(Util.scale(c, b, a, h), is(closeTo(j, fuzz)));
    }

    @Test
    void test_manhattan_of_coordinate() {
        assertEquals(0, Util.manhattanDistance(p1, p1));
        assertEquals(0, Util.manhattanDistance(p2, p2));
        assertEquals(0, Util.manhattanDistance(p3, p3));

        assertEquals(Util.manhattanDistance(p1, p2), Util.manhattanDistance(p2, p1));
        assertEquals(Util.manhattanDistance(p1, p3), Util.manhattanDistance(p3, p1));
        assertEquals(Util.manhattanDistance(p2, p3), Util.manhattanDistance(p3, p2));

        assertEquals(1, Util.manhattanDistance(p1, p2));
        assertEquals(99 + 99, Util.manhattanDistance(p1, p3));
        assertEquals(99 + 98, Util.manhattanDistance(p2, p3));
    }

    void test_manhattan_of_coordinate_and_xy() {
        assertEquals(0, Util.manhattanDistance(p1, p1.getX(), p1.getY()));
        assertEquals(0, Util.manhattanDistance(p2, p2.getX(), p2.getY()));
        assertEquals(0, Util.manhattanDistance(p3, p3.getX(), p2.getY()));

        assertEquals(Util.manhattanDistance(p1, p2.getX(), p2.getY()),
                Util.manhattanDistance(p2, p1.getX(), p1.getY()));
        assertEquals(Util.manhattanDistance(p1, p3.getX(), p3.getY()),
                Util.manhattanDistance(p3, p1.getX(), p1.getY()));
        assertEquals(Util.manhattanDistance(p2, p3.getX(), p3.getY()),
                Util.manhattanDistance(p3, p2.getX(), p2.getY()));

        assertEquals(1, Util.manhattanDistance(p1, p2.getX(), p2.getY()));
        assertEquals(99 + 99, Util.manhattanDistance(p1, p3.getX(), p3.getY()));
        assertEquals(99 + 98, Util.manhattanDistance(p2, p3.getX(), p3.getY()));
    }
}

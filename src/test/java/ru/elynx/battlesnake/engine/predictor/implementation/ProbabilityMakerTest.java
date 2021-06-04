package ru.elynx.battlesnake.engine.predictor.implementation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;
import java.util.List;
import org.javatuples.Pair;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.entity.Coordinates;

@Tag("Internals")
class ProbabilityMakerTest {
    private static final double fuzz = 0.0001d;

    @Test
    void test_ctor() {
        ProbabilityMaker tested = new ProbabilityMaker();

        assertTrue(tested.isEmpty(), "Newly created is empty");
        assertEquals(0, tested.makeProbabilities().size(), "Newly created is empty");
    }

    @Test
    void test_add_and_reset() {
        ProbabilityMaker tested = new ProbabilityMaker();

        tested.addPosition(new Coordinates(0, 0));

        assertEquals(1, tested.makeProbabilities().size(), "Preparation");

        tested.reset();

        assertTrue(tested.isEmpty(), "Reset makes empty");
        assertEquals(0, tested.makeProbabilities().size(), "Reset makes empty");
    }

    @Test
    void test_add_excess() {
        ProbabilityMaker tested = new ProbabilityMaker();

        tested.addPosition(new Coordinates(0, 0));
        tested.addPosition(new Coordinates(1, 1));
        tested.addPosition(new Coordinates(2, 2));
        tested.addPosition(new Coordinates(3, 3));

        Coordinates oneTooMuch = new Coordinates(4, 4);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> tested.addPosition(oneTooMuch));
    }

    @Test
    void test_make() {
        ProbabilityMaker tested = new ProbabilityMaker();
        List<Pair<Coordinates, Double>> list;

        tested.addPosition(new Coordinates(0, 0));
        assertFalse(tested.isEmpty());
        list = tested.makeProbabilities();
        assertEquals(1, list.size());
        assertThat(list.get(0).getValue1(), is(closeTo(1.0d, fuzz)));

        tested.addPosition(new Coordinates(1, 1));
        assertFalse(tested.isEmpty());
        list = tested.makeProbabilities();
        assertEquals(2, list.size());
        assertThat(list.get(0).getValue1(), is(closeTo(0.5d, fuzz)));
        assertThat(list.get(1).getValue1(), is(closeTo(0.5d, fuzz)));

        tested.addPosition(new Coordinates(2, 2));
        assertFalse(tested.isEmpty());
        list = tested.makeProbabilities();
        assertEquals(3, list.size());
        assertThat(list.get(0).getValue1(), is(closeTo(1.0d / 3.0d, fuzz)));
        assertThat(list.get(1).getValue1(), is(closeTo(1.0d / 3.0d, fuzz)));
        assertThat(list.get(2).getValue1(), is(closeTo(1.0d / 3.0d, fuzz)));

        tested.addPosition(new Coordinates(3, 3));
        assertFalse(tested.isEmpty());
        list = tested.makeProbabilities();
        assertEquals(4, list.size());
        assertThat(list.get(0).getValue1(), is(closeTo(0.25d, fuzz)));
        assertThat(list.get(1).getValue1(), is(closeTo(0.25d, fuzz)));
        assertThat(list.get(2).getValue1(), is(closeTo(0.25d, fuzz)));
        assertThat(list.get(3).getValue1(), is(closeTo(0.25d, fuzz)));
    }

    @Test
    void test_make_scored() {
        ProbabilityMaker tested = new ProbabilityMaker();
        List<Pair<Coordinates, Double>> list;

        tested.addPositionWithScore(new Coordinates(0, 0), -1);
        tested.addPositionWithScore(new Coordinates(0, 0), 0);
        assertTrue(tested.isEmpty());
        list = tested.makeProbabilities();
        assertTrue(list.isEmpty(), "Non-positive score must be skipped");

        tested.addPositionWithScore(new Coordinates(0, 0), 3);
        assertFalse(tested.isEmpty());
        list = tested.makeProbabilities();
        assertEquals(1, list.size());
        assertThat(list.get(0).getValue1(), is(closeTo(1.0d, fuzz)));

        tested.addPositionWithScore(new Coordinates(1, 1), 3);
        assertFalse(tested.isEmpty());
        list = tested.makeProbabilities();
        assertEquals(2, list.size());
        assertThat(list.get(0).getValue1(), is(closeTo(0.5d, fuzz)));
        assertThat(list.get(1).getValue1(), is(closeTo(0.5d, fuzz)));

        tested.addPositionWithScore(new Coordinates(2, 2), 6);
        assertFalse(tested.isEmpty());
        list = tested.makeProbabilities();
        assertEquals(3, list.size());
        list.sort(Comparator.comparingDouble(Pair::getValue1));
        assertThat(list.get(0).getValue1(), is(closeTo(1.0d / 4.0d, fuzz)));
        assertThat(list.get(1).getValue1(), is(closeTo(1.0d / 4.0d, fuzz)));
        assertThat(list.get(2).getValue1(), is(closeTo(1.0d / 2.0d, fuzz)));

        tested.addPositionWithScore(new Coordinates(3, 3), 6);
        assertFalse(tested.isEmpty());
        list = tested.makeProbabilities();
        assertEquals(4, list.size());
        list.sort(Comparator.comparingDouble(Pair::getValue1));
        assertThat(list.get(0).getValue1(), is(closeTo(1.0d / 6.0d, fuzz)));
        assertThat(list.get(1).getValue1(), is(closeTo(1.0d / 6.0d, fuzz)));
        assertThat(list.get(2).getValue1(), is(closeTo(1.0d / 3.0d, fuzz)));
        assertThat(list.get(3).getValue1(), is(closeTo(1.0d / 3.0d, fuzz)));
    }
}

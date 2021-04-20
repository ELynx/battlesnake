package ru.elynx.battlesnake.engine.predictor.implementation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.javatuples.Triplet;
import org.junit.jupiter.api.Test;

class ProbabilityMakerTest {
    private static final double fuzz = 0.0001d;

    @Test
    void test_ctor() {
        ProbabilityMaker tested = new ProbabilityMaker();

        assertEquals(0, tested.make().size(), "Newly created is empty");
    }

    @Test
    void test_add_and_reset() {
        ProbabilityMaker tested = new ProbabilityMaker();

        tested.add(0, 0);

        assertEquals(1, tested.make().size(), "Preparation");

        tested.reset();

        assertEquals(0, tested.make().size(), "Reset makes empty");
    }

    @Test
    void test_add_excess() {
        ProbabilityMaker tested = new ProbabilityMaker();

        tested.add(0, 0);
        tested.add(1, 1);
        tested.add(2, 2);
        tested.add(3, 3);

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> tested.add(4, 4));
    }

    @Test
    void test_make() {
        ProbabilityMaker tested = new ProbabilityMaker();
        List<Triplet<Integer, Integer, Double>> list;

        tested.add(0, 0);
        list = tested.make();
        assertEquals(1, list.size());
        assertThat(list.get(0).getValue2(), is(closeTo(1.0d, fuzz)));

        tested.add(1, 1);
        list = tested.make();
        assertEquals(2, list.size());
        assertThat(list.get(0).getValue2(), is(closeTo(0.5d, fuzz)));
        assertThat(list.get(1).getValue2(), is(closeTo(0.5d, fuzz)));

        tested.add(2, 2);
        list = tested.make();
        assertEquals(3, list.size());
        assertThat(list.get(0).getValue2(), is(closeTo(1.0d / 3.0d, fuzz)));
        assertThat(list.get(1).getValue2(), is(closeTo(1.0d / 3.0d, fuzz)));
        assertThat(list.get(2).getValue2(), is(closeTo(1.0d / 3.0d, fuzz)));

        tested.add(3, 3);
        list = tested.make();
        assertEquals(4, list.size());
        assertThat(list.get(0).getValue2(), is(closeTo(0.25d, fuzz)));
        assertThat(list.get(1).getValue2(), is(closeTo(0.25d, fuzz)));
        assertThat(list.get(2).getValue2(), is(closeTo(0.25d, fuzz)));
        assertThat(list.get(3).getValue2(), is(closeTo(0.25d, fuzz)));
    }
}
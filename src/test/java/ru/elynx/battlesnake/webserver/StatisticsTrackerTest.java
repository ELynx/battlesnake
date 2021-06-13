package ru.elynx.battlesnake.webserver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.testbuilder.EntityBuilder;

@SpringBootTest
@Tag("Internals")
class StatisticsTrackerTest {
    @Test
    void test_root(@Autowired StatisticsTracker tested) {
        assertDoesNotThrow(() -> tested.trackRoot("Test"));
    }

    @Test
    void test_start(@Autowired StatisticsTracker tested) {
        assertDoesNotThrow(() -> tested.trackStart(EntityBuilder.hazardPredictor().getGameState()));
    }

    @Test
    void test_move(@Autowired StatisticsTracker tested) {
        assertDoesNotThrow(() -> tested.trackMove(EntityBuilder.hazardPredictor().getGameState()));
    }

    @Test
    void test_end(@Autowired StatisticsTracker tested) {
        assertDoesNotThrow(() -> tested.trackEnd(EntityBuilder.hazardPredictor().getGameState()));
    }

    @Test
    void ping(@Autowired StatisticsTracker tested) {
        assertDoesNotThrow(tested::ping);
    }

    @Test
    void test_get_pings(@Autowired StatisticsTracker tested) {
        assertDoesNotThrow(() -> {
            long tmp = tested.getPings();
            assertThat(tmp, is(greaterThanOrEqualTo(0L)));
        });

        long before = tested.getPings();
        tested.ping();
        tested.ping();
        tested.ping();
        long after = tested.getPings();

        assertEquals(before + 3, after);
    }
}

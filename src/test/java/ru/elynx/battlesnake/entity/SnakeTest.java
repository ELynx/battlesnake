package ru.elynx.battlesnake.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.testbuilder.EntityBuilder;

@Tag("Internals")
class SnakeTest {
    @Test
    void test_is_timed_out() {
        Snake tested1 = EntityBuilder.snakeWithTimeout(100);
        assertFalse(tested1.isTimedOut());

        Snake tested2 = EntityBuilder.snakeWithTimeout(0);
        assertTrue(tested2.isTimedOut());

        Snake testedInitialState = EntityBuilder.snakeWithTimeout(null);
        assertFalse(testedInitialState.isTimedOut());
    }
}

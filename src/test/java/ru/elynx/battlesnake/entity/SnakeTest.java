package ru.elynx.battlesnake.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.asciitest.AsciiToGameState;
import ru.elynx.battlesnake.testbuilder.EntityBuilder;

@Tag("Internals")
class SnakeTest {
    @Test
    void test_is_timed_out() {
        Snake tested1 = EntityBuilder.snakeWithLatency(100);
        assertFalse(tested1.isTimedOut());

        Snake tested2 = EntityBuilder.snakeWithLatency(0);
        assertTrue(tested2.isTimedOut());

        Snake testedInitialState = EntityBuilder.snakeWithLatency(null);
        assertFalse(testedInitialState.isTimedOut());
    }

    @Test
    void test_is_growing() {
        GameState gameState;

        gameState = new AsciiToGameState("yyY").setStartSnakeSize(3).setHealth("Y", 42).build().getGameState();
        assertFalse(gameState.getYou().isGrowing(), "Not growing under normal conditions");

        gameState = new AsciiToGameState("yY_").setStartSnakeSize(3).setHealth("Y", 42).build().getGameState();
        assertTrue(gameState.getYou().isGrowing(), "Growing when stepping on tail");

        gameState = new AsciiToGameState("yyY").setStartSnakeSize(3).setHealth("Y", 100).build().getGameState();
        assertTrue(gameState.getYou().isGrowing(), "Full health grows");
    }
}

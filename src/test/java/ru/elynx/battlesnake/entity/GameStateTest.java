package ru.elynx.battlesnake.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.testbuilder.EntityBuilder;

@Tag("Internals")
class GameStateTest {
    @Test
    void test_is_you_eliminated() {
        GameState gameState = EntityBuilder.gameState();

        assertFalse(gameState.isYouEliminated());
    }

    @Test
    void test_is_eliminated() {
        GameState gameState = EntityBuilder.gameState();

        assertFalse(gameState.isEliminated(gameState.getYou()));

        Snake snake = EntityBuilder.snakeWithId("Some non existence snake");
        assertTrue(gameState.isEliminated(snake));
    }
}

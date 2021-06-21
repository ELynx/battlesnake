package ru.elynx.battlesnake.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.testbuilder.EntityBuilder;

class GameStateTest {
    @Test
    void test_is_you_eliminated() {
        HazardPredictor entity1 = EntityBuilder.hazardPredictor();
        GameState gameState = entity1.getGameState();

        assertFalse(gameState.isYouEliminated());
    }

    @Test
    void test_is_eliminated() {
        HazardPredictor entity1 = EntityBuilder.hazardPredictor();
        GameState gameState = entity1.getGameState();

        assertFalse(gameState.isEliminated(gameState.getYou()));

        Snake snake = EntityBuilder.snakeWithId("Some non existence snake");
        assertTrue(gameState.isEliminated(snake));
    }
}

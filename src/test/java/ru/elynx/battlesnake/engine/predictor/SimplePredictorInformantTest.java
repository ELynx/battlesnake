package ru.elynx.battlesnake.engine.predictor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Dimensions;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Snake;
import ru.elynx.battlesnake.testbuilder.EntityBuilder;

@Tag("Internals")
class SimplePredictorInformantTest {
    @Test
    void test_out_of_bounds_is_not() {
        GameState gameState = EntityBuilder.gameState();

        SimplePredictorInformant tested = new SimplePredictorInformant(gameState);

        assertFalse(tested.isWalkable(new Coordinates(-1, -1)));
        assertFalse(tested.isWalkable(new Coordinates(-1, 0)));
        assertFalse(tested.isWalkable(new Coordinates(0, -1)));

        Dimensions dimensions = gameState.getBoard().getDimensions();

        assertFalse(tested.isWalkable(new Coordinates(dimensions.getWidth(), dimensions.getHeight())));
        assertFalse(tested.isWalkable(new Coordinates(dimensions.getWidth() - 1, dimensions.getHeight())));
        assertFalse(tested.isWalkable(new Coordinates(dimensions.getWidth(), dimensions.getHeight() - 1)));
    }

    @Test
    void test_snake_is_not() {
        GameState gameState = EntityBuilder.gameState();

        SimplePredictorInformant tested = new SimplePredictorInformant(gameState);

        for (Snake snake : gameState.getBoard().getSnakes()) {
            for (Coordinates body : snake.getBody()) {
                assertFalse(tested.isWalkable(body));
            }
        }
    }

    @Test
    void test_free_is() {
        GameState gameState = EntityBuilder.gameState();

        SimplePredictorInformant tested = new SimplePredictorInformant(gameState);

        assertTrue(tested.isWalkable(new Coordinates(1, 1)));
    }
}

package ru.elynx.battlesnake.engine.game.predictor;

import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

public class GameStatePredictor extends GameStateDto {
    private static final int INITIAL_LENGTH = 3;
    private static final int MAX_HEALTH = 100;

    /**
     * Predict if snake will grow on it's tail this turn Prediction is 100% if logic
     * is not changed
     *
     * @param snake
     *            Snake to be checked
     * @return True if snake will not empty it's tail cell.
     */
    public boolean isGrowing(SnakeDto snake) {
        // initial expansion
        if (getTurn() < INITIAL_LENGTH) {
            return true;
        }

        // just ate food
        return snake.getHealth().equals(MAX_HEALTH);
    }
}

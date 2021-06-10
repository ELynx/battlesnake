package ru.elynx.battlesnake.entity;

import lombok.NonNull;
import lombok.Value;

@Value
public class GameState {
    private static final int INITIAL_LENGTH = 3;
    private static final int MAX_HEALTH = 100;

    @NonNull
    String gameId;
    int turn;

    @NonNull
    Rules rules;

    @NonNull
    Board board;
    @NonNull
    Snake you;

    /**
     * Predict if snake will grow on it's tail this turn
     *
     * @param snake
     *            Snake to be checked
     * @return True if snake will not empty it's tail cell.
     */
    public boolean isSnakeGrowing(Snake snake) {
        // initial expansion
        if (getTurn() < INITIAL_LENGTH) {
            return true;
        }

        // just ate food
        return snake.getHealth() == MAX_HEALTH;
    }
}

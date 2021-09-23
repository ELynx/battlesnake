package ru.elynx.battlesnake.entity;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@NonFinal // for testing purposes
public class GameState {
    @NonNull
    String gameId;
    int turn;

    @NonNull
    Rules rules;

    @NonNull
    Board board;
    @NonNull
    Snake you;

    public boolean isYouEliminated() {
        return isEliminated(you);
    }

    public boolean isEliminated(Snake snake) {
        return board.getSnakes().stream().filter(x -> x.getId().equals(snake.getId())).findAny().isEmpty();
    }
}

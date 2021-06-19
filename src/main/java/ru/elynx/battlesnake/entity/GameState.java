package ru.elynx.battlesnake.entity;

import lombok.NonNull;
import lombok.Value;

@Value
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
}

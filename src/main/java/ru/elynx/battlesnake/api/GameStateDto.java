package ru.elynx.battlesnake.api;

import lombok.NonNull;
import lombok.Value;

@Value
public class GameStateDto {
    @NonNull
    GameDto game;
    @NonNull
    Integer turn;
    @NonNull
    BoardDto board;
    @NonNull
    SnakeDto you;
}

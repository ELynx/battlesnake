package ru.elynx.battlesnake.api;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
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

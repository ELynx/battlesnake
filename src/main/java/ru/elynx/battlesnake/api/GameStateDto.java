package ru.elynx.battlesnake.api;

import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
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

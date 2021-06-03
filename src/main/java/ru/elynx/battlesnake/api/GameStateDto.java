package ru.elynx.battlesnake.api;

import lombok.*;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
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

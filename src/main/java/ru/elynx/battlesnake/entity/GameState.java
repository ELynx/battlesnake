package ru.elynx.battlesnake.entity;

import lombok.Value;

@Value
public class GameState {
    String gameId;
    int turn;

    Rules rules;

    Board board;
    Snake you;
}

package ru.elynx.battlesnake.entity;

import lombok.NonNull;
import lombok.Value;

@Value
public class Move {
    @NonNull
    MoveCommand moveCommand;
    String shout; // TODO nullable
}

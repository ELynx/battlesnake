package ru.elynx.battlesnake.entity;

import lombok.NonNull;
import lombok.Value;

@Value
public class Move {
    @NonNull
    MoveCommand moveCommand;
    String shout;

    public Move(MoveCommand moveCommand) {
        this.moveCommand = moveCommand;
        this.shout = null;
    }

    public Move(MoveCommand moveCommand, String shout) {
        this.moveCommand = moveCommand;
        this.shout = shout;
    }
}

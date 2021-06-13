package ru.elynx.battlesnake.entity;

import lombok.NonNull;
import lombok.Value;

@Value
public class Move {
    @NonNull
    MoveCommand moveCommand;
    String shout;

    public Move(MoveCommand moveCommand) {
        validateMoveCommand(moveCommand);

        this.moveCommand = moveCommand;
        this.shout = null;
    }

    public Move(MoveCommand moveCommand, String shout) {
        validateMoveCommand(moveCommand);

        this.moveCommand = moveCommand;
        this.shout = shout;
    }

    private void validateMoveCommand(MoveCommand moveCommand) {
        if (moveCommand == null) {
            throw new NullPointerException("MoveCommand must be non-null");
        }
    }
}

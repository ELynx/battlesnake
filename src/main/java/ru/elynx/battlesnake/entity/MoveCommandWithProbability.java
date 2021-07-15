package ru.elynx.battlesnake.entity;

import java.util.List;
import lombok.Value;

@Value
public class MoveCommandWithProbability {
    MoveCommand moveCommand;
    double probability;

    public static MoveCommandWithProbability from(MoveCommand moveCommand) {
        return new MoveCommandWithProbability(moveCommand, 1.0d);
    }

    public static List<MoveCommandWithProbability> onlyFrom(MoveCommand moveCommand) {
        return List.of(from(moveCommand));
    }
}

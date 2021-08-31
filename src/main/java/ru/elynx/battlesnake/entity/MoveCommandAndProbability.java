package ru.elynx.battlesnake.entity;

import java.util.List;
import lombok.Value;

@Value
public class MoveCommandAndProbability {
    MoveCommand moveCommand;
    double probability;

    public static MoveCommandAndProbability from(MoveCommand moveCommand) {
        return new MoveCommandAndProbability(moveCommand, 1.0d);
    }

    public static List<MoveCommandAndProbability> onlyFrom(MoveCommand moveCommand) {
        return List.of(from(moveCommand));
    }
}

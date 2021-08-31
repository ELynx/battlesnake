package ru.elynx.battlesnake.entity;

import java.util.Collection;
import java.util.List;
import lombok.Value;

@Value
public class MoveCommandAndProbability {
    private static final double PROBABILITY_1 = 1.0d;

    MoveCommand moveCommand;
    double probability;

    public static MoveCommandAndProbability from(MoveCommand moveCommand) {
        return new MoveCommandAndProbability(moveCommand, PROBABILITY_1);
    }

    public static Collection<MoveCommandAndProbability> onlyFrom(MoveCommand moveCommand) {
        return List.of(from(moveCommand));
    }
}

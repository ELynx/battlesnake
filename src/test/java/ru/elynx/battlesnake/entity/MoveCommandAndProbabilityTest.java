package ru.elynx.battlesnake.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Internals")
class MoveCommandAndProbabilityTest {
    @Test
    void test_from() {
        for (MoveCommand moveCommand : MoveCommand.values()) {
            MoveCommandAndProbability tested = MoveCommandAndProbability.from(moveCommand);

            assertEquals(moveCommand, tested.getMoveCommand());
            assertEquals(1.0d, tested.getProbability());
        }
    }

    @Test
    void test_only_from() {
        for (MoveCommand moveCommand : MoveCommand.values()) {
            Collection<MoveCommandAndProbability> tested = MoveCommandAndProbability.onlyFrom(moveCommand);

            assertEquals(1, tested.size());

            tested.forEach(testedItem -> {
                assertEquals(moveCommand, testedItem.getMoveCommand());
                assertEquals(1.0d, testedItem.getProbability());
            });
        }
    }
}

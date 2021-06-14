package ru.elynx.battlesnake.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.asciitest.AsciiToGameState;

@Tag("Internals")
class GameStateTest {
    @Test
    void test_is_snake_growing() {
        AsciiToGameState generator = new AsciiToGameState("__Y__");
        GameState tested;

        tested = generator.build().getGameState();
        assertFalse(tested.isSnakeGrowing(tested.getYou()), "Not growing under normal conditions");

        for (int turn = 0; turn < 3; ++turn) {
            tested = generator.setTurn(turn).build().getGameState();
            assertTrue(tested.isSnakeGrowing(tested.getYou()), "Grows on turn [" + turn + ']');
        }

        tested = generator.setTurn(123).setHealth("Y", 100).build().getGameState();

        assertTrue(tested.isSnakeGrowing(tested.getYou()), "Grows on food");
    }
}

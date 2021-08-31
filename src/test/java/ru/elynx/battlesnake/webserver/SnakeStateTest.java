package ru.elynx.battlesnake.webserver;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Move;
import ru.elynx.battlesnake.entity.MoveCommand;
import ru.elynx.battlesnake.testbuilder.EntityBuilder;
import ru.elynx.battlesnake.testsnake.MySnake;

// TODO more thorough testing
@Tag("Internals")
class SnakeStateTest {
    IGameStrategy gameStrategy = new MySnake();

    @Test
    void test_is_last_accessed_before() {
        SnakeState tested = new SnakeState(gameStrategy);
        Instant compareTo = Instant.now();

        assertFalse(tested.isLastAccessedBefore(compareTo.minus(1, ChronoUnit.HOURS)));
        assertTrue(tested.isLastAccessedBefore(compareTo.plus(1, ChronoUnit.HOURS)));
    }

    @Test
    void test_process_start() {
        GameState gameState = EntityBuilder.gameState();

        SnakeState tested = new SnakeState(gameStrategy);

        assertDoesNotThrow(() -> tested.processStart(gameState));
    }

    @Test
    void test_process_move() {
        GameState gameState = EntityBuilder.gameState();

        SnakeState tested = new SnakeState(gameStrategy);

        Move move = tested.processMove(gameState);
        assertEquals(MoveCommand.UP, move.getMoveCommand());
    }

    @Test
    void test_process_end() {
        GameState gameState = EntityBuilder.gameState();

        SnakeState tested = new SnakeState(gameStrategy);

        assertDoesNotThrow(() -> tested.processEnd(gameState));
    }
}

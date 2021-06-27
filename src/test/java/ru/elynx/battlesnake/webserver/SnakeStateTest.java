package ru.elynx.battlesnake.webserver;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Move;
import ru.elynx.battlesnake.entity.MoveCommand;
import ru.elynx.battlesnake.testbuilder.EntityBuilder;
import ru.elynx.battlesnake.testsnake.MySnake;

// TODO more thorough testing
class SnakeStateTest {
    IGameStrategy mySnakeStrategy = new MySnake();

    @Test
    void test_i_las_accessed_before() {
        SnakeState tested = new SnakeState(mySnakeStrategy);
        Instant compareTo = Instant.now();

        assertFalse(tested.isLastAccessedBefore(compareTo.minus(1, ChronoUnit.HOURS)));
        assertTrue(tested.isLastAccessedBefore(compareTo.plus(1, ChronoUnit.HOURS)));
    }

    @Test
    void test_process_start() {
        GameState gameState = EntityBuilder.gameState();

        SnakeState tested = new SnakeState(mySnakeStrategy);

        assertDoesNotThrow(() -> tested.processStart(gameState));
    }

    @Test
    void test_move_start() {
        GameState gameState = EntityBuilder.gameState();

        SnakeState tested = new SnakeState(mySnakeStrategy);

        Move move = tested.processMove(gameState);
        assertEquals(MoveCommand.UP, move.getMoveCommand());
    }

    @Test
    void test_end_start() {
        GameState gameState = EntityBuilder.gameState();

        SnakeState tested = new SnakeState(mySnakeStrategy);

        assertDoesNotThrow(() -> tested.processEnd(gameState));
    }
}

package ru.elynx.battlesnake.webserver;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.engine.strategy.SnakeNotFoundException;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Move;
import ru.elynx.battlesnake.entity.MoveCommand;
import ru.elynx.battlesnake.testbuilder.EntityBuilder;
import ru.elynx.battlesnake.testsnake.MySnakeGameStrategyFactory;

@Tag("Internals")
class SnakeManagerTest {
    MySnakeGameStrategyFactory mySnakeFactory = new MySnakeGameStrategyFactory();

    @Test
    void test_clean_up_stale_snakes() {
        SnakeManager tested = new SnakeManager(mySnakeFactory);

        // no snakes
        assertDoesNotThrow(() -> tested.cleanStaleSnakeTest(Instant.now()));

        tested.start(EntityBuilder.gameStateWithName("My Snake"));

        // fresh snake
        assertDoesNotThrow(() -> tested.cleanStaleSnakeTest(Instant.now()));

        // advance time forward
        assertDoesNotThrow(() -> tested.cleanStaleSnakeTest(Instant.now().plus(1, ChronoUnit.HOURS)));
    }

    @Test
    void test_root_gives_snake_info() {
        SnakeManager tested = new SnakeManager(mySnakeFactory);
        BattlesnakeInfo battlesnakeInfo = tested.root("My Snake");

        assertEquals("Test Aut|hor", battlesnakeInfo.getAuthor());
    }

    @Test
    void test_root_throws_when_not_found() {
        SnakeManager tested = new SnakeManager(mySnakeFactory);
        assertThrows(SnakeNotFoundException.class, () -> tested.root("No Such Snake"));
    }

    @Test
    void test_start_does_not_throw() {
        SnakeManager tested = new SnakeManager(mySnakeFactory);

        GameState gameState = EntityBuilder.gameStateWithName("My Snake");

        assertDoesNotThrow(() -> tested.start(gameState));
    }

    @Test
    void test_start_throws_when_not_found() {
        SnakeManager tested = new SnakeManager(mySnakeFactory);

        GameState gameState = EntityBuilder.gameStateWithName("No Such Snake");

        assertThrows(SnakeNotFoundException.class, () -> tested.start(gameState));
    }

    @Test
    void test_move_does_not_throw() {
        SnakeManager tested = new SnakeManager(mySnakeFactory);

        GameState gameState = EntityBuilder.gameStateWithName("My Snake");

        Move move = tested.move(gameState);
        assertEquals(MoveCommand.UP, move.getMoveCommand());
    }

    @Test
    void test_move_does_not_throw_repeatedly() {
        SnakeManager tested = new SnakeManager(mySnakeFactory);

        GameState gameState = EntityBuilder.gameStateWithName("My Snake");

        Move move = tested.move(gameState);
        assertEquals(MoveCommand.UP, move.getMoveCommand());

        move = tested.move(gameState);
        assertEquals(MoveCommand.UP, move.getMoveCommand());
    }

    @Test
    void test_move_throws_when_not_found() {
        SnakeManager tested = new SnakeManager(mySnakeFactory);

        GameState gameState = EntityBuilder.gameStateWithName("No Such Snake");

        assertThrows(SnakeNotFoundException.class, () -> tested.move(gameState));
    }

    @Test
    void test_end_does_not_throw_when_prepared() {
        SnakeManager tested = new SnakeManager(mySnakeFactory);

        GameState gameState = EntityBuilder.gameStateWithName("My Snake");

        tested.start(gameState);

        assertDoesNotThrow(() -> tested.end(gameState));
    }

    @Test
    void test_end_throws_when_not_prepared() {
        SnakeManager tested = new SnakeManager(mySnakeFactory);

        GameState gameState = EntityBuilder.gameStateWithName("My Snake");

        assertThrows(SnakeNotFoundException.class, () -> tested.end(gameState));
    }

    @Test
    void test_end_throws_when_not_found() {
        SnakeManager tested = new SnakeManager(mySnakeFactory);

        GameState gameState = EntityBuilder.gameStateWithName("No Such Snake");

        assertThrows(SnakeNotFoundException.class, () -> tested.end(gameState));
    }
}

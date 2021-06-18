package ru.elynx.battlesnake.engine.advancer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.asciitest.AsciiToGameState;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.MoveCommand;
import ru.elynx.battlesnake.entity.Snake;
import ru.elynx.battlesnake.testbuilder.EntityBuilder;

class GameStateAdvancerTest {
    BiFunction<Snake, GameState, MoveCommand> moveRight = (Snake snake, GameState gameState) -> MoveCommand.RIGHT;

    @Test
    void test_constants() {
        HazardPredictor entity1 = EntityBuilder.hazardPredictor();
        GameState from = entity1.getGameState();
        GameState to = GameStateAdvancer.advance(from, moveRight);

        assertEquals(from.getGameId(), to.getGameId());
        assertEquals(from.getRules(), to.getRules());
    }

    @Test
    void test_turn_increment() {
        HazardPredictor entity1 = EntityBuilder.hazardPredictor();
        GameState from = entity1.getGameState();
        GameState to = GameStateAdvancer.advance(from, moveRight);

        assertEquals(from.getTurn() + 1, to.getTurn());
    }

    @Test
    void test_consistency() {
        HazardPredictor entity1 = EntityBuilder.hazardPredictor();
        GameState from = entity1.getGameState();
        GameState to = GameStateAdvancer.advance(from, moveRight);

        Function<Snake, Void> testSnake = (Snake snake) -> {
            assertEquals(snake.getHead(), snake.getBody().get(0));
            assertEquals(snake.getLength(), snake.getBody().size());
            return null;
        };

        testSnake.apply(to.getYou());
        for (Snake snake : to.getBoard().getSnakes()) {
            testSnake.apply(snake);
        }
    }

    @Test
    void test_snake_moves() {
        HazardPredictor entity1 = new AsciiToGameState("" + //
                "___\n" + //
                "_Y_\n" + //
                "___\n").setStartSnakeSize(1).build();
        GameState from = entity1.getGameState();

        HazardPredictor entity2 = new AsciiToGameState("" + //
                "___\n" + //
                "__Y\n" + //
                "___\n").setStartSnakeSize(1).build();
        GameState expected = entity2.getGameState();

        GameState to = GameStateAdvancer.advance(from, moveRight);

        assertEquals(expected.getBoard(), to.getBoard());
    }
}

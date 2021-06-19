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
    BiFunction<Snake, GameState, MoveCommand> moveDown = (Snake snake, GameState gameState) -> MoveCommand.DOWN;

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
                "yy_\n" + //
                "_Y_\n" + //
                "___\n").setHealth("Y", 25).build();
        GameState from = entity1.getGameState();

        HazardPredictor entity2 = new AsciiToGameState("" + //
                "_y_\n" + //
                "_yY\n" + //
                "___\n").setHealth("Y", 24).build();
        GameState expected = entity2.getGameState();

        GameState to = GameStateAdvancer.advance(from, moveRight);

        assertEquals(expected.getBoard(), to.getBoard());
    }

    @Test
    void test_food_persists() {
        HazardPredictor entity1 = new AsciiToGameState("" + //
                "yy_\n" + //
                "_Y_\n" + //
                "0__\n").setHealth("Y", 25).build();
        GameState from = entity1.getGameState();

        HazardPredictor entity2 = new AsciiToGameState("" + //
                "_y_\n" + //
                "_yY\n" + //
                "0__\n").setHealth("Y", 24).build();
        GameState expected = entity2.getGameState();

        GameState to = GameStateAdvancer.advance(from, moveRight);

        assertEquals(expected.getBoard(), to.getBoard());
    }

    @Test
    void test_snake_health_decrease() {
        HazardPredictor entity1 = EntityBuilder.hazardPredictor();
        GameState from = entity1.getGameState();
        GameState to = GameStateAdvancer.advance(from, moveRight);

        assertEquals(from.getYou().getHealth() - 1, to.getYou().getHealth());
    }

    @Test
    void test_snake_eat_food() {
        HazardPredictor entity1 = new AsciiToGameState("" + //
                "_yy\n" + //
                "_Y_\n" + //
                "_0_\n").setTurn(1).setHealth("Y", 42).build();
        GameState turn1 = entity1.getGameState();

        HazardPredictor entity2 = new AsciiToGameState("" + //
                "_y_\n" + //
                "_y_\n" + //
                "_Y_\n").setTurn(2).setHealth("Y", 100).build();
        GameState turn2 = entity2.getGameState();

        HazardPredictor entity3 = new AsciiToGameState("" + //
                "_y_\n" + //
                "_y_\n" + //
                "_yY\n").setTurn(3).setHealth("Y", 99).build();
        GameState turn3 = entity3.getGameState();

        GameState advance2 = GameStateAdvancer.advance(turn1, moveDown);

        GameState advance3fromAdvance2 = GameStateAdvancer.advance(advance2, moveRight);
        GameState advance3fromTurn2 = GameStateAdvancer.advance(turn2, moveRight);

        assertEquals(turn2, advance2);
        assertEquals(turn3, advance3fromAdvance2);
        assertEquals(turn3, advance3fromTurn2);
    }
}

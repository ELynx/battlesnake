package ru.elynx.battlesnake.engine.advancer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.asciitest.AsciiToGameState;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.MoveCommand;
import ru.elynx.battlesnake.entity.Snake;
import ru.elynx.battlesnake.testbuilder.CaseBuilder;
import ru.elynx.battlesnake.testbuilder.EntityBuilder;

@Tag("Internals")
class GameStateAdvancerTest {
    BiFunction<Snake, GameState, MoveCommand> moveDown = (Snake snake, GameState gameState) -> MoveCommand.DOWN;
    BiFunction<Snake, GameState, MoveCommand> moveLeft = (Snake snake, GameState gameState) -> MoveCommand.LEFT;
    BiFunction<Snake, GameState, MoveCommand> moveRight = (Snake snake, GameState gameState) -> MoveCommand.RIGHT;
    BiFunction<Snake, GameState, MoveCommand> moveUp = (Snake snake, GameState gameState) -> MoveCommand.UP;

    @Test
    void test_constants() {
        GameState from = EntityBuilder.gameState();
        GameState to = GameStateAdvancer.advance(moveRight, from);

        assertEquals(from.getGameId(), to.getGameId());
        assertEquals(from.getRules(), to.getRules());
    }

    @Test
    void test_turn_increment() {
        GameState from = EntityBuilder.gameState();
        GameState to = GameStateAdvancer.advance(moveRight, from);

        assertEquals(from.getTurn() + 1, to.getTurn());
    }

    @Test
    void test_consistency() {
        GameState from = EntityBuilder.gameState();
        GameState to = GameStateAdvancer.advance(moveRight, from);

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
        GameState from = new AsciiToGameState("" + //
                "yy_\n" + //
                "_Y_\n" + //
                "___\n").setHealth("Y", 25).build();

        GameState expected = new AsciiToGameState("" + //
                "_y_\n" + //
                "_yY\n" + //
                "___\n").setHealth("Y", 24).build();

        GameState to = GameStateAdvancer.advance(moveRight, from);

        assertEquals(expected.getBoard(), to.getBoard());
    }

    @Test
    void test_food_persists() {
        GameState from = new AsciiToGameState("" + //
                "yy_\n" + //
                "_Y_\n" + //
                "0__\n").setHealth("Y", 25).build();

        GameState expected = new AsciiToGameState("" + //
                "_y_\n" + //
                "_yY\n" + //
                "0__\n").setHealth("Y", 24).build();

        GameState to = GameStateAdvancer.advance(moveRight, from);

        assertEquals(expected.getBoard(), to.getBoard());
    }

    @Test
    void test_snake_health_decrease() {
        GameState from = EntityBuilder.gameState();
        GameState to = GameStateAdvancer.advance(moveRight, from);

        assertEquals(from.getYou().getHealth() - 1, to.getYou().getHealth());
    }

    @Test
    void test_snake_eat_food() {
        GameState turn1 = new AsciiToGameState("" + //
                "_yy\n" + //
                "_Y_\n" + //
                "_0_\n").setTurn(1).setHealth("Y", 42).build();

        GameState turn2 = new AsciiToGameState("" + //
                "_y_\n" + //
                "_y_\n" + //
                "_Y_\n").setTurn(2).setLength("Y", 4).setHealth("Y", Snake.getMaxHealth()).build();

        GameState turn3 = new AsciiToGameState("" + //
                "_y_\n" + //
                "_y_\n" + //
                "_yY\n").setTurn(3).setHealth("Y", Snake.getMaxHealth() - 1).build();

        GameState advance2 = GameStateAdvancer.advance(moveDown, turn1);

        GameState advance3fromAdvance2 = GameStateAdvancer.advance(moveRight, advance2);
        GameState advance3fromTurn2 = GameStateAdvancer.advance(moveRight, turn2);

        assertEquals(turn2, advance2);
        assertEquals(turn3, advance3fromAdvance2);
        assertEquals(turn3, advance3fromTurn2);
    }

    @Test
    void test_snake_eat_food_head_to_head() {
        GameState from = new AsciiToGameState("" + //
                "___\n" + //
                "_Y_\n" + //
                "A0_\n").setStartSnakeLength(1).build();

        GameState to = GameStateAdvancer.advance((Snake snake, GameState gameState) -> {
            if ("Y".equals(snake.getId())) {
                return moveDown.apply(snake, gameState);
            } else {
                return moveRight.apply(snake, gameState);
            }
        }, from);

        assertEquals(0, to.getBoard().getFood().size());
        assertEquals(0, to.getBoard().getSnakes().size());
    }

    @Test
    void test_snake_elimination_out_of_health() {
        GameState from = new AsciiToGameState("" + //
                "_y_\n" + //
                "_y_\n" + //
                "_Y_\n").setHealth("Y", 1).build();

        GameState to = GameStateAdvancer.advance(moveRight, from);

        assertEquals(0, to.getBoard().getSnakes().size());
    }

    @Test
    void test_snake_elimination_out_of_health_prevented_by_food() {
        GameState from = new AsciiToGameState("" + //
                "_y_\n" + //
                "_y_\n" + //
                "_Y0\n").setHealth("Y", 1).build();

        GameState to = GameStateAdvancer.advance(moveRight, from);

        assertEquals(1, to.getBoard().getSnakes().size());
        assertEquals(4, to.getBoard().getSnakes().get(0).getLength());
    }

    @Test
    void test_snake_elimination_moved_out_of_bounds() {
        GameState from = new AsciiToGameState("" + //
                "_y_\n" + //
                "_y_\n" + //
                "_Y_\n").setHealth("Y", Snake.getMaxHealth() - 1).build();

        GameState to = GameStateAdvancer.advance(moveDown, from);

        assertEquals(0, to.getBoard().getSnakes().size());
    }

    @Test
    void test_snake_elimination_collide_self() {
        GameState from = new AsciiToGameState("" + //
                "___\n" + //
                "_Y<\n" + //
                ">>^\n").setHealth("Y", Snake.getMaxHealth() - 1).build();

        GameState to = GameStateAdvancer.advance(moveDown, from);

        assertEquals(0, to.getBoard().getSnakes().size());
    }

    @Test
    void test_snake_elimination_collide_self_avoid_tail() {
        GameState from = new AsciiToGameState("" + //
                "___\n" + //
                "_v<\n" + //
                "_Y^\n").setHealth("Y", Snake.getMaxHealth() - 1).build();

        GameState to = GameStateAdvancer.advance(moveRight, from);

        assertEquals(1, to.getBoard().getSnakes().size());
    }

    @Test
    void test_snake_elimination_collide_self_avoid_initial_1() {
        GameState from = new AsciiToGameState("" + //
                "___\n" + //
                "Y__\n" + //
                "___\n").setLength("Y", 3).setHealth("Y", Snake.getMaxHealth() - 1).build();

        GameState to = GameStateAdvancer.advance(moveRight, from);

        assertEquals(1, to.getBoard().getSnakes().size());
    }

    @Test
    void test_snake_elimination_collide_self_avoid_initial_2() {
        GameState from = new AsciiToGameState("" + //
                "___\n" + //
                "yY_\n" + //
                "___\n").setLength("Y", 3).setHealth("Y", Snake.getMaxHealth() - 1).build();

        GameState to = GameStateAdvancer.advance(moveRight, from);

        assertEquals(1, to.getBoard().getSnakes().size());
    }

    @Test
    void test_snake_elimination_collide_self_avoid_after_full_health() {
        GameState from = new AsciiToGameState("" + //
                "___\n" + //
                "yyY\n" + //
                "___\n").setLength("Y", 4).setHealth("Y", Snake.getMaxHealth()).build();

        GameState to = GameStateAdvancer.advance(moveDown, from);

        assertEquals(1, to.getBoard().getSnakes().size());
    }

    @Test
    void test_snake_elimination_side() {
        GameState from = new AsciiToGameState("" + //
                "______y_\n" + //
                "___bbBy_\n" + //
                "___ccCy_\n" + //
                "___ddDy_\n" + //
                "___eeEy_\n" + //
                "_____AYa\n" + //
                "_____aaa\n").setStartSnakeLength(1).build();

        GameState to = GameStateAdvancer.advance((Snake snake, GameState gameState) -> {
            if ("Y".equals(snake.getId())) {
                return moveDown.apply(snake, gameState);
            } else {
                return moveRight.apply(snake, gameState);
            }
        }, from);

        assertEquals(0, to.getBoard().getSnakes().size());
    }

    @Test
    void test_snake_elimination_winner() {
        GameState from = new AsciiToGameState("" + //
                "______y_\n" + //
                "___bbBy_\n" + //
                "___ccCy_\n" + //
                "___ddDy_\n" + //
                "___eeEy_\n" + //
                "___ffFY_\n" + //
                "___aaA__\n").setStartSnakeLength(1).build();

        GameState to = GameStateAdvancer.advance((Snake snake, GameState gameState) -> {
            if ("Y".equals(snake.getId())) {
                return moveDown.apply(snake, gameState);
            } else {
                return moveRight.apply(snake, gameState);
            }
        }, from);

        assertEquals(1, to.getBoard().getSnakes().size());
        assertEquals("Y", to.getBoard().getSnakes().get(0).getId());
    }

    @Test
    void test_hazard_damage_non_fatal() {
        GameState from = new AsciiToGameState("" + //
                "_y_\n" + //
                "_y_\n" + //
                "_Y_\n").setHazards("__H\n__H\n__H\n").setHealth("Y", 40).build();

        GameState to = GameStateAdvancer.advance(moveRight, from);

        assertEquals(1, to.getBoard().getSnakes().size());
        assertEquals(3, to.getBoard().getSnakes().get(0).getLength());
        assertEquals(40 - 1 - from.getRules().getHazardDamage(), to.getBoard().getSnakes().get(0).getHealth());
    }

    @Test
    void test_hazard_damage_fatal() {
        GameState from = new AsciiToGameState("" + //
                "_y_\n" + //
                "_y_\n" + //
                "_Y_\n").setHazards("__H\n__H\n__H\n").setHealth("Y", 5).build();

        GameState to = GameStateAdvancer.advance(moveRight, from);

        assertEquals(0, to.getBoard().getSnakes().size());
    }

    @Test
    void test_food_and_hazard_order() {
        GameState from = new AsciiToGameState("" + //
                "_y_\n" + //
                "_y_\n" + //
                "_Y0\n").setHazards("__H\n__H\n__H\n").setHealth("Y", 1).build();

        GameState to = GameStateAdvancer.advance(moveRight, from);

        assertEquals(1, to.getBoard().getSnakes().size());
        assertEquals(4, to.getBoard().getSnakes().get(0).getLength());
        assertEquals(Snake.getMaxHealth() - from.getRules().getHazardDamage(),
                to.getBoard().getSnakes().get(0).getHealth());
    }

    @Test
    void test_elimination_and_hazard_order() {
        GameState from = new AsciiToGameState("" + //
                "______y_\n" + //
                "___bbBy_\n" + //
                "___ccCy_\n" + //
                "___ddDy_\n" + //
                "___eeEy_\n" + //
                "___ffFY_\n" + //
                "___aaA__\n").setHealth("Y", 5).setHazards("" + //
                        "________\n" + //
                        "________\n" + //
                        "________\n" + //
                        "________\n" + //
                        "________\n" + //
                        "________\n" + //
                        "HHHHHHHH\n").setStartSnakeLength(1).build();

        GameState to = GameStateAdvancer.advance((Snake snake, GameState gameState) -> {
            if ("Y".equals(snake.getId())) {
                return moveDown.apply(snake, gameState);
            } else {
                return moveRight.apply(snake, gameState);
            }
        }, from);

        assertEquals(0, to.getBoard().getSnakes().size());
    }

    @Test
    void test_active_hazards() {
        GameState from = CaseBuilder.can_handle_meta_information();

        GameState to = GameStateAdvancer.advance(moveDown, from);

        assertEquals(1, to.getBoard().getSnakes().size());
        assertEquals(from.getYou().getHealth() - 1, to.getYou().getHealth());
    }

    @Test
    void test_you_found_repeatedly() {
        GameState step0 = CaseBuilder.avoid_fruit_in_corner_easy_2_health();
        GameState step1 = GameStateAdvancer.advance(moveUp, step0);
        assertFalse(step1.isYouEliminated());
        GameState step2 = GameStateAdvancer.advance(moveLeft, step1);
        assertTrue(step2.isYouEliminated());

        step0 = CaseBuilder.avoid_fruit_in_corner_easy_10_health();
        step1 = GameStateAdvancer.advance(moveUp, step0);
        assertFalse(step1.isYouEliminated());
        step2 = GameStateAdvancer.advance(moveLeft, step1);
        assertFalse(step2.isYouEliminated());
    }
}

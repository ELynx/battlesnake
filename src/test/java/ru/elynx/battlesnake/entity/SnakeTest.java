package ru.elynx.battlesnake.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.asciitest.AsciiToGameState;
import ru.elynx.battlesnake.testbuilder.CaseBuilder;
import ru.elynx.battlesnake.testbuilder.EntityBuilder;

@Tag("Internals")
class SnakeTest {
    @Test
    void test_is_timed_out() {
        Snake tested1 = EntityBuilder.snakeWithLatency(100);
        assertFalse(tested1.isTimedOut());

        Snake tested2 = EntityBuilder.snakeWithLatency(0);
        assertTrue(tested2.isTimedOut());

        Snake testedInitialState = EntityBuilder.snakeWithLatency(null);
        assertFalse(testedInitialState.isTimedOut());
    }

    @Test
    void test_is_growing() {
        GameState gameState;

        gameState = new AsciiToGameState("yyY").setStartSnakeLength(3).setHealth("Y", 42).build();
        assertFalse(gameState.getYou().isGrowing(), "Not growing under normal conditions");

        gameState = new AsciiToGameState("yY_").setStartSnakeLength(3).setHealth("Y", 42).build();
        assertTrue(gameState.getYou().isGrowing(), "Growing when stepping on tail");

        gameState = new AsciiToGameState("yyY").setStartSnakeLength(3).setHealth("Y", Snake.getMaxHealth()).build();
        assertTrue(gameState.getYou().isGrowing(), "Full health grows");
    }

    @Test
    void test_max_health() {
        assertEquals(100, Snake.getMaxHealth());
    }

    @Test
    void test_get_neck_length_0() {
        Snake tested = new Snake("EmptyBody", "Empty Body Snake", 99, Collections.emptyList(), 100, Coordinates.ZERO, 0,
                "Shout", "Squad");

        assertThrows(IndexOutOfBoundsException.class, () -> {
            Coordinates impossible = tested.getNeck();
        });
    }

    @Test
    void test_get_neck_length_1() {
        Snake tested = new Snake("HeadBody", "Head Body Snake", 99, List.of(Coordinates.ZERO), 100, Coordinates.ZERO, 1,
                "Shout", "Squad");

        assertEquals(Coordinates.ZERO, tested.getNeck());
        assertEquals(tested.getHead(), tested.getNeck());
    }

    @Test
    void test_get_neck_length_greater_than_1() {
        Snake tested = new Snake("HeadBody", "Head Body Snake", 99,
                List.of(Coordinates.ZERO, Coordinates.ZERO.withX(1)), 100, Coordinates.ZERO, 2, "Shout", "Squad");

        assertEquals(Coordinates.ZERO.withX(1), tested.getNeck());
    }

    @Test
    void test_get_advancing_moves() {
        GameState gameState = CaseBuilder.dont_die_for_food_and_hunt();
        Collection<CoordinatesWithDirection> actual = gameState.getYou().getAdvancingMoves();
        Coordinates head = gameState.getYou().getHead();

        assertThat(actual, containsInAnyOrder(head.move(MoveCommand.LEFT), head.move(MoveCommand.DOWN),
                head.move(MoveCommand.RIGHT)));

        gameState = CaseBuilder.avoid_fruit_in_corner_easy();
        actual = gameState.getYou().getAdvancingMoves();
        head = gameState.getYou().getHead();

        assertThat(actual, containsInAnyOrder(head.move(MoveCommand.UP), head.move(MoveCommand.RIGHT),
                head.move(MoveCommand.DOWN)));
    }
}

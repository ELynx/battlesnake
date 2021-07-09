package ru.elynx.battlesnake.engine.predictor.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.elynx.battlesnake.entity.MoveCommand.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.engine.predictor.SimplePredictorInformant;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Snake;
import ru.elynx.battlesnake.testbuilder.CaseBuilder;

@Tag("Internals")
class MoveScoreMakerTest {
    @Test
    void test_empty_space_better_than_snake() {
        GameState gameState = CaseBuilder.empty_space_better_than_snake();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(-10, tested.scoreMove(head.move(LEFT)));
        assertEquals(1, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-10, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_avoid_fruit_surrounded_by_snake_2_hp() {
        GameState gameState = CaseBuilder.avoid_fruit_surrounded_by_snake_2_hp();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(-10, tested.scoreMove(head.move(LEFT)));
        assertEquals(1, tested.scoreMove(head.move(RIGHT)));
        assertEquals(4, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_avoid_fruit_surrounded_by_snake_10_hp() {
        GameState gameState = CaseBuilder.avoid_fruit_surrounded_by_snake_10_hp();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(-10, tested.scoreMove(head.move(LEFT)));
        assertEquals(1, tested.scoreMove(head.move(RIGHT)));
        assertEquals(4, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_avoid_fruit_in_corner_easy_2_health() {
        GameState gameState = CaseBuilder.avoid_fruit_in_corner_easy_2_health();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(4, tested.scoreMove(head.move(DOWN)));
        assertEquals(-10, tested.scoreMove(head.move(LEFT)));
        assertEquals(-10, tested.scoreMove(head.move(RIGHT)));
        assertEquals(1, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_avoid_fruit_in_corner_hard_2_health() {
        GameState gameState = CaseBuilder.avoid_fruit_in_corner_hard_2_health();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(4, tested.scoreMove(head.move(DOWN)));
        assertEquals(-10, tested.scoreMove(head.move(LEFT)));
        assertEquals(-10, tested.scoreMove(head.move(RIGHT)));
        assertEquals(1, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_avoid_fruit_in_corner_easy_10_health() {
        GameState gameState = CaseBuilder.avoid_fruit_in_corner_easy_10_health();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(4, tested.scoreMove(head.move(DOWN)));
        assertEquals(-10, tested.scoreMove(head.move(LEFT)));
        assertEquals(-10, tested.scoreMove(head.move(RIGHT)));
        assertEquals(1, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_avoid_fruit_in_corner_hard_10_health() {
        GameState gameState = CaseBuilder.avoid_fruit_in_corner_hard_10_health();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(4, tested.scoreMove(head.move(DOWN)));
        assertEquals(-10, tested.scoreMove(head.move(LEFT)));
        assertEquals(-10, tested.scoreMove(head.move(RIGHT)));
        assertEquals(1, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_dont_die_for_food() {
        GameState gameState = CaseBuilder.dont_die_for_food();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(1, tested.scoreMove(head.move(LEFT)));
        assertEquals(-10, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-10, tested.scoreMove(head.move(UP)));

        gameState = CaseBuilder.dont_die_for_food_flip();
        snake = gameState.getYou();
        head = snake.getHead();

        informant = new SimplePredictorInformant(gameState);

        tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(1, tested.scoreMove(head.move(UP)));
        assertEquals(1, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-10, tested.scoreMove(head.move(LEFT)));
        assertEquals(-10, tested.scoreMove(head.move(DOWN)));
    }

    @Test
    void test_dont_die_for_food_and_hunt() {
        GameState gameState = CaseBuilder.dont_die_for_food_and_hunt();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(-5, tested.scoreMove(head.move(DOWN)));
        assertEquals(-2, tested.scoreMove(head.move(LEFT)));
        assertEquals(-2, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-10, tested.scoreMove(head.move(UP)));

        gameState = CaseBuilder.dont_die_for_food_and_hunt_flip();
        snake = gameState.getYou();
        head = snake.getHead();

        informant = new SimplePredictorInformant(gameState);

        tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(-5, tested.scoreMove(head.move(UP)));
        assertEquals(-2, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-2, tested.scoreMove(head.move(LEFT)));
        assertEquals(-10, tested.scoreMove(head.move(DOWN)));
    }

    @Test
    void test_dont_give_up() {
        GameState gameState = CaseBuilder.dont_give_up();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(-10, tested.scoreMove(head.move(DOWN)));
        assertEquals(1, tested.scoreMove(head.move(LEFT)));
        assertEquals(-10, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-10, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_eat_in_hazard() {
        GameState gameState = CaseBuilder.eat_in_hazard();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(-10, tested.scoreMove(head.move(DOWN)));
        assertEquals(0, tested.scoreMove(head.move(LEFT)));
        assertEquals(-10, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-3, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_sees_the_inevitable() {
        GameState gameState = CaseBuilder.sees_the_inevitable();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(-10, tested.scoreMove(head.move(LEFT)));
        assertEquals(6, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-10, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_does_not_go_into_hazard_lake() {
        GameState gameState = CaseBuilder.does_not_go_into_hazard_lake();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(-10, tested.scoreMove(head.move(LEFT)));
        assertEquals(1, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-10, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_sees_escape_route() {
        GameState gameState = CaseBuilder.sees_escape_route();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(-10, tested.scoreMove(head.move(DOWN)));
        assertEquals(-10, tested.scoreMove(head.move(LEFT)));
        assertEquals(-10, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-4, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_sees_escape_route_plus() {
        GameState gameState = CaseBuilder.sees_escape_route_plus();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(-10, tested.scoreMove(head.move(DOWN)));
        assertEquals(-10, tested.scoreMove(head.move(LEFT)));
        assertEquals(-10, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-4, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_hazard_better_than_lose() {
        GameState gameState = CaseBuilder.hazard_better_than_lose();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(-10, tested.scoreMove(head.move(LEFT)));
        assertEquals(-4, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-10, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_does_not_corner_self() {
        GameState gameState = CaseBuilder.does_not_corner_self();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(-10, tested.scoreMove(head.move(DOWN)));
        assertEquals(-10, tested.scoreMove(head.move(LEFT)));
        assertEquals(6, tested.scoreMove(head.move(RIGHT)));
        assertEquals(6, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_avoid_lock_1() {
        GameState gameState = CaseBuilder.avoid_lock_1();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(-10, tested.scoreMove(head.move(LEFT)));
        assertEquals(-10, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-2, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_avoid_lock_2() {
        GameState gameState = CaseBuilder.avoid_lock_2();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(-10, tested.scoreMove(head.move(LEFT)));
        assertEquals(-10, tested.scoreMove(head.move(RIGHT)));
        assertEquals(1, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_can_handle_meta_information() {
        GameState gameState = CaseBuilder.can_handle_meta_information();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);

        MoveScoreMaker tested = new MoveScoreMaker(snake, gameState, informant);

        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(-3, tested.scoreMove(head.move(LEFT)));
        assertEquals(-10, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-3, tested.scoreMove(head.move(UP)));
    }
}

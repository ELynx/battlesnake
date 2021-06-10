package ru.elynx.battlesnake.engine.predictor.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static ru.elynx.battlesnake.entity.MoveCommand.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Snake;
import ru.elynx.battlesnake.testbuilder.CaseBuilder;

@Tag("Internals")
class ScoreMakerTest {
    @Test
    void test_empty_space_better_than_snake() {
        HazardPredictor entity1 = CaseBuilder.empty_space_better_than_snake();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        ScoreMaker tested = new ScoreMaker(snake, gameState);

        assertEquals(1, tested.scoreHead());
        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(6, tested.scoreMove(head.move(LEFT)));
        assertEquals(1, tested.scoreMove(head.move(RIGHT)));
        assertEquals(1, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_avoid_fruit_surrounded_by_snake() {
        HazardPredictor entity1 = CaseBuilder.avoid_fruit_surrounded_by_snake();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        ScoreMaker tested = new ScoreMaker(snake, gameState);

        assertEquals(1, tested.scoreHead());
        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(1, tested.scoreMove(head.move(LEFT)));
        assertEquals(1, tested.scoreMove(head.move(RIGHT)));
        assertEquals(4, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_avoid_fruit_in_corner_easy() {
        HazardPredictor entity1 = CaseBuilder.avoid_fruit_in_corner_easy();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        ScoreMaker tested = new ScoreMaker(snake, gameState);

        assertEquals(1, tested.scoreHead());
        assertEquals(4, tested.scoreMove(head.move(DOWN)));
        assertEquals(1, tested.scoreMove(head.move(LEFT)));
        assertEquals(1, tested.scoreMove(head.move(RIGHT)));
        assertEquals(1, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_avoid_fruit_in_corner_hard() {
        HazardPredictor entity1 = CaseBuilder.avoid_fruit_in_corner_hard();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        ScoreMaker tested = new ScoreMaker(snake, gameState);

        assertEquals(1, tested.scoreHead());
        assertEquals(4, tested.scoreMove(head.move(DOWN)));
        assertEquals(1, tested.scoreMove(head.move(LEFT)));
        assertEquals(1, tested.scoreMove(head.move(RIGHT)));
        assertEquals(1, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_dont_die_for_food() {
        HazardPredictor entity1 = CaseBuilder.dont_die_for_food();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        ScoreMaker tested = new ScoreMaker(snake, gameState);

        assertEquals(1, tested.scoreHead());
        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(1, tested.scoreMove(head.move(LEFT)));
        assertEquals(1, tested.scoreMove(head.move(RIGHT)));
        assertEquals(1, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_dont_die_for_food_and_hunt() {
        HazardPredictor entity1 = CaseBuilder.dont_die_for_food_and_hunt();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        ScoreMaker tested = new ScoreMaker(snake, gameState);

        assertEquals(1, tested.scoreHead());
        assertEquals(-5, tested.scoreMove(head.move(DOWN)));
        assertEquals(-2, tested.scoreMove(head.move(LEFT)));
        assertEquals(-2, tested.scoreMove(head.move(RIGHT)));
        assertEquals(1, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_dont_give_up() {
        HazardPredictor entity1 = CaseBuilder.dont_give_up();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        ScoreMaker tested = new ScoreMaker(snake, gameState);

        assertEquals(1, tested.scoreHead());
        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(1, tested.scoreMove(head.move(LEFT)));
        assertEquals(1, tested.scoreMove(head.move(RIGHT)));
        assertEquals(1, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_eat_in_hazard() {
        HazardPredictor entity1 = CaseBuilder.eat_in_hazard();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        ScoreMaker tested = new ScoreMaker(snake, gameState);

        assertEquals(-3, tested.scoreHead());
        assertEquals(-3, tested.scoreMove(head.move(DOWN)));
        assertEquals(0, tested.scoreMove(head.move(LEFT)));
        assertEquals(1, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-3, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_sees_the_inevitable() {
        HazardPredictor entity1 = CaseBuilder.sees_the_inevitable();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        ScoreMaker tested = new ScoreMaker(snake, gameState);

        assertEquals(1, tested.scoreHead());
        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(1, tested.scoreMove(head.move(LEFT)));
        assertEquals(6, tested.scoreMove(head.move(RIGHT)));
        assertEquals(6, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_does_not_go_into_hazard_lake() {
        HazardPredictor entity1 = CaseBuilder.does_not_go_into_hazard_lake();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        ScoreMaker tested = new ScoreMaker(snake, gameState);

        assertEquals(1, tested.scoreHead());
        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(1, tested.scoreMove(head.move(LEFT)));
        assertEquals(1, tested.scoreMove(head.move(RIGHT)));
        assertEquals(1, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_sees_escape_route() {
        HazardPredictor entity1 = CaseBuilder.sees_escape_route();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        ScoreMaker tested = new ScoreMaker(snake, gameState);

        assertEquals(1, tested.scoreHead());
        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(1, tested.scoreMove(head.move(LEFT)));
        assertEquals(1, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-4, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_sees_escape_route_plus() {
        HazardPredictor entity1 = CaseBuilder.sees_escape_route_plus();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        ScoreMaker tested = new ScoreMaker(snake, gameState);

        assertEquals(1, tested.scoreHead());
        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(1, tested.scoreMove(head.move(LEFT)));
        assertEquals(1, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-4, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_hazard_better_than_lose() {
        HazardPredictor entity1 = CaseBuilder.hazard_better_than_lose();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        ScoreMaker tested = new ScoreMaker(snake, gameState);

        assertEquals(1, tested.scoreHead());
        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(1, tested.scoreMove(head.move(LEFT)));
        assertEquals(-4, tested.scoreMove(head.move(RIGHT)));
        assertEquals(-4, tested.scoreMove(head.move(UP)));
    }

    @Test
    void test_does_not_corner_self() {
        HazardPredictor entity1 = CaseBuilder.does_not_corner_self();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();
        Coordinates head = snake.getHead();

        ScoreMaker tested = new ScoreMaker(snake, gameState);

        assertEquals(1, tested.scoreHead());
        assertEquals(1, tested.scoreMove(head.move(DOWN)));
        assertEquals(1, tested.scoreMove(head.move(LEFT)));
        assertEquals(6, tested.scoreMove(head.move(RIGHT)));
        assertEquals(6, tested.scoreMove(head.move(UP)));
    }
}

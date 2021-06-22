package ru.elynx.battlesnake.engine.predictor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Comparator;
import java.util.List;
import org.javatuples.Pair;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Snake;
import ru.elynx.battlesnake.testbuilder.CaseBuilder;

@Tag("Internals")
class SnakeMovePredictorTest {
    @Test
    void test_empty_space_better_than_snake() {
        HazardPredictor entity1 = CaseBuilder.empty_space_better_than_snake();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(7, 7), predictions.get(0).getValue0());
    }

    @Test
    void test_avoid_fruit_surrounded_by_snake() {
        HazardPredictor entity1 = CaseBuilder.avoid_fruit_surrounded_by_snake();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(1, 9), predictions.get(0).getValue0());
    }

    @Test
    void test_avoid_fruit_in_corner_easy() {
        HazardPredictor entity1 = CaseBuilder.avoid_fruit_in_corner_easy();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(10, 0), predictions.get(0).getValue0());
    }

    @Test
    void test_avoid_fruit_in_corner_hard() {
        HazardPredictor entity1 = CaseBuilder.avoid_fruit_in_corner_hard();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(10, 0), predictions.get(0).getValue0());
    }

    @Test
    void test_dont_die_for_food() {
        HazardPredictor entity1 = CaseBuilder.dont_die_for_food();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(4, 3), predictions.get(0).getValue0());
    }

    @Test
    void test_dont_die_for_food_and_hunt() {
        HazardPredictor entity1 = CaseBuilder.dont_die_for_food_and_hunt();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed()
                .thenComparingInt(pair -> pair.getValue0().getX()));

        // both with equal probability
        assertEquals(0.5d, predictions.get(0).getValue1());
        assertEquals(0.5d, predictions.get(1).getValue1());

        assertEquals(new Coordinates(3, 4), predictions.get(0).getValue0());
        assertEquals(new Coordinates(5, 4), predictions.get(1).getValue0());
    }

    @Test
    void test_dont_give_up() {
        HazardPredictor entity1 = CaseBuilder.dont_give_up();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(3, 3), predictions.get(0).getValue0());
    }

    @Test
    void test_eat_in_hazard() {
        HazardPredictor entity1 = CaseBuilder.eat_in_hazard();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(9, 4), predictions.get(0).getValue0());
    }

    @Test
    void test_sees_the_inevitable() {
        HazardPredictor entity1 = CaseBuilder.sees_the_inevitable();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(10, 3), predictions.get(0).getValue0());
    }

    @Test
    void test_does_not_go_into_hazard_lake() {
        HazardPredictor entity1 = CaseBuilder.does_not_go_into_hazard_lake();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(0, 2), predictions.get(0).getValue0());
    }

    @Test
    void test_sees_escape_route() {
        HazardPredictor entity1 = CaseBuilder.sees_escape_route();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(2, 3), predictions.get(0).getValue0());
    }

    @Test
    void test_sees_escape_route_plus() {
        HazardPredictor entity1 = CaseBuilder.sees_escape_route_plus();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(2, 3), predictions.get(0).getValue0());
    }

    @Test
    void test_hazard_better_than_lose() {
        HazardPredictor entity1 = CaseBuilder.hazard_better_than_lose();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(0, 3), predictions.get(0).getValue0());
    }

    @Test
    void test_does_not_corner_self() {
        HazardPredictor entity1 = CaseBuilder.does_not_corner_self();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(9, 0), predictions.get(0).getValue0());
    }

    @Test
    void test_avoid_lock_1() {
        HazardPredictor entity1 = CaseBuilder.avoid_lock_1();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(10, 0), predictions.get(0).getValue0());
    }

    @Test
    void test_avoid_lock_2() {
        HazardPredictor entity1 = CaseBuilder.avoid_lock_2();
        GameState gameState = entity1.getGameState();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(0, 2), predictions.get(0).getValue0());
    }
}

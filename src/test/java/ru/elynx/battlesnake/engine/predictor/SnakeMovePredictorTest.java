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
        GameState gameState = CaseBuilder.empty_space_better_than_snake();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(7, 7), predictions.get(0).getValue0());
    }

    @Test
    void test_avoid_fruit_surrounded_by_snake_2_hp() {
        GameState gameState = CaseBuilder.avoid_fruit_surrounded_by_snake_2_hp();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(1, 9), predictions.get(0).getValue0());
    }

    @Test
    void test_avoid_fruit_surrounded_by_snake_10_hp() {
        GameState gameState = CaseBuilder.avoid_fruit_surrounded_by_snake_10_hp();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(1, 9), predictions.get(0).getValue0());
    }

    @Test
    void test_avoid_fruit_in_corner_easy_2_health() {
        GameState gameState = CaseBuilder.avoid_fruit_in_corner_easy_2_health();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(10, 0), predictions.get(0).getValue0());
    }

    @Test
    void test_avoid_fruit_in_corner_hard_2_health() {
        GameState gameState = CaseBuilder.avoid_fruit_in_corner_hard_2_health();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(10, 0), predictions.get(0).getValue0());
    }

    @Test
    void test_avoid_fruit_in_corner_easy_10_health() {
        GameState gameState = CaseBuilder.avoid_fruit_in_corner_easy_10_health();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(10, 0), predictions.get(0).getValue0());
    }

    @Test
    void test_avoid_fruit_in_corner_hard_10_health() {
        GameState gameState = CaseBuilder.avoid_fruit_in_corner_hard_10_health();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(10, 0), predictions.get(0).getValue0());
    }

    @Test
    void test_dont_die_for_food() {
        GameState gameState = CaseBuilder.dont_die_for_food();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(4, 3), predictions.get(0).getValue0());

        gameState = CaseBuilder.dont_die_for_food_flip();
        snake = gameState.getYou();

        informant = new SimplePredictorInformant(gameState);
        tested = new SnakeMovePredictor(informant);
        predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(1, 2), predictions.get(0).getValue0());
    }

    @Test
    void test_dont_die_for_food_and_hunt() {
        GameState gameState = CaseBuilder.dont_die_for_food_and_hunt();
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

        gameState = CaseBuilder.dont_die_for_food_and_hunt_flip();
        snake = gameState.getYou();

        informant = new SimplePredictorInformant(gameState);
        tested = new SnakeMovePredictor(informant);
        predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed()
                .thenComparingInt(pair -> pair.getValue0().getX()));

        // both with equal probability
        assertEquals(0.5d, predictions.get(0).getValue1());
        assertEquals(0.5d, predictions.get(1).getValue1());

        assertEquals(new Coordinates(3, 2), predictions.get(0).getValue0());
        assertEquals(new Coordinates(5, 2), predictions.get(1).getValue0());
    }

    @Test
    void test_dont_give_up() {
        GameState gameState = CaseBuilder.dont_give_up();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(3, 3), predictions.get(0).getValue0());
    }

    @Test
    void test_eat_in_hazard() {
        GameState gameState = CaseBuilder.eat_in_hazard();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(9, 4), predictions.get(0).getValue0());
    }

    @Test
    void test_sees_the_inevitable() {
        GameState gameState = CaseBuilder.sees_the_inevitable();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(10, 3), predictions.get(0).getValue0());
    }

    @Test
    void test_does_not_go_into_hazard_lake() {
        GameState gameState = CaseBuilder.does_not_go_into_hazard_lake();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(0, 2), predictions.get(0).getValue0());
    }

    @Test
    void test_sees_escape_route() {
        GameState gameState = CaseBuilder.sees_escape_route();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(2, 3), predictions.get(0).getValue0());
    }

    @Test
    void test_sees_escape_route_plus() {
        GameState gameState = CaseBuilder.sees_escape_route_plus();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(2, 3), predictions.get(0).getValue0());
    }

    @Test
    void test_hazard_better_than_lose() {
        GameState gameState = CaseBuilder.hazard_better_than_lose();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(0, 3), predictions.get(0).getValue0());
    }

    @Test
    void test_does_not_corner_self() {
        GameState gameState = CaseBuilder.does_not_corner_self();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(9, 0), predictions.get(0).getValue0());
    }

    @Test
    void test_avoid_lock_1() {
        GameState gameState = CaseBuilder.avoid_lock_1();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(10, 0), predictions.get(0).getValue0());
    }

    @Test
    void test_avoid_lock_2() {
        GameState gameState = CaseBuilder.avoid_lock_2();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(0, 2), predictions.get(0).getValue0());
    }

    @Test
    void test_can_handle_meta_information() {
        GameState gameState = CaseBuilder.can_handle_meta_information();
        Snake snake = gameState.getYou();

        SimplePredictorInformant informant = new SimplePredictorInformant(gameState);
        SnakeMovePredictor tested = new SnakeMovePredictor(informant);
        List<Pair<Coordinates, Double>> predictions = tested.predict(snake, gameState);
        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        assertEquals(new Coordinates(5, 8), predictions.get(0).getValue0());
    }
}

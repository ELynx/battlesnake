package ru.elynx.battlesnake.engine.strategy;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static ru.elynx.battlesnake.engine.strategy.GameStrategyFactoryTest.STRATEGY_NAMES;
import static ru.elynx.battlesnake.engine.strategy.MoveAssert.assertMove;
import static ru.elynx.battlesnake.entity.MoveCommand.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.entity.Move;
import ru.elynx.battlesnake.testbuilder.CaseBuilder;

@SpringBootTest
@Tag("StrategyCase")
class GameStrategyCaseTest {
    @Autowired
    IGameStrategyFactory gameStrategyFactory;

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_empty_space_better_than_snake(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        HazardPredictor gameState = CaseBuilder.empty_space_better_than_snake();
        gameStrategy.init(gameState);

        Move move = gameStrategy.processMove(gameState);
        assertMove(move.getMoveCommand(), equalTo(DOWN)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_avoid_fruit_surrounded_by_snake(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        HazardPredictor gameState = CaseBuilder.avoid_fruit_surrounded_by_snake();
        gameStrategy.init(gameState);

        Move move = gameStrategy.processMove(gameState);
        assertMove(move.getMoveCommand(), not(equalTo(UP))).failing("Pixel").failing("Voxel").validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_avoid_fruit_in_corner_easy(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        HazardPredictor gameState = CaseBuilder.avoid_fruit_in_corner_easy();
        gameStrategy.init(gameState);

        Move move = gameStrategy.processMove(gameState);
        assertMove(move.getMoveCommand(), equalTo(UP)).failing("Pixel").failing("Voxel").validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_avoid_fruit_in_corner_hard(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        HazardPredictor gameState = CaseBuilder.avoid_fruit_in_corner_hard();
        gameStrategy.init(gameState);

        Move move = gameStrategy.processMove(gameState);
        assertMove(move.getMoveCommand(), equalTo(UP)).failing("Ahaetulla").failing("Pixel").failing("Voxel")
                .validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_dont_die_for_food(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        HazardPredictor gameState = CaseBuilder.dont_die_for_food();
        gameStrategy.init(gameState);

        Move move = gameStrategy.processMove(gameState);
        assertMove(move.getMoveCommand(), not(equalTo(DOWN))).failing("Pixel").failing("Voxel").validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_dont_die_for_food_and_hunt(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        HazardPredictor gameState = CaseBuilder.dont_die_for_food_and_hunt();
        gameStrategy.init(gameState);

        Move move = gameStrategy.processMove(gameState);
        assertMove(move.getMoveCommand(), not(equalTo(DOWN))).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_dont_give_up(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        HazardPredictor gameState = CaseBuilder.dont_give_up();
        gameStrategy.init(gameState);

        Move move = gameStrategy.processMove(gameState);
        assertMove(move.getMoveCommand(), equalTo(LEFT)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_eat_in_hazard(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        HazardPredictor gameState = CaseBuilder.eat_in_hazard();
        gameStrategy.init(gameState);

        Move move = gameStrategy.processMove(gameState);
        assertMove(move.getMoveCommand(), equalTo(LEFT)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_sees_the_inevitable(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        HazardPredictor gameState = CaseBuilder.sees_the_inevitable();
        gameStrategy.init(gameState);

        Move move = gameStrategy.processMove(gameState);
        assertMove(move.getMoveCommand(), equalTo(RIGHT)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_does_not_go_into_hazard_lake(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        HazardPredictor gameState = CaseBuilder.does_not_go_into_hazard_lake();
        gameStrategy.init(gameState);

        Move move = gameStrategy.processMove(gameState);
        assertMove(move.getMoveCommand(), equalTo(RIGHT)).failing("Ahaetulla").failing("Pixel").failing("Voxel")
                .validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_sees_escape_route(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        HazardPredictor gameState = CaseBuilder.sees_escape_route();
        gameStrategy.init(gameState);

        Move move = gameStrategy.processMove(gameState);
        assertMove(move.getMoveCommand(), equalTo(DOWN)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_sees_escape_route_plus(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        HazardPredictor gameState = CaseBuilder.sees_escape_route_plus();
        gameStrategy.init(gameState);

        Move move = gameStrategy.processMove(gameState);
        assertMove(move.getMoveCommand(), equalTo(DOWN)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_hazard_better_than_lose(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        HazardPredictor gameState = CaseBuilder.hazard_better_than_lose();
        gameStrategy.init(gameState);

        Move move = gameStrategy.processMove(gameState);
        assertMove(move.getMoveCommand(), equalTo(DOWN)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_does_not_corner_self(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        HazardPredictor gameState = CaseBuilder.does_not_corner_self();
        gameStrategy.init(gameState);

        Move move = gameStrategy.processMove(gameState);
        assertMove(move.getMoveCommand(), equalTo(UP)).failing("Pixel").failing("Voxel").validate(name);
    }
}

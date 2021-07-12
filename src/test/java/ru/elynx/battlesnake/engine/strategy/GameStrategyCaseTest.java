package ru.elynx.battlesnake.engine.strategy;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static ru.elynx.battlesnake.engine.strategy.GameStrategyFactoryTest.STRATEGY_NAMES;
import static ru.elynx.battlesnake.engine.strategy.MoveAssert.assertMove;
import static ru.elynx.battlesnake.entity.MoveCommand.*;

import java.util.Optional;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.engine.advancer.GameStateAdvancer;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.MoveCommand;
import ru.elynx.battlesnake.entity.Snake;
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

        GameState gameState = CaseBuilder.empty_space_better_than_snake();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), equalTo(DOWN)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_avoid_fruit_surrounded_by_snake(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState = CaseBuilder.avoid_fruit_surrounded_by_snake_2_hp();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), not(equalTo(UP))).failing("Pixel").different("Voxel").validate(name);

        gameState = CaseBuilder.avoid_fruit_surrounded_by_snake_10_hp();
        gameStrategy.init(gameState);

        move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), not(equalTo(UP))).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_avoid_fruit_in_corner_easy(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState = CaseBuilder.avoid_fruit_in_corner_easy_2_health();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), equalTo(UP)).failing("Pixel").different("Voxel").validate(name);

        gameState = CaseBuilder.avoid_fruit_in_corner_easy_10_health();
        gameStrategy.init(gameState);

        move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), equalTo(UP)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_avoid_fruit_in_corner_hard(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState = CaseBuilder.avoid_fruit_in_corner_hard_2_health();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), equalTo(UP)).failing("Pixel").different("Voxel").validate(name);

        gameState = CaseBuilder.avoid_fruit_in_corner_hard_10_health();
        gameStrategy.init(gameState);

        move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), equalTo(UP)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_dont_die_for_food(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState = CaseBuilder.dont_die_for_food();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), not(equalTo(DOWN))).failing("Pixel").different("Voxel").validate(name);

        // special case - use flipped version of board to avoid "deterministic
        // adversary" effect

        gameState = CaseBuilder.dont_die_for_food_flip();
        gameStrategy.init(gameState);

        move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), not(equalTo(DOWN))).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_dont_die_for_food_and_hunt(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState = CaseBuilder.dont_die_for_food_and_hunt();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), not(equalTo(DOWN))).different("Voxel").validate(name);

        // special case - use flipped version of board to avoid "deterministic
        // adversary" effect

        gameState = CaseBuilder.dont_die_for_food_flip();
        gameStrategy.init(gameState);

        move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), not(equalTo(DOWN))).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_dont_give_up(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState = CaseBuilder.dont_give_up();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), equalTo(LEFT)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_eat_in_hazard(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState = CaseBuilder.eat_in_hazard();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), equalTo(LEFT)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_sees_the_inevitable(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState = CaseBuilder.sees_the_inevitable();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), equalTo(RIGHT)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_does_not_go_into_hazard_lake(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState = CaseBuilder.does_not_go_into_hazard_lake();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), equalTo(RIGHT)).failing("Pixel").validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_sees_escape_route(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState = CaseBuilder.sees_escape_route();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), equalTo(DOWN)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_sees_escape_route_plus(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState = CaseBuilder.sees_escape_route_plus();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), equalTo(DOWN)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_hazard_better_than_lose(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState = CaseBuilder.hazard_better_than_lose();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), equalTo(DOWN)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_does_not_corner_self(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState = CaseBuilder.does_not_corner_self();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), equalTo(UP)).failing("Pixel").validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_avoid_lock_1(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState = CaseBuilder.avoid_lock_1();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), equalTo(DOWN)).failing("Ahaetulla").validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_avoid_lock_2(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState = CaseBuilder.avoid_lock_2();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), equalTo(DOWN)).failing("Ahaetulla").failing("Voxel").validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_eat_food_immediately(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState = CaseBuilder.eat_food_immediately();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), equalTo(LEFT)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_eat_food_and_conquer_in_two_turns(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState0 = CaseBuilder.eat_food_and_conquer_in_two_turns();
        gameStrategy.init(gameState0);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState0);
        assertMove(move.orElseThrow(), equalTo(UP)).failing("Pixel").validate(name);

        BiFunction<Snake, GameState, MoveCommand> whoDidWhat = (Snake snake, GameState gameState) -> {
            switch (snake.getId()) {
                case "S" :
                    return LEFT;
                case "R" :
                case "K" :
                    return RIGHT;
                case "Y" :
                    return UP;
                default :
                    throw new IllegalArgumentException("Snake [" + snake.getId() + "] did not participate");
            }
        };

        GameState gameState1 = GameStateAdvancer.advance(gameState0, whoDidWhat);

        move = gameStrategy.processMove(gameState1);
        assertMove(move.orElseThrow(), equalTo(UP)).validate(name);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_attempt_on_enemy_life(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        GameState gameState = CaseBuilder.attempt_on_enemy_life();
        gameStrategy.init(gameState);

        Optional<MoveCommand> move = gameStrategy.processMove(gameState);
        assertMove(move.orElseThrow(), not(equalTo(RIGHT))).failing("Ahaetulla").validate(name);
    }
}

package ru.elynx.battlesnake.engine.strategy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static ru.elynx.battlesnake.engine.strategy.GameStrategyFactoryTest.STRATEGY_NAMES;
import static ru.elynx.battlesnake.entity.MoveCommand.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.asciitest.AsciiToGameState;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;
import ru.elynx.battlesnake.entity.Dimensions;
import ru.elynx.battlesnake.entity.Move;
import ru.elynx.battlesnake.entity.MoveCommand;
import ru.elynx.battlesnake.testbuilder.EntityBuilder;

@SpringBootTest
@Tag("StrategyBasic")
class GameStrategyBasicTest {
    @Autowired
    IGameStrategyFactory gameStrategyFactory;

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_strategy_produce_BattlesnakeInfo(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        BattlesnakeInfo battlesnakeInfo = gameStrategy.getBattesnakeInfo();
        assertNotNull(battlesnakeInfo);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_strategy_does_not_throw_on_init(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        assertDoesNotThrow(() -> gameStrategy.init(EntityBuilder.hazardPredictor()));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_strategy_does_not_throw_on_start(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        gameStrategy.init(EntityBuilder.hazardPredictor());
        assertDoesNotThrow(() -> gameStrategy.processStart(EntityBuilder.hazardPredictor()));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_strategy_produce_Move(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        gameStrategy.init(EntityBuilder.hazardPredictor());
        Move move = gameStrategy.processMove(EntityBuilder.hazardPredictor());
        assertNotNull(move);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_strategy_does_not_throw_on_end(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        gameStrategy.init(EntityBuilder.hazardPredictor());
        assertDoesNotThrow(() -> gameStrategy.processEnd(EntityBuilder.hazardPredictor()));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_strategy_does_not_move_into_wall(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        HazardPredictor entity1 = EntityBuilder.hazardPredictor();
        gameStrategy.init(entity1);

        Dimensions dimensions = entity1.getGameState().getBoard().getDimensions();
        int w = dimensions.getWidth();
        int h = dimensions.getHeight();

        for (int x = 0; x < w; ++x) {
            Move move1 = gameStrategy.processMove(EntityBuilder.hazardPredictorWithHeadPosition(x, 0));
            assertNotEquals(DOWN, move1.getMoveCommand());

            Move move2 = gameStrategy.processMove(EntityBuilder.hazardPredictorWithHeadPosition(x, h - 1));
            assertNotEquals(UP, move2.getMoveCommand());
        }

        for (int y = 0; y < h; ++y) {
            Move move1 = gameStrategy.processMove(EntityBuilder.hazardPredictorWithHeadPosition(0, y));
            assertNotEquals(LEFT, move1.getMoveCommand());

            Move move2 = gameStrategy.processMove(EntityBuilder.hazardPredictorWithHeadPosition(w - 1, y));
            assertNotEquals(RIGHT, move2.getMoveCommand());
        }
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_circling_novice(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        String[] circles = {"Y_\n__", "_Y\n__", "__\nY_", "__\n_Y"};
        MoveCommand[] notTo = {LEFT, UP, UP, RIGHT, DOWN, LEFT, RIGHT, DOWN};
        assertThat(notTo.length, is(circles.length * 2));

        for (int i = 0; i < circles.length; ++i) {
            int j = i * 2;
            int k = j + 1;

            AsciiToGameState generator = new AsciiToGameState(circles[i]);

            HazardPredictor entity1 = generator.build();
            gameStrategy.init(entity1);
            Move move = gameStrategy.processMove(entity1);
            assertThat("Step " + i, move.getMoveCommand(), not(equalTo(notTo[j])));
            assertThat("Step " + i, move.getMoveCommand(), not(equalTo(notTo[k])));
        }
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_circling_easy(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        String[] circles = {"Yy\n__", "_Y\n_y", "__\nyY", "y_\nY_", "Y_\ny_", "yY\n__", "_y\n_Y", "__\nYy"};
        MoveCommand[] to = {DOWN, LEFT, UP, RIGHT, RIGHT, DOWN, LEFT, UP};
        assertThat(to.length, is(circles.length));

        for (int i = 0; i < circles.length; ++i) {
            AsciiToGameState generator = new AsciiToGameState(circles[i]);

            HazardPredictor entity1 = generator.build();
            gameStrategy.init(entity1);
            Move move = gameStrategy.processMove(entity1);
            assertThat("Step " + i, move.getMoveCommand(), equalTo(to[i]));
        }
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_circling_medium(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        String[] circles = {"Yy\n_y", "_Y\nyy", "y_\nyY", "yy\nY_", "Y_\nyy", "yY\ny_", "yy\n_Y", "_y\nYy"};
        MoveCommand[] to = {DOWN, LEFT, UP, RIGHT, RIGHT, DOWN, LEFT, UP};
        assertThat(to.length, is(circles.length));

        for (int i = 0; i < circles.length; ++i) {
            AsciiToGameState generator = new AsciiToGameState(circles[i]);

            HazardPredictor entity1 = generator.build();
            gameStrategy.init(entity1);
            Move move = gameStrategy.processMove(entity1);
            assertThat("Step " + i, move.getMoveCommand(), equalTo(to[i]));
        }
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_circling_hard(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        String[] circles = {"Y<\n>^", "vY\n>^", "v<\n>Y", "v<\nY^", "Yv\n^<", ">Y\n^<", ">v\n^Y", ">v\nY<"};
        MoveCommand[] to = {DOWN, LEFT, UP, RIGHT, RIGHT, DOWN, LEFT, UP};
        assertThat(to.length, is(circles.length));

        for (int i = 0; i < circles.length; ++i) {
            AsciiToGameState generator = new AsciiToGameState(circles[i]);

            HazardPredictor entity1 = generator.build();
            gameStrategy.init(entity1);
            Move move = gameStrategy.processMove(entity1);
            assertThat("Step " + i, move.getMoveCommand(), equalTo(to[i]));
        }
    }
}

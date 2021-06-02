package ru.elynx.battlesnake.engine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static ru.elynx.battlesnake.entity.MoveCommand.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.asciitest.AsciiToGameState;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.entity.*;

@SpringBootTest
@Tag("StrategyBasic")
class GameStrategyBasicTest {
    public static final String STRATEGY_NAMES = "ru.elynx.battlesnake.engine.GameStrategyBasicTest#provideStrategyNames";

    private static HazardPredictor withHead(int x, int y) {
        Coordinates head = new Coordinates(x, y);

        List<Coordinates> body = new ArrayList<>();
        body.add(head);

        List<Snake> snakes = new ArrayList<>();
        snakes.add(new Snake("TestYou-id", "TestYou-name", 99, body, 250, head, 1, "TestYou-shout", ""));

        String gameId = "test-case";
        int turn = 0;
        Rules rules = new Rules("standard", "1.234", 500);

        Dimensions dimensions = new Dimensions(15, 11);
        List<Coordinates> food = Collections.emptyList();
        List<Coordinates> hazards = Collections.emptyList();
        Board board = new Board(dimensions, food, hazards, snakes);

        GameState gameState = new GameState(gameId, turn, rules, board, snakes.get(0));
        return new HazardPredictor(gameState, 0);
    }

    HazardPredictor withAllDefaults() {
        return withHead(0, 0);
    }

    @Autowired
    IGameStrategyFactory gameStrategyFactory;

    public static Stream<String> provideStrategyNames() {
        return Stream.of("Ahaetulla", "Pixel", "Voxel");
    }

    @Test
    void test_factory_is_autowired() {
        assertNotNull(gameStrategyFactory);
    }

    @Test
    void test_factory_has_strategies() {
        assertTrue(gameStrategyFactory.getRegisteredStrategies().size() > 0);
    }

    @Test
    void test_factory_throws_on_not_known_name() {
        assertThrows(SnakeNotFoundException.class, () -> gameStrategyFactory.getGameStrategy(null));
        assertThrows(SnakeNotFoundException.class, () -> gameStrategyFactory.getGameStrategy("Foo"));
    }

    @Test
    void test_all_combatant_strategies_are_tested() {
        Stream<String> testedStrategies = provideStrategyNames();
        Set<String> knownStrategies = gameStrategyFactory.getRegisteredStrategies();

        Set<String> temp1 = testedStrategies.sorted().collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> temp2 = knownStrategies.stream().filter(name -> {
            IGameStrategy strategy = gameStrategyFactory.getGameStrategy(name);
            return strategy.isCombatant();
        }).sorted().collect(Collectors.toCollection(LinkedHashSet::new));

        assertIterableEquals(temp1, temp2);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_factory_produces_strategy(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        assertNotNull(gameStrategy);
    }

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
        assertDoesNotThrow(() -> gameStrategy.init(withAllDefaults()));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_strategy_does_not_throw_on_start(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        gameStrategy.init(withAllDefaults());
        assertDoesNotThrow(() -> gameStrategy.processStart(withAllDefaults()));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_strategy_produce_Move(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        gameStrategy.init(withAllDefaults());
        Move move = gameStrategy.processMove(withAllDefaults());
        assertNotNull(move);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_strategy_does_not_throw_on_end(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        gameStrategy.init(withAllDefaults());
        assertDoesNotThrow(() -> gameStrategy.processEnd(withAllDefaults()));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void gameStrategyDoesNotGoIntoWall(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        gameStrategy.init(withAllDefaults());

        int w = withAllDefaults().getGameState().getBoard().getDimensions().getWidth();
        int h = withAllDefaults().getGameState().getBoard().getDimensions().getHeight();

        for (int x = 0; x < w; ++x) {
            Move move1 = gameStrategy.processMove(withHead(x, 0));
            assertNotEquals(DOWN, move1.getMoveCommand());

            Move move2 = gameStrategy.processMove(withHead(x, h - 1));
            assertNotEquals(UP, move2.getMoveCommand());
        }

        for (int y = 0; y < h; ++y) {
            Move move1 = gameStrategy.processMove(withHead(0, y));
            assertNotEquals(RIGHT, move1.getMoveCommand());

            Move move2 = gameStrategy.processMove(withHead(w - 1, y));
            assertNotEquals(LEFT, move2.getMoveCommand());
        }
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_circling_novice(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        System.out.println("Testing " + name);

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

        System.out.println("Testing " + name);

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

        System.out.println("Testing " + name);

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

        System.out.println("Testing " + name);

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

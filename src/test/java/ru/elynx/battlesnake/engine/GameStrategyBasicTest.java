package ru.elynx.battlesnake.engine;

import static org.junit.jupiter.api.Assertions.*;
import static ru.elynx.battlesnake.protocol.Move.Moves.*;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.protocol.*;
import ru.elynx.battlesnake.testspecific.ToApiVersion;
import ru.elynx.battlesnake.testspecific.TestMove;
import ru.elynx.battlesnake.testspecific.TestSnakeDto;

@SpringBootTest
class GameStrategyBasicTest {
    public static final String STRATEGY_NAMES = "ru.elynx.battlesnake.engine.GameStrategyBasicTest#provideStrategyNames";

    static GameStateDto dummyGameState;

    @Autowired
    IGameStrategyFactory gameStrategyFactory;

    @BeforeAll
    static void fillDummies() {
        dummyGameState = new GameStateDto();

        dummyGameState.setGame(new GameDto());
        dummyGameState.getGame().setId(GameStrategyBasicTest.class.getSimpleName());

        dummyGameState.setBoard(new BoardDto());
        dummyGameState.getBoard().setHeight(11);
        dummyGameState.getBoard().setWidth(15);
    }

    public static Stream<String> provideStrategyNames() {
        return Stream.of("Snake_1", "Snake_1a", "ChesssMassster");
    }

    @BeforeEach
    void resetDummies() {
        dummyGameState.setTurn(0);

        dummyGameState.getBoard().setFood(new LinkedList<>());
        dummyGameState.getBoard().setHazards(new LinkedList<>()); // v1
        dummyGameState.getBoard().setSnakes(new LinkedList<>());

        dummyGameState.setYou(new TestSnakeDto(ToApiVersion.V0)); // v0 for lazy init
        dummyGameState.getYou().setId("TestYou-id");
        dummyGameState.getYou().setName("TestYou-name");
        dummyGameState.getYou().setHealth(100);
        dummyGameState.getYou().setBody(new LinkedList<>());
        dummyGameState.getYou().getBody().add(new CoordsDto(0, 0));
        dummyGameState.getYou().setShout("TestYou-shout");
        // v1
        dummyGameState.getYou().setLatency(250);
        dummyGameState.getYou().setSquad("");

        dummyGameState.getBoard().getSnakes().add(dummyGameState.getYou());
    }

    @Test
    void factoryAutowired() {
        assertNotNull(gameStrategyFactory);
    }

    @Test
    void factoryHasStrategies() {
        assertTrue(gameStrategyFactory.getRegisteredStrategies().size() > 0);
    }

    @Test
    void factoryGetGameStrategyThrowsOnInvalidName() {
        assertThrows(SnakeNotFoundException.class, () -> gameStrategyFactory.getGameStrategy(null));
        assertThrows(SnakeNotFoundException.class, () -> gameStrategyFactory.getGameStrategy("Foo"));
    }

    @Test
    void allStrategiesAreTested() {
        Stream<String> testedStrategies = provideStrategyNames();
        Set<String> knownStrategies = gameStrategyFactory.getRegisteredStrategies();

        Set<String> temp1 = testedStrategies.sorted().collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> temp2 = knownStrategies.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new));

        assertIterableEquals(temp1, temp2);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void factoryGetGameStrategy(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        assertNotNull(gameStrategy);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void gameStrategyGivesInfo(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        BattlesnakeInfo battlesnakeInfo = gameStrategy.getBattesnakeInfo();
        assertNotNull(battlesnakeInfo);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void gameStrategyDoesNotThrowOnStart(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        assertDoesNotThrow(() -> gameStrategy.processStart(dummyGameState));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void gameStrategyGivesMove(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        gameStrategy.processStart(dummyGameState);
        Move move = gameStrategy.processMove(dummyGameState);
        assertNotNull(move);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void gameStrategyDoesNotThrowOnEnd(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        gameStrategy.processStart(dummyGameState);
        assertDoesNotThrow(() -> gameStrategy.processEnd(dummyGameState));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void gameStrategyDoesNotGoIntoWall(String name) {
        System.out.println(String.format("Wall test for snake %s", name));

        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        if (gameStrategy.isPuzzleOnly()) {
            System.out.println("Puzzler, skipped");
            return;
        }

        dummyGameState.getYou().getHead().setX(0);
        dummyGameState.getYou().getHead().setY(0);

        gameStrategy.processStart(dummyGameState);

        for (int x = 0; x < dummyGameState.getBoard().getWidth(); ++x) {
            dummyGameState.getYou().getHead().setX(x);
            System.out.print(dummyGameState.getYou().getHead());

            TestMove move = new TestMove(gameStrategy.processMove(dummyGameState), ToApiVersion.V1);
            System.out.println(" -> " + move);

            assertFalse(DOWN.equalsIgnoreCase(move.getMove()));
        }

        for (int y = 0; y < dummyGameState.getBoard().getHeight(); ++y) {
            dummyGameState.getYou().getHead().setY(y);
            System.out.print(dummyGameState.getYou().getHead());

            TestMove move = new TestMove(gameStrategy.processMove(dummyGameState), ToApiVersion.V1);
            System.out.println(" -> " + move);

            assertFalse(RIGHT.equalsIgnoreCase(move.getMove()));
        }

        for (int x = dummyGameState.getBoard().getWidth() - 1; x >= 0; --x) {
            dummyGameState.getYou().getHead().setX(x);
            System.out.print(dummyGameState.getYou().getHead());

            TestMove move = new TestMove(gameStrategy.processMove(dummyGameState), ToApiVersion.V1);
            System.out.println(" -> " + move);

            assertFalse(UP.equalsIgnoreCase(move.getMove()));
        }

        for (int y = dummyGameState.getBoard().getHeight() - 1; y >= 0; --y) {
            dummyGameState.getYou().getHead().setY(y);
            System.out.print(dummyGameState.getYou().getHead());

            TestMove move = new TestMove(gameStrategy.processMove(dummyGameState), ToApiVersion.V1);
            System.out.println(" -> " + move);

            assertFalse(LEFT.equalsIgnoreCase(move.getMove()));
        }
    }
}

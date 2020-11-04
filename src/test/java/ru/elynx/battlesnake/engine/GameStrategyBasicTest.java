package ru.elynx.battlesnake.engine;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.protocol.*;

import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GameStrategyBasicTest {
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
        return Stream.of("Snake 1", "Snake 1a");
    }

    @BeforeEach
    void resetDummies() {
        dummyGameState.setTurn(0);

        dummyGameState.getBoard().setFood(new LinkedList<>());
        dummyGameState.getBoard().setSnakes(new LinkedList<>());

        dummyGameState.setYou(new SnakeDto());
        dummyGameState.getYou().setId("TestYou-id");
        dummyGameState.getYou().setName("TestYou-name");
        dummyGameState.getYou().setHealth(100);
        dummyGameState.getYou().setBody(new LinkedList<>());
        dummyGameState.getYou().getBody().add(new CoordsDto(0, 0));
        dummyGameState.getYou().setShout("TestYou-shout");

        dummyGameState.getBoard().getSnakes().add(dummyGameState.getYou());
    }

    @Test
    public void factoryAutowired() {
        assertNotNull(gameStrategyFactory);
    }

    @Test
    public void factoryAlwaysMakesGameStrategy() throws Exception {
        IGameStrategy gameStrategy1 = gameStrategyFactory.alwaysGetGameStrategy(dummyGameState);
        assertNotNull(gameStrategy1);

        IGameStrategy gameStrategy2 = gameStrategyFactory.alwaysGetGameStrategy(null);
        assertNotNull(gameStrategy2);
    }

    @Test
    public void factoryHasStrategies() {
        assertTrue(gameStrategyFactory.getRegisteredStrategies().size() > 0);
    }

    @Test
    public void factoryGetGameStrategyThrowsOnInvalidIndex() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> gameStrategyFactory.getGameStrategy(null));
        assertThrows(IllegalArgumentException.class, () -> gameStrategyFactory.getGameStrategy("Foo"));
    }

    @Test
    public void allStrategiesAreTested() throws Exception {
        Stream<String> testedStrategies = provideStrategyNames();
        Set<String> knownStrategies = gameStrategyFactory.getRegisteredStrategies();

        assertIterableEquals(testedStrategies.collect(Collectors.toSet()), knownStrategies);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    public void factoryGetGameStrategy(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        assertNotNull(gameStrategy);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    public void gameStrategyGivesConfig(String name) throws Exception {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        SnakeConfigDto snakeConfig = gameStrategy.processStart(dummyGameState);
        assertNotNull(snakeConfig);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    public void gameStrategyGivesMove(String name) throws Exception {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        MoveDto move = gameStrategy.processMove(dummyGameState);
        assertNotNull(move);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    public void gameStrategyDoesNotThrowOnEnd(String name) throws Exception {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        assertDoesNotThrow(() -> gameStrategy.processEnd(dummyGameState));
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    public void gameStrategyDoesNotGoIntoWall(String name) throws Exception {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);

        dummyGameState.getYou().getBody().get(0).setY(0);

        for (int x = 0; x < dummyGameState.getBoard().getWidth(); ++x) {
            dummyGameState.getYou().getBody().get(0).setX(x);

            MoveDto move = gameStrategy.processMove(dummyGameState);
            assertFalse("up".equalsIgnoreCase(move.getMove()));
        }

        for (int y = 0; y < dummyGameState.getBoard().getHeight(); ++y) {
            dummyGameState.getYou().getBody().get(0).setY(y);

            MoveDto move = gameStrategy.processMove(dummyGameState);
            assertFalse("right".equalsIgnoreCase(move.getMove()));
        }

        for (int x = dummyGameState.getBoard().getWidth() - 1; x >= 0; --x) {
            dummyGameState.getYou().getBody().get(0).setX(x);

            MoveDto move = gameStrategy.processMove(dummyGameState);
            assertFalse("down".equalsIgnoreCase(move.getMove()));
        }

        for (int y = dummyGameState.getBoard().getHeight() - 1; y >= 0; --y) {
            dummyGameState.getYou().getBody().get(0).setY(y);

            MoveDto move = gameStrategy.processMove(dummyGameState);
            assertFalse("left".equalsIgnoreCase(move.getMove()));
        }
    }
}

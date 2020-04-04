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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GameStrategyBasicTest {
    static GameState dummyGameState;

    @Autowired
    IGameStrategyFactory gameStrategyFactory;

    @BeforeAll
    static void fillDummies() {
        dummyGameState = new GameState();

        dummyGameState.setGame(new Game());
        dummyGameState.getGame().setId(GameStrategyBasicTest.class.getSimpleName());

        dummyGameState.setBoard(new Board());
        dummyGameState.getBoard().setHeight(11);
        dummyGameState.getBoard().setWidth(15);
    }

    public static Stream<Integer> provideStrategyIndexes() {
        return Stream.of(0, 1);
    }

    @BeforeEach
    void resetDummies() {
        dummyGameState.setTurn(0);

        dummyGameState.getBoard().setFood(new LinkedList<>());
        dummyGameState.getBoard().setSnakes(new LinkedList<>());

        dummyGameState.setYou(new Snake());
        dummyGameState.getYou().setId("TestYou-id");
        dummyGameState.getYou().setName("TestYou-name");
        dummyGameState.getYou().setHealth(100);
        dummyGameState.getYou().setBody(new LinkedList<>());
        dummyGameState.getYou().getBody().add(new Coords(0, 0));
        dummyGameState.getYou().setShout("TestYou-shout");

        dummyGameState.getBoard().getSnakes().add(dummyGameState.getYou());
    }

    @Test
    public void factoryAutowired() {
        assertNotNull(gameStrategyFactory);
    }

    @Test
    public void factoryAlwaysMakesGameStrategy() throws Exception {
        IGameStrategy gameStrategy1 = gameStrategyFactory.makeGameStrategy(dummyGameState);
        assertNotNull(gameStrategy1);

        IGameStrategy gameStrategy2 = gameStrategyFactory.makeGameStrategy(null);
        assertNotNull(gameStrategy2);
    }

    @Test
    public void factoryStrategySizeIsGreaterThanZero() {
        assertTrue(gameStrategyFactory.getGameStrategySize() > 0);
    }

    @Test
    public void factoryGetGameStrategyThrowsOnInvalidIndex() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> gameStrategyFactory.getGameStrategy(-1));
        assertThrows(IllegalArgumentException.class, () -> gameStrategyFactory.getGameStrategy(gameStrategyFactory.getGameStrategySize()));
    }

    @Test
    public void allStrategiesAreTested() throws Exception {
        Stream<Integer> testedStrategies = provideStrategyIndexes();

        List<Integer> indexes = new LinkedList<>();
        for (int i = 0; i < gameStrategyFactory.getGameStrategySize(); ++i) {
            indexes.add(i);
        }

        assertIterableEquals(testedStrategies.collect(Collectors.toList()), indexes);
    }

    @ParameterizedTest
    @MethodSource("provideStrategyIndexes")
    public void factoryGetGameStrategy(Integer index) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(index);
        assertNotNull(gameStrategy);
    }

    @ParameterizedTest
    @MethodSource("provideStrategyIndexes")
    public void gameStrategyGivesConfig(Integer index) throws Exception {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(index);
        SnakeConfig snakeConfig = gameStrategy.processStart(dummyGameState);
        assertNotNull(snakeConfig);
    }

    @ParameterizedTest
    @MethodSource("provideStrategyIndexes")
    public void gameStrategyGivesMove(Integer index) throws Exception {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(index);
        Move move = gameStrategy.processMove(dummyGameState);
        assertNotNull(move);
    }

    @ParameterizedTest
    @MethodSource("provideStrategyIndexes")
    public void gameStrategyDoesNotThrowOnEnd(Integer index) throws Exception {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(index);
        assertDoesNotThrow(() -> gameStrategy.processEnd(dummyGameState));
    }

    @ParameterizedTest
    @MethodSource("provideStrategyIndexes")
    public void gameStrategyDoesNotGoIntoWall(Integer index) throws Exception {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(index);

        dummyGameState.getYou().getBody().get(0).setY(0);

        for (int x = 0; x < dummyGameState.getBoard().getWidth(); ++x) {
            dummyGameState.getYou().getBody().get(0).setX(x);

            Move move = gameStrategy.processMove(dummyGameState);
            assertFalse("up".equalsIgnoreCase(move.getMove()));
        }

        for (int y = 0; y < dummyGameState.getBoard().getHeight(); ++y) {
            dummyGameState.getYou().getBody().get(0).setY(y);

            Move move = gameStrategy.processMove(dummyGameState);
            assertFalse("right".equalsIgnoreCase(move.getMove()));
        }

        for (int x = dummyGameState.getBoard().getWidth() - 1; x >= 0; --x) {
            dummyGameState.getYou().getBody().get(0).setX(x);

            Move move = gameStrategy.processMove(dummyGameState);
            assertFalse("down".equalsIgnoreCase(move.getMove()));
        }

        for (int y = dummyGameState.getBoard().getHeight() - 1; y >= 0; --y) {
            dummyGameState.getYou().getBody().get(0).setY(y);

            Move move = gameStrategy.processMove(dummyGameState);
            assertFalse("left".equalsIgnoreCase(move.getMove()));
        }
    }
}

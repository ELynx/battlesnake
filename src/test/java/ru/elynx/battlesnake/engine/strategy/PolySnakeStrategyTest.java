package ru.elynx.battlesnake.engine.strategy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.MoveCommandAndProbability;
import ru.elynx.battlesnake.entity.Snake;
import ru.elynx.battlesnake.testbuilder.CaseBuilder;

@SpringBootTest
@Tag("Internals")
class PolySnakeStrategyTest {
    public static final String CARTESIAN = "ru.elynx.battlesnake.engine.strategy.PolySnakeStrategyTest#provideCartesianProduct";

    @Autowired
    IGameStrategyFactory gameStrategyFactory;

    public static Stream<String> providePolyStrategyNames() {
        return Stream.of("Ahaetulla", "Pixel");
    }

    public static Stream<GameState> provideGameStates() {
        return Arrays.stream(CaseBuilder.class.getMethods())
                .filter((Method method) -> method.getGenericParameterTypes().length == 0
                        && method.getGenericReturnType().getTypeName().equals(GameState.class.getTypeName()))
                .map(method -> {
                    try {
                        return method.invoke(null);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        fail();
                    }
                    return null;
                }).map(GameState.class::cast);
    }

    public static Stream<Object[]> provideCartesianProduct() {
        return providePolyStrategyNames().flatMap(x -> provideGameStates().map(y -> {
            Object[] z = new Object[2];
            z[0] = x;
            z[1] = y;
            return z;
        }));
    }

    @Test
    void test_all_poly_strategies_are_tested() {
        Stream<String> testedStrategies = providePolyStrategyNames();
        Set<String> knownStrategies = gameStrategyFactory.getRegisteredStrategies();

        Set<String> temp1 = testedStrategies.sorted().collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> temp2 = knownStrategies.stream().filter(name -> {
            IGameStrategy strategy = gameStrategyFactory.getGameStrategy(name);
            return strategy instanceof IPolySnakeGameStrategy;
        }).sorted().collect(Collectors.toCollection(LinkedHashSet::new));

        assertIterableEquals(temp1, temp2);
    }

    @Test
    void test_non_zero_cases_are_tested() {
        Set<GameState> usedCases = provideGameStates().collect(Collectors.toSet());
        assertFalse(usedCases.isEmpty());
    }

    @Test
    void test_cartesian_all() {
        Set<String> testedStrategies = providePolyStrategyNames().collect(Collectors.toSet());
        Set<GameState> usedCases = provideGameStates().collect(Collectors.toSet());

        Set<Object[]> cartesianProduct = provideCartesianProduct().collect(Collectors.toSet());

        assertEquals(testedStrategies.size() * usedCases.size(), cartesianProduct.size());
    }

    @ParameterizedTest
    @MethodSource(CARTESIAN)
    void test_no_throw_on_primary(String name, GameState gameState) {
        IPolySnakeGameStrategy gameStrategy = (IPolySnakeGameStrategy) gameStrategyFactory.getGameStrategy(name);

        assertDoesNotThrow(() -> {
            gameStrategy.setPrimarySnake(gameState.getYou());
            gameStrategy.init(gameState);
            gameStrategy.setPrimarySnake(gameState.getYou());
        });
    }

    @ParameterizedTest
    @MethodSource(CARTESIAN)
    void test_consistent_1(String name, GameState gameState) {
        IPolySnakeGameStrategy gameStrategy = (IPolySnakeGameStrategy) gameStrategyFactory.getGameStrategy(name);

        gameStrategy.init(gameState);
        gameStrategy.setPrimarySnake(gameState.getYou());
        assertEquals(gameStrategy.processMove(gameState), gameStrategy.processMove(gameState.getYou(), gameState));
    }

    @ParameterizedTest
    @MethodSource(CARTESIAN)
    void test_consistent_2(String name, GameState gameState) {
        IPolySnakeGameStrategy gameStrategy = (IPolySnakeGameStrategy) gameStrategyFactory.getGameStrategy(name);

        gameStrategy.init(gameState);
        gameStrategy.setPrimarySnake(gameState.getYou());
        assertEquals(gameStrategy.processMove(gameState),
                gameStrategy.processMoveWithProbabilities(gameState.getYou(), gameState).stream()
                        .max(Comparator.comparingDouble(MoveCommandAndProbability::getProbability))
                        .map(MoveCommandAndProbability::getMoveCommand));
    }

    @ParameterizedTest
    @MethodSource(CARTESIAN)
    void test_does_not_throw_on_all_snakes(String name, GameState gameState) {
        IPolySnakeGameStrategy gameStrategy = (IPolySnakeGameStrategy) gameStrategyFactory.getGameStrategy(name);

        gameStrategy.init(gameState);
        gameStrategy.setPrimarySnake(gameState.getYou());
        for (Snake snake : gameState.getBoard().getSnakes()) {
            assertDoesNotThrow(() -> gameStrategy.processMoveWithProbabilities(snake, gameState));
        }
    }

    @ParameterizedTest
    @MethodSource(CARTESIAN)
    void test_does_not_invoke_you(String name, GameState gameState) {
        IPolySnakeGameStrategy gameStrategy = (IPolySnakeGameStrategy) gameStrategyFactory.getGameStrategy(name);

        GameState spy = spy(gameState);

        gameStrategy.init(spy);
        gameStrategy.setPrimarySnake(gameState.getYou()); // not via spy
        for (Snake snake : gameState.getBoard().getSnakes()) {
            assertDoesNotThrow(() -> gameStrategy.processMoveWithProbabilities(snake, spy));
            assertDoesNotThrow(() -> gameStrategy.processMove(snake, spy));
        }

        verify(spy, never()).getYou();
    }
}

package ru.elynx.battlesnake.engine.strategy;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Tag("Internals")
class GameStrategyFactoryTest {
    public static final String STRATEGY_NAMES = "ru.elynx.battlesnake.engine.strategy.GameStrategyFactoryTest#provideStrategyNames";
    public static final String FIVE_SKULLS = "\uD83D\uDC80\uD83D\uDC80\uD83D\uDC80\uD83D\uDC80\uD83D\uDC80";

    @Autowired
    IGameStrategyFactory gameStrategyFactory;

    public static Stream<String> provideStrategyNames() {
        return Stream.of("Ahaetulla", "Pixel", "Voxel", "Taste of Space", FIVE_SKULLS);
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
    void test_all_strategies_are_tested() {
        Set<String> testedStrategies = provideStrategyNames().sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> knownStrategies = gameStrategyFactory.getRegisteredStrategies().stream().sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));

        assertIterableEquals(knownStrategies, testedStrategies);
    }

    @ParameterizedTest
    @MethodSource(STRATEGY_NAMES)
    void test_factory_produces_strategy(String name) {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        assertNotNull(gameStrategy);
    }
}

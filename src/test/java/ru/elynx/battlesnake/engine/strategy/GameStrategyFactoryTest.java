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
}

package ru.elynx.battlesnake.testsnake;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.engine.strategy.SnakeNotFoundException;

@Tag("TestComponent")
class MySnakeGameStrategyFactoryTest {
    @Test
    void test_gives_my_snake() {
        MySnakeGameStrategyFactory tested = new MySnakeGameStrategyFactory();

        IGameStrategy gameStrategy = tested.getGameStrategy("My Snake");
        assertTrue(gameStrategy instanceof MySnake);
    }

    @Test
    void test_throws_when_unknown() {
        MySnakeGameStrategyFactory tested = new MySnakeGameStrategyFactory();

        assertThrows(SnakeNotFoundException.class, () -> tested.getGameStrategy("Foo"));
    }

    @Test
    void test_knows_only_my_snake() {
        MySnakeGameStrategyFactory tested = new MySnakeGameStrategyFactory();

        Set<String> known = tested.getRegisteredStrategies();

        assertEquals(1, known.size());
        assertEquals("My Snake", known.iterator().next());
    }
}

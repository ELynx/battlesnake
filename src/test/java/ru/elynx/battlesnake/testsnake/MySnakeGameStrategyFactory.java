package ru.elynx.battlesnake.testsnake;

import java.util.Set;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.engine.strategy.IGameStrategyFactory;
import ru.elynx.battlesnake.engine.strategy.SnakeNotFoundException;

public class MySnakeGameStrategyFactory implements IGameStrategyFactory {
    @Override
    public IGameStrategy getGameStrategy(String name) throws SnakeNotFoundException {
        if (name.equals("My Snake")) {
            return new MySnake();
        }

        throw new SnakeNotFoundException("Test snake factory does not have snake [" + name + ']');
    }

    @Override
    public Set<String> getRegisteredStrategies() {
        return Set.of("My Snake");
    }
}

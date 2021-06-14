package ru.elynx.battlesnake.engine.strategy;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameStrategyFactory implements IGameStrategyFactory {
    private final Map<String, Supplier<IGameStrategy>> registeredGameStrategies;

    @Autowired
    public GameStrategyFactory(Map<String, Supplier<IGameStrategy>> registeredGameStrategies) {
        this.registeredGameStrategies = registeredGameStrategies;
    }

    @Override
    public IGameStrategy getGameStrategy(String name) throws SnakeNotFoundException {
        Supplier<IGameStrategy> supplier = registeredGameStrategies.get(name);

        if (supplier == null)
            throw new SnakeNotFoundException(name);

        return supplier.get();
    }

    @Override
    public Set<String> getRegisteredStrategies() {
        return registeredGameStrategies.keySet();
    }
}

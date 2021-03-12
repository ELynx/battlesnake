package ru.elynx.battlesnake.engine;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameStrategyFactory implements IGameStrategyFactory {
    @Autowired
    Map<String, Supplier<IGameStrategy>> registeredGameStrategies;

    @Override
    public IGameStrategy getGameStrategy(String name) throws SnakeNotFoundException {
        final Supplier<IGameStrategy> supplier = registeredGameStrategies.get(name);

        if (supplier == null)
            throw new SnakeNotFoundException(name);

        return supplier.get();
    }

    @Override
    public Set<String> getRegisteredStrategies() {
        return registeredGameStrategies.keySet();
    }
}

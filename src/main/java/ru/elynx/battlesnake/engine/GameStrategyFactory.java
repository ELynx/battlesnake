package ru.elynx.battlesnake.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Service
public class GameStrategyFactory implements IGameStrategyFactory {
    private final Logger logger = LoggerFactory.getLogger(GameStrategyFactory.class);

    @Autowired
    Map<String, Supplier<IGameStrategy>> registeredGameStrategies;

    @Override
    public IGameStrategy getGameStrategy(String name) throws SnakeNotFoundException {
        Supplier<IGameStrategy> supplier = registeredGameStrategies.get(name);

        if (supplier == null)
            throw new SnakeNotFoundException("Game strategy [" + name + "] is not registered");

        return supplier.get();
    }

    @Override
    public Set<String> getRegisteredStrategies() {
        return registeredGameStrategies.keySet();
    }
}

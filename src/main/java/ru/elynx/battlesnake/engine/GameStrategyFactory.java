package ru.elynx.battlesnake.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.elynx.battlesnake.protocol.GameState;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Service
public class GameStrategyFactory implements IGameStrategyFactory {
    private final Logger logger = LoggerFactory.getLogger(GameStrategyFactory.class);

    @Autowired
    Map<String, Supplier<IGameStrategy>> registeredGameStrategies;

    @Autowired
    Supplier<IGameStrategy> primaryGameStrategy;

    public IGameStrategy makeGameStrategy(GameState gameState) {
        String snakeName = "undefined";

        try {
            snakeName = gameState.getYou().getName();
            return getGameStrategy(snakeName);
        } catch (Exception e) {
            logger.error("Exception choosing game strategy", e);
        }

        return primaryGameStrategy.get();
    }

    @Override
    public Set<String> getRegisteredStrategies() {
        return registeredGameStrategies.keySet();
    }

    @Override
    public IGameStrategy getGameStrategy(String name) {
        Supplier<IGameStrategy> supplier = registeredGameStrategies.get(name);

        if (supplier == null)
            throw new IllegalArgumentException("Game strategy [" + name + "] is not registered");

        return supplier.get();
    }
}

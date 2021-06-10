package ru.elynx.battlesnake.engine.strategy;

import java.util.Set;

public interface IGameStrategyFactory {
    IGameStrategy getGameStrategy(String name) throws SnakeNotFoundException;

    Set<String> getRegisteredStrategies();
}

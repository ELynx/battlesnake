package ru.elynx.battlesnake.engine;

import java.util.Set;

public interface IGameStrategyFactory {
    IGameStrategy getGameStrategy(String name) throws SnakeNotFoundException;

    Set<String> getRegisteredStrategies();
}

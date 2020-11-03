package ru.elynx.battlesnake.engine;

import java.util.Set;

public interface IGameStrategyFactory {
    IGameStrategy makeGameStrategy(String name);

    Set<String> getRegisteredStrategies();

    IGameStrategy getGameStrategy(String name);
}

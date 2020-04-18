package ru.elynx.battlesnake.engine;

import ru.elynx.battlesnake.protocol.GameState;

import java.util.Set;

public interface IGameStrategyFactory {
    IGameStrategy makeGameStrategy(GameState gameState);

    Set<String> getRegisteredStrategies();

    IGameStrategy getGameStrategy(String name);
}

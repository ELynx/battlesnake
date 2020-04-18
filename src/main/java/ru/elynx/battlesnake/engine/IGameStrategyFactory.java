package ru.elynx.battlesnake.engine;

import ru.elynx.battlesnake.protocol.GameStateDto;

import java.util.Set;

public interface IGameStrategyFactory {
    IGameStrategy makeGameStrategy(GameStateDto gameState);

    Set<String> getRegisteredStrategies();

    IGameStrategy getGameStrategy(String name);
}

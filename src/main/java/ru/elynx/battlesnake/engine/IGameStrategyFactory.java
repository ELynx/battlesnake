package ru.elynx.battlesnake.engine;

import ru.elynx.battlesnake.protocol.GameState;

public interface IGameStrategyFactory {
    IGameStrategy makeGameStrategy(GameState gameState);

    int getGameStrategySize();

    IGameStrategy getGameStrategy(int index) throws IllegalArgumentException;
}

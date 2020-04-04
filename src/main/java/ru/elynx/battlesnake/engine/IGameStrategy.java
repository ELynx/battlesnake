package ru.elynx.battlesnake.engine;

import ru.elynx.battlesnake.protocol.GameState;
import ru.elynx.battlesnake.protocol.Move;
import ru.elynx.battlesnake.protocol.SnakeConfig;

public interface IGameStrategy {
    SnakeConfig processStart(GameState gameState);

    Move processMove(GameState gameState);

    Void processEnd(GameState gameState);

    default SnakeConfig getSnakeConfig() {
        return SnakeConfig.DEFAULT_SNAKE_CONFIG;
    }
}

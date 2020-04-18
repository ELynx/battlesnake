package ru.elynx.battlesnake.engine;

import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.MoveDto;
import ru.elynx.battlesnake.protocol.SnakeConfigDto;

public interface IGameStrategy {
    SnakeConfigDto DEFAULT_SNAKE_CONFIG = new SnakeConfigDto("#ffbf00", "smile", "regular");

    SnakeConfigDto processStart(GameStateDto gameState);

    MoveDto processMove(GameStateDto gameState);

    Void processEnd(GameStateDto gameState);

    default SnakeConfigDto getSnakeConfig() {
        return DEFAULT_SNAKE_CONFIG;
    }
}

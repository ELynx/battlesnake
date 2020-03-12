package ru.elynx.battlesnake.engine;

import ru.elynx.battlesnake.protocol.GameState;
import ru.elynx.battlesnake.protocol.Move;
import ru.elynx.battlesnake.protocol.SnakeConfig;

public class GameEngine implements IGameEngine {
    static private Move hardcode = new Move("up", "3% ready");

    @Override
    public SnakeConfig processStart(GameState gameState) {
        return getSnakeConfig();
    }

    @Override
    public Move processMove(GameState gameState) {
        return hardcode;
    }

    @Override
    public Void processEnd(GameState gameState) {
        return null;
    }
}

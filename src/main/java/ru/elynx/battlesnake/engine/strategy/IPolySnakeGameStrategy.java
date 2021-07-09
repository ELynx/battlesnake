package ru.elynx.battlesnake.engine.strategy;

import java.util.Optional;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.MoveCommand;
import ru.elynx.battlesnake.entity.Snake;

public interface IPolySnakeGameStrategy extends IGameStrategy {
    default Optional<MoveCommand> processMove(GameState gameState) {
        return processMove(gameState.getYou(), gameState);
    }

    Optional<MoveCommand> processMove(Snake snake, GameState gameState);
}

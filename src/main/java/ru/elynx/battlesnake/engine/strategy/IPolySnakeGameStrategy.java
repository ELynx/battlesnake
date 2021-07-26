package ru.elynx.battlesnake.engine.strategy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.MoveCommand;
import ru.elynx.battlesnake.entity.MoveCommandWithProbability;
import ru.elynx.battlesnake.entity.Snake;

public interface IPolySnakeGameStrategy extends IGameStrategy {
    void setPrimarySnake(Snake snake);

    default Optional<MoveCommand> processMove(GameState gameState) {
        return processMove(gameState.getYou(), gameState);
    }

    Optional<MoveCommand> processMove(Snake snake, GameState gameState);

    default List<MoveCommandWithProbability> processMove(Snake snake, GameState gameState, int maxMoves) {
        Optional<MoveCommand> bestMove = processMove(snake, gameState);
        return bestMove.map(MoveCommandWithProbability::onlyFrom).orElse(Collections.emptyList());
    }
}

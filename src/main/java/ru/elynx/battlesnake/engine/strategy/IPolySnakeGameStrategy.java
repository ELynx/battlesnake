package ru.elynx.battlesnake.engine.strategy;

import java.util.Comparator;
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

    default Optional<MoveCommand> processMove(Snake snake, GameState gameState) {
        return processMoveWithProbabilities(snake, gameState).stream()
                .max(Comparator.comparingDouble(MoveCommandWithProbability::getProbability))
                .map(MoveCommandWithProbability::getMoveCommand);
    }

    List<MoveCommandWithProbability> processMoveWithProbabilities(Snake snake, GameState gameState);
}

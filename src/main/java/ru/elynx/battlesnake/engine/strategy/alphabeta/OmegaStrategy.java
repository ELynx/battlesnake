package ru.elynx.battlesnake.engine.strategy.alphabeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.math.FlagMatrix;
import ru.elynx.battlesnake.engine.predictor.IPredictorInformant;
import ru.elynx.battlesnake.engine.predictor.SnakeMovePredictor;
import ru.elynx.battlesnake.engine.strategy.Common;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.engine.strategy.IPolySnakeGameStrategy;
import ru.elynx.battlesnake.entity.*;

public class OmegaStrategy implements IPolySnakeGameStrategy, IPredictorInformant {
    private FlagMatrix occupiedPositions;
    private SnakeMovePredictor snakeMovePredictor;

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#314152", "pixel-round", "pixel-round", "2");
    }

    @Override
    public void init(GameState gameState) {
        occupiedPositions = FlagMatrix.uninitializedMatrix(gameState.getBoard().getDimensions(), true);
        snakeMovePredictor = new SnakeMovePredictor(this);
    }

    @Override
    public void setPrimarySnake(Snake snake) {
        // OmegaStrategy has nothing different for primary snake
    }

    @Override
    public List<MoveCommandWithProbability> processMoveWithProbabilities(Snake snake, GameState gameState) {
        setupPredictorInformant(gameState);
        return snakeMoveProbabilities(snake, gameState);
    }

    private void setupPredictorInformant(GameState gameState) {
        occupiedPositions.unsetAll();
        Common.forAllSnakeBodies(gameState, occupiedPositions::set);
    }

    private List<MoveCommandWithProbability> snakeMoveProbabilities(Snake snake, GameState gameState) {
        var rankedMoves = snakeMovePredictor.predict(snake, gameState);
        List<MoveCommandWithProbability> result = new ArrayList<>(rankedMoves.size());
        for (CoordinatesWithDirection direction : snake.getHead().getSideNeighbours()) {
            for (var rankedMove : rankedMoves) {
                if (direction.equals(rankedMove.getValue0())) {
                    result.add(new MoveCommandWithProbability(direction.getDirection(), rankedMove.getValue1()));
                }
            }
        }

        return result;
    }

    @Override
    public boolean isWalkable(Coordinates coordinates) {
        return !occupiedPositions.isSet(coordinates);
    }

    @Configuration
    public static class OmegaStrategyConfiguration {
        @Bean("Pixel")
        public Supplier<IGameStrategy> omega() {
            return OmegaStrategy::new;
        }
    }
}

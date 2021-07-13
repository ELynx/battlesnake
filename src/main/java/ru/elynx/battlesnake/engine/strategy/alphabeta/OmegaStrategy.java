package ru.elynx.battlesnake.engine.strategy.alphabeta;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Supplier;
import org.javatuples.Pair;
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
    public Optional<MoveCommand> processMove(Snake snake, GameState gameState) {
        setupPredictorInformant(gameState);
        return bestMoveForSnake(snake, gameState);
    }

    private void setupPredictorInformant(GameState gameState) {
        occupiedPositions.unsetAll();
        Common.forAllSnakeBodies(gameState, occupiedPositions::set);
    }

    private Optional<MoveCommand> bestMoveForSnake(Snake snake, GameState gameState) {
        var rankedMoves = snakeMovePredictor.predict(snake, gameState);
        var bestMove = rankedMoves.stream().max(Comparator.comparingDouble(Pair::getValue1));
        if (bestMove.isEmpty()) {
            return Optional.empty();
        }

        Coordinates coordinates = bestMove.get().getValue0();
        return snake.getHead().sideNeighbours().stream().filter(x -> x.equals(coordinates))
                .map(CoordinatesWithDirection::getDirection).findAny();
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

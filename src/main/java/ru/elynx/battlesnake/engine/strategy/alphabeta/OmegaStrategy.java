package ru.elynx.battlesnake.engine.strategy.alphabeta;

import static ru.elynx.battlesnake.entity.MoveCommand.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.math.FlagMatrix;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.engine.predictor.IPredictorInformant;
import ru.elynx.battlesnake.engine.predictor.implementation.ScoreMaker;
import ru.elynx.battlesnake.engine.strategy.Common;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.entity.*;

public class OmegaStrategy implements IGameStrategy, IPredictorInformant {
    private FlagMatrix occupiedPositions;

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#314152", "pixel-round", "pixel-round", "not very smart");
    }

    @Override
    public void init(HazardPredictor hazardPredictor) {
        GameState gameState = hazardPredictor.getGameState();
        occupiedPositions = FlagMatrix.uninitializedMatrix(gameState.getBoard().getDimensions(), true);
    }

    @Override
    public Void processStart(HazardPredictor hazardPredictor) {
        return null;
    }

    @Override
    public Move processMove(HazardPredictor hazardPredictor) {
        GameState gameState = hazardPredictor.getGameState();
        Optional<MoveCommand> moveCommand = bestMoveForSnake(gameState.getYou(), gameState);

        return new Move(moveCommand.orElse(REPEAT_LAST));
    }

    protected Optional<MoveCommand> bestMoveForSnake(Snake snake, GameState gameState) {
        ScoreMaker scoreMaker = makeScoreMaker(snake, gameState);

        Collection<CoordinatesWithDirection> moves = snake.getHead().sideNeighbours();
        return moves.stream().sorted(Comparator.comparingInt(scoreMaker::scoreMove).reversed())
                .map(CoordinatesWithDirection::getDirection).findFirst();
    }

    protected ScoreMaker makeScoreMaker(Snake snake, GameState gameState) {
        setupPredictorInformant(gameState);
        return new ScoreMaker(snake, gameState, this);
    }

    private void setupPredictorInformant(GameState gameState) {
        occupiedPositions.unsetAll();
        Common.forAllSnakeBodies(gameState, coordinates -> occupiedPositions.set(coordinates));
    }

    @Override
    public Void processEnd(HazardPredictor hazardPredictor) {
        return null;
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

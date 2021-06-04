package ru.elynx.battlesnake.engine.strategy.alphabeta;

import static ru.elynx.battlesnake.entity.MoveCommand.*;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import org.javatuples.Pair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.math.FreeSpaceMatrix;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.engine.predictor.IPredictorInformant;
import ru.elynx.battlesnake.engine.predictor.SnakeMovePredictor;
import ru.elynx.battlesnake.engine.strategy.Common;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Move;

public class OmegaStrategy implements IGameStrategy, IPredictorInformant {
    protected FreeSpaceMatrix freeSpaceMatrix;
    protected SnakeMovePredictor predictor;

    protected OmegaStrategy() {
    }

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#314152", "pixel-round", "pixel-round", "not very smart");
    }

    @Override
    public void init(HazardPredictor hazardPredictor) {
        freeSpaceMatrix = FreeSpaceMatrix
                .uninitializedMatrix(hazardPredictor.getGameState().getBoard().getDimensions());
        predictor = new SnakeMovePredictor(this);
    }

    @Override
    public Void processStart(HazardPredictor hazardPredictor) {
        return null;
    }

    @Override
    public Move processMove(HazardPredictor hazardPredictor) {
        freeSpaceMatrix.empty();

        GameState gameState = hazardPredictor.getGameState();
        Common.forAllSnakeBodies(gameState, coordinates -> freeSpaceMatrix.setOccupied(coordinates));

        List<Pair<Coordinates, Double>> predictions = predictor.predict(hazardPredictor.getGameState().getYou(),
                gameState);

        // repeat last
        if (predictions.isEmpty()) {
            return new Move(REPEAT_LAST, null); // TODO not null
        }

        predictions.sort(Comparator.<Pair<Coordinates, Double>>comparingDouble(Pair::getValue1).reversed());

        Pair<Coordinates, Double> bestPrediction = predictions.get(0);
        int px = bestPrediction.getValue0().getX();
        int py = bestPrediction.getValue0().getY();

        Coordinates head = hazardPredictor.getGameState().getYou().getHead();
        int x = head.getX();
        int y = head.getY();

        int dx = px - x;
        int dy = py - y;

        // TODO not nulls
        if (dx > 0) {
            return new Move(RIGHT, null);
        } else if (dx < 0) {
            return new Move(LEFT, null);
        } else if (dy > 0) {
            return new Move(UP, null);
        } else if (dy < 0) {
            return new Move(DOWN, null);
        } else {
            return new Move(REPEAT_LAST, null);
        }
    }

    @Override
    public Void processEnd(HazardPredictor hazardPredictor) {
        return null;
    }

    @Override
    public boolean isWalkable(Coordinates coordinates) {
        return freeSpaceMatrix.isFree(coordinates);
    }

    @Configuration
    public static class OmegaStrategyConfiguration {
        @Bean("Pixel")
        public Supplier<IGameStrategy> thoughtful() {
            return OmegaStrategy::new;
        }
    }
}

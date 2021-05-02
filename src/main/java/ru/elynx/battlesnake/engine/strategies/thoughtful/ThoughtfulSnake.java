package ru.elynx.battlesnake.engine.strategies.thoughtful;

import static ru.elynx.battlesnake.protocol.Move.Moves.*;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import org.javatuples.Triplet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.math.FreeSpaceMatrix;
import ru.elynx.battlesnake.engine.predictor.GameStatePredictor;
import ru.elynx.battlesnake.engine.predictor.IPredictorInformant;
import ru.elynx.battlesnake.engine.predictor.SnakeMovePredictor;
import ru.elynx.battlesnake.engine.strategies.CommonPatterns;
import ru.elynx.battlesnake.protocol.BattlesnakeInfo;
import ru.elynx.battlesnake.protocol.CoordsDto;
import ru.elynx.battlesnake.protocol.Move;

public class ThoughtfulSnake implements IGameStrategy, IPredictorInformant {
    protected FreeSpaceMatrix freeSpaceMatrix;
    protected SnakeMovePredictor predictor;

    protected ThoughtfulSnake() {
    }

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#05bfbf", "shades", "rbc-necktie", "1");
    }

    @Override
    public void init(GameStatePredictor gameState) {
        freeSpaceMatrix = FreeSpaceMatrix.uninitializedMatrix(gameState.getBoard().getWidth(),
                gameState.getBoard().getHeight());
        predictor = new SnakeMovePredictor(this);
    }

    @Override
    public Void processStart(GameStatePredictor gameState) {
        return null;
    }

    @Override
    public Move processMove(GameStatePredictor gameState) {
        freeSpaceMatrix.empty();
        CommonPatterns.forSnakeBody(gameState,
                coordsDto -> freeSpaceMatrix.setOccupied(coordsDto.getX(), coordsDto.getY()));

        List<Triplet<Integer, Integer, Double>> predictions = predictor.predict(gameState.getYou(), gameState);

        // repeat last
        if (predictions.isEmpty()) {
            return new Move();
        }

        predictions.sort(Comparator
                .comparingDouble((ToDoubleFunction<Triplet<Integer, Integer, Double>>) Triplet::getValue2).reversed());

        Triplet<Integer, Integer, Double> bestPrediction = predictions.get(0);
        int px = bestPrediction.getValue0();
        int py = bestPrediction.getValue1();

        CoordsDto head = gameState.getYou().getHead();
        int x = head.getX();
        int y = head.getY();

        int dx = px - x;
        int dy = py - y;

        if (dx > 0) {
            return new Move(RIGHT);
        } else if (dx < 0) {
            return new Move(LEFT);
        } else if (dy > 0) {
            return new Move(UP);
        } else if (dy < 0) {
            return new Move(DOWN);
        } else {
            return new Move();
        }
    }

    @Override
    public Void processEnd(GameStatePredictor gameState) {
        return null;
    }

    @Override
    public boolean isWalkable(int x, int y) {
        return freeSpaceMatrix.isFree(x, y);
    }

    @Configuration
    public static class ThoughtfulSnakeConfiguration {
        @Bean("The-serpent-saves-us-from-thought")
        public Supplier<IGameStrategy> thoughtful() {
            return ThoughtfulSnake::new;
        }
    }
}

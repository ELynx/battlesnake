package ru.elynx.battlesnake.engine.strategies.weightedsearch;

import static ru.elynx.battlesnake.protocol.Move.Moves.*;

import java.util.List;
import java.util.function.Supplier;
import javafx.util.Pair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.math.DoubleMatrix;
import ru.elynx.battlesnake.engine.math.Util;
import ru.elynx.battlesnake.engine.strategies.shared.SnakeMovePredictor;
import ru.elynx.battlesnake.protocol.*;

public class WeightedSearchV2Strategy implements IGameStrategy {
    private static final double MIN_FOOD_WEIGHT = 0.0d;
    private static final double MAX_FOOD_WEIGHT = 1.0d;
    private static final double HUNGER_HEALTH_THRESHOLD = 100.0d;
    private static final double LESSER_SNAKE_HEAD_WEIGHT = 0.75d;
    private static final double TIMED_OUT_LESSER_SNAKE_HEAD_WEIGHT = 0.0d;
    private static final double GREATER_SNAKE_HEAD_WEIGHT = -1.0d;
    private static final double SNAKE_BODY_WEIGHT = -1.0d;
    private static final double HAZARD_WEIGHT = -Double.MAX_VALUE;

    protected final double wallWeight;
    protected final String version;
    protected DoubleMatrix weightMatrix;
    protected SnakeMovePredictor snakeMovePredictor;
    protected boolean initialized = false;

    private WeightedSearchV2Strategy(double wallWeight, String version) {
        this.wallWeight = wallWeight;
        this.version = version;
    }

    protected void initOnce(GameStateDto gameState) {
        if (initialized)
            return;

        weightMatrix = DoubleMatrix.uninitializedMatrix(gameState.getBoard().getWidth(),
                gameState.getBoard().getHeight(), wallWeight);

        snakeMovePredictor = new SnakeMovePredictor();

        initialized = true;
    }

    protected void applyGameState(GameStateDto gameState) {
        weightMatrix.zero();

        applyHunger(gameState);
        applySnakes(gameState);
        applyHazards(gameState);
    }

    protected void applyHunger(GameStateDto gameState) {
        double foodWeight = Util.scale(MIN_FOOD_WEIGHT, HUNGER_HEALTH_THRESHOLD - gameState.getYou().getHealth(),
                HUNGER_HEALTH_THRESHOLD, MAX_FOOD_WEIGHT);

        if (foodWeight <= 0.0)
            return;

        for (CoordsDto food : gameState.getBoard().getFood()) {
            final int x = food.getX();
            final int y = food.getY();

            weightMatrix.splash2ndOrder(x, y, foodWeight);
        }
    }

    protected void applySnakes(GameStateDto gameState) {
        snakeMovePredictor.setGameState(gameState);

        final CoordsDto ownHead = gameState.getYou().getHead();
        final int ownSize = gameState.getYou().getLength();
        final String ownId = gameState.getYou().getId();

        for (SnakeDto snake : gameState.getBoard().getSnakes()) {
            final List<CoordsDto> body = snake.getBody();
            final CoordsDto head = snake.getHead();
            final int size = body.size();
            final String id = snake.getId();

            // manage head
            {
                final int x = head.getX();
                final int y = head.getY();

                weightMatrix.setValue(x, y, SNAKE_BODY_WEIGHT);

                if (!id.equals(ownId)) {
                    double baseWeight;
                    if (size < ownSize) {
                        baseWeight = snake.getLatency() == 0
                                ? TIMED_OUT_LESSER_SNAKE_HEAD_WEIGHT
                                : LESSER_SNAKE_HEAD_WEIGHT;
                    } else {
                        baseWeight = GREATER_SNAKE_HEAD_WEIGHT;
                    }

                    if (baseWeight != 0.0) {
                        if (head.manhattanDistance(ownHead) > 4) {
                            // cheap and easy on faraway snakes
                            weightMatrix.splash1stOrder(x, y, baseWeight);
                        } else {
                            // rapid decline "scent" field for more distant catch-up
                            weightMatrix.splash2ndOrder(x, y, baseWeight, 10);

                            List<Pair<CoordsDto, Double>> predictions = snakeMovePredictor.predict(snake);
                            predictions.forEach(prediction -> {
                                final int px = prediction.getKey().getX();
                                final int py = prediction.getKey().getY();
                                final double w = baseWeight * prediction.getValue();

                                weightMatrix.setValue(px, py, w);
                            });
                        }
                    }
                }
            }

            // manage body
            for (int i = 1; i < size; ++i) {
                final int x = body.get(i).getX();
                final int y = body.get(i).getY();

                // cell with three pieces of snake around should cost less than piece of snake
                weightMatrix.splash1stOrder(x, y, SNAKE_BODY_WEIGHT, 4.0);
            }
        }
    }

    protected void applyHazards(GameStateDto gameState) {
        for (CoordsDto hazard : gameState.getBoard().getHazards()) {
            final int x = hazard.getX();
            final int y = hazard.getY();

            weightMatrix.setValue(x, y, HAZARD_WEIGHT);
        }
    }

    private double getCrossWeight(int x, int y) {
        double result = weightMatrix.getValue(x, y - 1);
        result += weightMatrix.getValue(x - 1, y);
        result += weightMatrix.getValue(x, y);
        result += weightMatrix.getValue(x + 1, y);
        result += weightMatrix.getValue(x, y + 1);
        return result;
    }

    protected String bestMove(CoordsDto head) {
        final int x = head.getX();
        final int y = head.getY();

        // index ascending order

        String bestDirection = DOWN;
        double bestValue = getCrossWeight(x, y - 1);

        double nextValue = getCrossWeight(x - 1, y);
        if (nextValue > bestValue) {
            bestDirection = LEFT;
            bestValue = nextValue;
        }

        nextValue = getCrossWeight(x + 1, y);
        if (nextValue > bestValue) {
            bestDirection = RIGHT;
            bestValue = nextValue;
        }

        nextValue = getCrossWeight(x, y + 1);
        if (nextValue > bestValue) {
            bestDirection = UP;
        }

        return bestDirection;
    }

    protected String makeMove(GameStateDto gameState) {
        applyGameState(gameState);
        return bestMove(gameState.getYou().getHead());
    }

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#b58900", "smile", "sharp", version);
    }

    @Override
    public Void processStart(GameStateDto gameState) {
        initOnce(gameState);
        return null;
    }

    @Override
    public Move processMove(GameStateDto gameState) {
        // for test compatibility
        initOnce(gameState);
        final String move = makeMove(gameState);
        return new Move(move, "New and theoretically improved");
    }

    @Override
    public Void processEnd(GameStateDto gameState) {
        return null;
    }

    @Configuration
    public static class WeightedSearchV2StrategyConfiguration {
        private static final double WALL_WEIGHT_NEUTRAL = 0.0d;

        @Bean("Snake_1a")
        public Supplier<IGameStrategy> wallWeightZero() {
            return () -> new WeightedSearchV2Strategy(WALL_WEIGHT_NEUTRAL, "1a X");
        }
    }
}

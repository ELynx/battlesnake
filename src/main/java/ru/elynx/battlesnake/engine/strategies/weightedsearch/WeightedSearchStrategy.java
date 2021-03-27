package ru.elynx.battlesnake.engine.strategies.weightedsearch;

import static ru.elynx.battlesnake.protocol.Move.Moves.*;

import java.util.List;
import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.math.DoubleMatrix;
import ru.elynx.battlesnake.engine.math.FlagMatrix;
import ru.elynx.battlesnake.engine.math.Util;
import ru.elynx.battlesnake.protocol.*;

public class WeightedSearchStrategy implements IGameStrategy {
    private static final double MIN_FOOD_WEIGHT = 0.1d;
    private static final double MAX_FOOD_WEIGHT = 1.0d;
    private static final double LESSER_SNAKE_HEAD_WEIGHT = 0.75d;
    private static final double TIMED_OUT_LESSER_SNAKE_HEAD_WEIGHT = 0.0d;
    private static final double SNAKE_BODY_WEIGHT = -1.0d;
    private static final double BLOCKED_MOVE_WEIGHT = -Double.MAX_VALUE;
    private static final double HAZARD_WEIGHT = -Double.MAX_VALUE;
    private static final double REPEAT_LAST_MOVE_WEIGHT = 0.01d;
    private static final double MAX_HEALTH = 100.0d;
    private static final double WALL_WEIGHT_NEGATIVE = -1.0d;

    private static final String VERSION = "archival";

    protected DoubleMatrix weightMatrix;
    protected FlagMatrix blockedMatrix;
    protected String lastMove;
    protected boolean initialized = false;

    private WeightedSearchStrategy() {
    }

    protected void initOnce(GameStateDto gameState) {
        if (initialized)
            return;

        weightMatrix = DoubleMatrix.uninitializedMatrix(gameState.getBoard().getWidth(),
                gameState.getBoard().getHeight(), WALL_WEIGHT_NEGATIVE);

        blockedMatrix = FlagMatrix.uninitializedMatrix(gameState.getBoard().getWidth(),
                gameState.getBoard().getHeight(), true);

        lastMove = UP;

        initialized = true;
    }

    protected void applyGameState(GameStateDto gameState) {
        weightMatrix.zero();
        blockedMatrix.reset();

        applyHunger(gameState);
        applySnakes(gameState);
        applyHazards(gameState);
    }

    @SuppressWarnings("deprecation")
    protected void applyHunger(GameStateDto gameState) {
        double foodWeight = Util.scale(MIN_FOOD_WEIGHT, MAX_HEALTH - gameState.getYou().getHealth(), MAX_HEALTH,
                MAX_FOOD_WEIGHT);

        for (CoordsDto food : gameState.getBoard().getFood()) {
            final int x = food.getX();
            final int y = food.getY();

            weightMatrix.splash2ndOrderLegacy(x, y, foodWeight);
        }
    }

    @SuppressWarnings("deprecation")
    protected void applySnakes(GameStateDto gameState) {
        // cell with three pieces of snake around should cost less than piece of snake
        final double denominator = 4.0;

        final int ownSize = gameState.getYou().getLength();

        for (SnakeDto snake : gameState.getBoard().getSnakes()) {
            final List<CoordsDto> body = snake.getBody();
            for (int i = 0, size = body.size(); i < size; ++i) {
                final int x = body.get(i).getX();
                final int y = body.get(i).getY();

                if (i == 0 && size < ownSize) {
                    // don't explicitly rush for disconnected
                    final double headWeight = snake.isTimedOut()
                            ? TIMED_OUT_LESSER_SNAKE_HEAD_WEIGHT
                            : LESSER_SNAKE_HEAD_WEIGHT;
                    weightMatrix.splash2ndOrderLegacy(x, y, headWeight);
                } else {
                    // since we are looking for strictly less own body will get into no-go category
                    weightMatrix.splash1stOrderLegacy(x, y, SNAKE_BODY_WEIGHT, denominator);

                    // block only losing snake pieces
                    blockedMatrix.setValue(x, y, true);
                }
            }
        }
    }

    protected void applyHazards(GameStateDto gameState) {
        for (CoordsDto hazard : gameState.getBoard().getHazards()) {
            final int x = hazard.getX();
            final int y = hazard.getY();

            weightMatrix.setValue(x, y, HAZARD_WEIGHT);
            blockedMatrix.setValue(x, y, true);
        }
    }

    private double getCrossWeight(int x, int y) {
        if (blockedMatrix.getValue(x, y))
            return BLOCKED_MOVE_WEIGHT;

        double result = weightMatrix.getValue(x, y - 1);
        result += weightMatrix.getValue(x - 1, y);
        result += weightMatrix.getValue(x, y);
        result += weightMatrix.getValue(x + 1, y);
        result += weightMatrix.getValue(x, y + 1);
        return result;
    }

    private double getDirectionWeight(String direction) {
        if (direction.equals(lastMove))
            return REPEAT_LAST_MOVE_WEIGHT;
        return 0.0d;
    }

    protected String bestMove(CoordsDto head) {
        final int x = head.getX();
        final int y = head.getY();

        // index ascending order

        String bestDirection = DOWN;
        double bestValue = getCrossWeight(x, y - 1) + getDirectionWeight(DOWN);

        double nextValue = getCrossWeight(x - 1, y) + getDirectionWeight(LEFT);
        if (nextValue > bestValue) {
            bestDirection = LEFT;
            bestValue = nextValue;
        }

        nextValue = getCrossWeight(x + 1, y) + getDirectionWeight(RIGHT);
        if (nextValue > bestValue) {
            bestDirection = RIGHT;
            bestValue = nextValue;
        }

        nextValue = getCrossWeight(x, y + 1) + getDirectionWeight(UP);
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
        return new BattlesnakeInfo("ELynx", "#cecece", "smile", "sharp", VERSION);
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
        lastMove = move;
        return new Move(move, "I am a reference (point)");
    }

    @Override
    public Void processEnd(GameStateDto gameState) {
        return null;
    }

    @Configuration
    public static class WeightedSearchStrategyConfiguration {
        @Bean("Snake_1")
        public Supplier<IGameStrategy> archiveWeightedSearch() {
            return WeightedSearchStrategy::new;
        }
    }
}

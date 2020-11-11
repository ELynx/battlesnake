package ru.elynx.battlesnake.engine.strategies;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.math.DoubleMatrix;
import ru.elynx.battlesnake.engine.math.FlagMatrix;
import ru.elynx.battlesnake.engine.math.Util;
import ru.elynx.battlesnake.protocol.*;

import java.util.List;
import java.util.function.Supplier;

public class WeightedSearchStrategy implements IGameStrategy {
    private final static String LEFT = "left";

    protected final double wallWeight;
    protected final String version;
    protected DoubleMatrix weightMatrix;
    protected FlagMatrix blockedMatrix;
    protected int maxHealth;
    protected String lastMove;
    protected boolean initialized = false;

    private WeightedSearchStrategy(double wallWeight, String version) {
        this.wallWeight = wallWeight;
        this.version = version;
    }

    protected void initOnce(GameStateDto gameState) {
        if (initialized)
            return;

        weightMatrix = DoubleMatrix.uninitializedMatrix(
                gameState.getBoard().getWidth(),
                gameState.getBoard().getHeight(),
                wallWeight);
                gameState.getBoard().getHeight(), wallWeight);

        blockedMatrix = FlagMatrix.uninitializedMatrix(gameState.getBoard().getWidth(),
                gameState.getBoard().getHeight(), true);

        maxHealth = gameState.getYou().getHealth();
        lastMove = UP;

        initialized = true;
    }

    protected void applyGameState(GameStateDto gameState) {
        weightMatrix.zero();
        blockedMatrix.reset();

        // apply hunger
        {
            double foodWeight = Util.scale(MIN_FOOD_WEIGHT, maxHealth - gameState.getYou().getHealth(), maxHealth,
                    MAX_FOOD_WEIGHT);

            for (CoordsDto food : gameState.getBoard().getFood()) {
                final int x = food.getX();
                final int y = food.getY();

                weightMatrix.splash2ndOrder(x, y, foodWeight);
            }
        }

        // apply snake bodies for collision and hunt
        {
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
                        final double headWeight = snake.getLatency() == 0
                                ? TIMED_OUT_LESSER_SNAKE_HEAD_WEIGHT
                                : LESSER_SNAKE_HEAD_WEIGHT;
                        weightMatrix.splash2ndOrder(x, y, headWeight);
                    } else {
                        // since we are looking for strictly less own body will get into no-go category
                        weightMatrix.splash1stOrder(x, y, SNAKE_BODY_WEIGHT, denominator);

                        // block only losing snake pieces
                        blockedMatrix.setValue(x, y, true);
                    }
                }
            }
        }

        // apply hazards
        {
            for (CoordsDto hazard : gameState.getBoard().getHazards()) {
                final int x = hazard.getX();
                final int y = hazard.getY();

                weightMatrix.setValue(x, y, HAZARD_WEIGHT);
                blockedMatrix.setValue(x, y, true);
            }
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
        Integer x = head.getX();
        Integer y = head.getY();

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
            // bestValue = nextValue;
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
        lastMove = move;
        return new Move(move, "7% ready");
    }

    @Override
    public Void processEnd(GameStateDto gameState) {
        return null;
    }

    @Configuration
    public static class WeightedSearchStrategyConfiguration {
        private static final double WALL_WEIGHT_NEGATIVE = -1.0d;
        private static final double WALL_WEIGHT_NEUTRAL = 0.0d;

        @Bean("Snake_1")
        public Supplier<IGameStrategy> wallWeightNegativeOne() {
            return () -> new WeightedSearchStrategy(WALL_WEIGHT_NEGATIVE, "1");
        }

        @Bean("Snake_1a")
        public Supplier<IGameStrategy> wallWeightZero() {
            return () -> new WeightedSearchStrategy(WALL_WEIGHT_NEUTRAL, "1a");
        }
    }
}

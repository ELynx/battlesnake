package ru.elynx.battlesnake.engine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.elynx.battlesnake.engine.math.DoubleMatrix;
import ru.elynx.battlesnake.engine.math.FlagMatrix;
import ru.elynx.battlesnake.engine.math.Util;
import ru.elynx.battlesnake.protocol.*;

import java.util.List;
import java.util.function.Supplier;

public class WeightedSearchStrategy implements IGameStrategy {
    private final static double MIN_FOOD_WEIGHT = 0.1d;
    private final static double MAX_FOOD_WEIGHT = 1.0d;
    private final static double LESSER_SNAKE_HEAD_WEIGHT = 0.75d;
    private final static double SNAKE_BODY_WEIGHT = -1.0d;
    private final static double BLOCKED_MOVE_WEIGHT = -Double.MAX_VALUE;
    private final static double REPEAT_LAST_MOVE_WEIGHT = 0.01d;

    private final static String UP = "up";
    private final static String RIGHT = "right";
    private final static String DOWN = "down";
    private final static String LEFT = "left";

    protected final double wallWeight;
    protected final String version;
    protected DoubleMatrix weightMatrix;
    protected FlagMatrix blockedMatrix;
    protected int maxHealth;
    protected String lastMove;

    private WeightedSearchStrategy(double wallWeight, String version) {
        this.wallWeight = wallWeight;
        this.version = version;
    }

    protected void init(GameStateDto gameState) {
        weightMatrix = DoubleMatrix.zeroMatrix(
                gameState.getBoard().getWidth(),
                gameState.getBoard().getHeight(),
                wallWeight);

        blockedMatrix = FlagMatrix.falseMatrix(
                gameState.getBoard().getWidth(),
                gameState.getBoard().getHeight(),
                true);

        maxHealth = gameState.getYou().getHealth();
        lastMove = UP;
    }

    protected void applyGameState(GameStateDto gameState) {
        weightMatrix.zero();
        blockedMatrix.reset();

        // apply hunger
        {
            double foodWeight = Util.scale(MIN_FOOD_WEIGHT, maxHealth - gameState.getYou().getHealth(), maxHealth, MAX_FOOD_WEIGHT);

            for (CoordsDto food : gameState.getBoard().getFood()) {
                Integer x = food.getX();
                Integer y = food.getY();

                weightMatrix.splash2ndOrder(x, y, foodWeight);
            }
        }

        // apply snake bodies for collision and hunt
        {
            // cell with three pieces of snake around should cost less than piece of snake
            final double denominator = 4.0;

            int ownSize = gameState.getYou().getBody().size();

            for (SnakeDto snake : gameState.getBoard().getSnakes()) {
                List<CoordsDto> body = snake.getBody();
                for (int i = 0, size = body.size(); i < size; ++i) {
                    Integer x = body.get(i).getX();
                    Integer y = body.get(i).getY();

                    // since we are looking for strictly less own body will get into wall category
                    // side effect for using splash: all but last pieces get splash
                    if (i == 0 && size < ownSize) {
                        weightMatrix.splash2ndOrder(x, y, LESSER_SNAKE_HEAD_WEIGHT);
                    } else {
                        weightMatrix.splash1stOrder(x, y, SNAKE_BODY_WEIGHT, denominator);
                    }

                    blockedMatrix.setValue(x, y, true);
                }
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

        String bestDirection = UP;
        double bestValue = getCrossWeight(x, y - 1) + getDirectionWeight(UP);

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

        nextValue = getCrossWeight(x, y + 1) + getDirectionWeight(DOWN);
        if (nextValue > bestValue) {
            bestDirection = DOWN;
            //bestValue = nextValue;
        }

        return bestDirection;
    }

    protected String makeMove(GameStateDto gameState) {
        applyGameState(gameState);
        return bestMove(gameState.getYou().getBody().get(0));
    }

    @Override
    public Void processStart(GameStateDto gameState) {
        init(gameState);
        return null;
    }

    @Override
    public Move processMove(GameStateDto gameState) {
        String move = makeMove(gameState);
        lastMove = move;
        return new Move(move, "7% ready");
    }

    @Override
    public Void processEnd(GameStateDto gameState) {
        return null;
    }

    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("ELynx", "#ffbf00", "smile", "regular", version);
    }

    @Configuration
    public static class WeightedSearchStrategyConfiguration {
        @Bean("Snake 1")
        @Primary
        public Supplier<IGameStrategy> wallWeightNegativeOne() {
            return () -> new WeightedSearchStrategy(-1.0, "1");
        }

        @Bean("Snake 1a")
        public Supplier<IGameStrategy> wallWeightZero() {
            return () -> new WeightedSearchStrategy(0.0d, "1a");
        }
    }
}

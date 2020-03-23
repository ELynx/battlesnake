package ru.elynx.battlesnake.engine;

import ru.elynx.battlesnake.engine.math.Matrix;
import ru.elynx.battlesnake.engine.math.Util;
import ru.elynx.battlesnake.protocol.*;

import java.util.List;

public class GameEngine implements IGameEngine {
    private final static double WALL_WEIGHT = -1.0d;
    private final static double MIN_FOOD_WEIGHT = 0.1d;
    private final static double MAX_FOOD_WEIGHT = 1.0d;
    private final static double LESSER_SNAKE_HEAD_WEIGHT = 0.75d;
    private final static double SNAKE_BODY_WEIGHT = WALL_WEIGHT;
    private final static double REPEAT_LAST_MOVE_WEIGHT = 0.01;

    private final static String UP = "up";
    private final static String RIGHT = "right";
    private final static String DOWN = "down";
    private final static String LEFT = "left";

    protected Matrix matrix;
    protected int maxHealth;
    protected String lastMove;
    protected boolean initialized = false;

    protected void initOnce(GameState gameState) {
        if (initialized)
            return;

        matrix = Matrix.zeroMatrix(
                gameState.getBoard().getWidth(),
                gameState.getBoard().getHeight(),
                WALL_WEIGHT);

        maxHealth = gameState.getYou().getHealth();
        lastMove = UP;

        initialized = true;
    }

    @Override
    public SnakeConfig processStart(GameState gameState) {
        initOnce(gameState);
        return getSnakeConfig();
    }

    protected void applyGameState(GameState gameState) {
        matrix.zero();

        // apply hunger
        {
            double foodWeight = Util.scale(MIN_FOOD_WEIGHT, gameState.getYou().getHealth(), maxHealth, MAX_FOOD_WEIGHT);

            for (Coords food : gameState.getBoard().getFood()) {
                Integer x = food.getX();
                Integer y = food.getY();

                matrix.splash2ndOrder(x, y, foodWeight);
            }
        }

        // apply snake bodies for collision and hunt
        {
            // cell with three pieces of snake around should cost less than piece of snake
            final double denominator = 4.0;

            int ownSize = gameState.getYou().getBody().size();

            for (Snake snake : gameState.getBoard().getSnakes()) {
                List<Coords> body = snake.getBody();
                for (int i = 0, size = body.size(); i < size; ++i) {
                    Integer x = body.get(i).getX();
                    Integer y = body.get(i).getY();

                    // since we are looking for strictly less own body will get into wall category
                    // side effect for using splash: all but last pieces get splash
                    if (i == 0 && size < ownSize) {
                        matrix.splash2ndOrder(x, y, LESSER_SNAKE_HEAD_WEIGHT);
                    } else {
                        matrix.splash1stOrder(x, y, SNAKE_BODY_WEIGHT, denominator);
                    }
                }
            }
        }
    }

    private double getCrossWeight(int x, int y) {
        double result = matrix.getValue(x, y - 1);
        result += matrix.getValue(x - 1, y);
        result += matrix.getValue(x, y);
        result += matrix.getValue(x + 1, y);
        result += matrix.getValue(x, y + 1);
        return result;
    }

    private double getDirectionWeight(String direction) {
        if (direction.equals(lastMove))
            return REPEAT_LAST_MOVE_WEIGHT;
        return 0.0d;
    }

    protected String bestMove(Coords head) {
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

    protected String makeMove(GameState gameState) {
        applyGameState(gameState);
        return bestMove(gameState.getYou().getBody().get(0));
    }

    @Override
    public Move processMove(GameState gameState) {
        initOnce(gameState);
        String move = makeMove(gameState);
        lastMove = move;
        return new Move(move, "5% ready");
    }

    @Override
    public Void processEnd(GameState gameState) {
        return null;
    }
}

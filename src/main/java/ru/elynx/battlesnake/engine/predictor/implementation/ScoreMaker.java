package ru.elynx.battlesnake.engine.predictor.implementation;

import ru.elynx.battlesnake.engine.math.Util;
import ru.elynx.battlesnake.engine.predictor.GameStatePredictor;
import ru.elynx.battlesnake.protocol.CoordsDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

public class ScoreMaker {
    public static final int LEAST_SCORE = 1; // start with least positive score
    public static final int FOOD_SCORE = 3; // sum of least score value and food is less than score of being eaten
    public static final int HUNT_SCORE = 5; // positive for hunting lesser snakes, negative for avoiding greater ones
    public static final int HAZARD_SCORE = -10; // avoid hazard above other goals

    private final SnakeDto snake;
    private final GameStatePredictor gameState;

    private int x;
    private int y;

    public ScoreMaker(SnakeDto snake, GameStatePredictor gameState) {
        this.snake = snake;
        this.gameState = gameState;
    }

    public int scoreMove(int x, int y) {
        this.x = x;
        this.y = y;

        return scoreMoveImpl();
    }

    private int scoreMoveImpl() {
        int score = LEAST_SCORE;

        score += getFoodScore();
        score += getHuntScore();
        score += getHazardScore();

        return score;
    }

    private int getFoodScore() {
        for (CoordsDto food : gameState.getBoard().getFood()) {
            if (food.getX() == x && food.getY() == y) {
                return FOOD_SCORE;
            }
        }

        return 0;
    }

    private int getHuntScore() {
        int huntScore = 0;
        for (SnakeDto otherSnake : gameState.getBoard().getSnakes()) {
            huntScore += getHuntScorePerSnakeIfNextMove(otherSnake);
        }

        return huntScore;
    }

    private int getHuntScorePerSnakeIfNextMove(SnakeDto otherSnake) {
        // manhattan distance 0 of prediction is collision
        if (!snake.getId().equals(otherSnake.getId()) && Util.manhattanDistance(otherSnake.getHead(), x, y) == 1) {
            return getHuntScorePerSnake(otherSnake);
        }

        return 0;
    }

    private int getHuntScorePerSnake(SnakeDto otherSnake) {
        int ownLength = snake.getLength();
        int otherLength = otherSnake.getLength();

        if (ownLength < otherLength)
            return -HUNT_SCORE;
        else if (ownLength > otherLength)
            return HUNT_SCORE;

        return 0;
    }

    private int getHazardScore() {
        for (CoordsDto hazard : gameState.getBoard().getHazards()) {
            if (hazard.getX() == x && hazard.getY() == y) {
                return HAZARD_SCORE;
            }
        }

        return 0;
    }
}

package ru.elynx.battlesnake.engine.predictor.implementation;

import ru.elynx.battlesnake.engine.math.Util;
import ru.elynx.battlesnake.engine.predictor.GameStatePredictor;
import ru.elynx.battlesnake.protocol.CoordsDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

public class ScoreMaker {
    private enum Mode {
        MOVE_MODE, HEAD_MODE
    }

    // delivering h2h win is best
    // eating food is good
    // staying alive is OK
    // h2h draw depends on other snake's risk, is as bad as eating food is good
    // going into hazard negates base + food, but better than h2h lose
    // h2h lose negates base + food
    private static final int HEAD_TO_HEAD_WIN_SCORE = 5;
    private static final int FOOD_SCORE = 3;
    private static final int BASE_SCORE = 1;
    private static final int HEAD_TO_HEAD_DRAW_SCORE = -3;
    private static final int HAZARD_SCORE = -4;
    private static final int HEAD_TO_HEAD_LOSE_SCORE = -5;

    private SnakeDto snake;
    private GameStatePredictor gameState;

    CoordsDto coords;
    private Mode mode;

    public ScoreMaker() {
        // all fields are initialized per call
    }

    public void reset(SnakeDto snake, GameStatePredictor gameState) {
        this.snake = snake;
        this.gameState = gameState;
    }

    // TODO automatic reference clean-up
    public void freeReferences() {
        this.snake = null;
        this.gameState = null;
    }

    public int scoreMove(CoordsDto coords) {
        this.coords = coords;
        this.mode = Mode.MOVE_MODE;

        return scoreImpl();
    }

    public int scoreHead() {
        this.coords = snake.getHead();
        this.mode = Mode.HEAD_MODE;

        return scoreImpl();
    }

    private int scoreImpl() {
        int score = BASE_SCORE;

        score += getFoodScore();
        score += getHuntScore();
        score += getHazardScore();

        return score;
    }

    private int getFoodScore() {
        for (CoordsDto food : gameState.getBoard().getFood()) {
            if (food.equals(coords)) {
                return FOOD_SCORE;
            }
        }

        return 0;
    }

    private int getHuntScore() {
        int score = 0;

        int ownLength = snake.getLength();
        for (SnakeDto otherSnake : gameState.getBoard().getSnakes()) {
            if (isNotSelf(otherSnake) && isScoreTarget(otherSnake)) {
                int otherLength = otherSnake.getLength();

                if (ownLength < otherLength)
                    return HEAD_TO_HEAD_LOSE_SCORE;

                if (ownLength > otherLength) {
                    score += HEAD_TO_HEAD_WIN_SCORE;
                } else {
                    score += HEAD_TO_HEAD_DRAW_SCORE;
                }
            }
        }

        return score;
    }

    private boolean isNotSelf(SnakeDto otherSnake) {
        return !otherSnake.getId().equals(snake.getId());
    }

    private boolean isScoreTarget(SnakeDto otherSnake) {
        switch (mode) {
            case MOVE_MODE :
                return isNextMove(otherSnake);
            case HEAD_MODE :
                return isCollision(otherSnake);
            default :
                return false;
        }
    }

    private boolean isNextMove(SnakeDto otherSnake) {
        return Util.manhattanDistance(otherSnake.getHead(), coords) == 1;
    }

    private boolean isCollision(SnakeDto otherSnake) {
        return otherSnake.getHead().equals(coords);
    }

    private int getHazardScore() {
        for (CoordsDto hazard : gameState.getBoard().getHazards()) {
            if (hazard.equals(coords)) {
                return HAZARD_SCORE;
            }
        }

        return 0;
    }
}

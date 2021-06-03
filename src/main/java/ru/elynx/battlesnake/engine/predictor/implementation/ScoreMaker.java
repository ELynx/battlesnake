package ru.elynx.battlesnake.engine.predictor.implementation;

import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Snake;

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

    private Snake snake;
    private HazardPredictor hazardPredictor;

    Coordinates coordinates;
    private Mode mode;

    public ScoreMaker() {
        // all fields are initialized per call
    }

    public void reset(Snake snake, HazardPredictor hazardPredictor) {
        this.snake = snake;
        this.hazardPredictor = hazardPredictor;
    }

    // TODO automatic reference clean-up
    public void freeReferences() {
        this.snake = null;
        this.hazardPredictor = null;
    }

    public int scoreMove(Coordinates coordinates) {
        this.coordinates = coordinates;
        this.mode = Mode.MOVE_MODE;

        return scoreImpl();
    }

    public int scoreHead() {
        this.coordinates = snake.getHead();
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
        for (Coordinates food : hazardPredictor.getGameState().getBoard().getFood()) {
            if (food.equals(coordinates)) {
                return FOOD_SCORE;
            }
        }

        return 0;
    }

    private int getHuntScore() {
        int score = 0;

        int ownLength = snake.getLength();
        for (Snake otherSnake : hazardPredictor.getGameState().getBoard().getSnakes()) {
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

    private boolean isNotSelf(Snake otherSnake) {
        return !otherSnake.getId().equals(snake.getId());
    }

    private boolean isScoreTarget(Snake otherSnake) {
        switch (mode) {
            case MOVE_MODE :
                return isNextMove(otherSnake);
            case HEAD_MODE :
                return isCollision(otherSnake);
            default :
                return false;
        }
    }

    private boolean isNextMove(Snake otherSnake) {
        return otherSnake.getHead().manhattanDistance(coordinates) == 1;
    }

    private boolean isCollision(Snake otherSnake) {
        return otherSnake.getHead().equals(coordinates);
    }

    private int getHazardScore() {
        for (Coordinates hazard : hazardPredictor.getGameState().getBoard().getHazards()) {
            if (hazard.equals(coordinates)) {
                return HAZARD_SCORE;
            }
        }

        return 0;
    }
}
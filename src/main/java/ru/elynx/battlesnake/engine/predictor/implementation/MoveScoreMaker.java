package ru.elynx.battlesnake.engine.predictor.implementation;

import ru.elynx.battlesnake.engine.predictor.IPredictorInformant;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Snake;

public class MoveScoreMaker {
    // delivering h2h win is best
    // eating food is good
    // staying alive is OK
    // h2h draw depends on other snake's risk, is as bad as eating food is good
    // going into hazard negates base + food, but better than h2h lose
    // h2h lose negates base + food
    // collision is the worst because it is certain self-induced loss
    private static final int HEAD_TO_HEAD_WIN_SCORE = 5;
    private static final int FOOD_SCORE = 3;
    private static final int BASE_SCORE = 1;
    private static final int HEAD_TO_HEAD_DRAW_SCORE = -3;
    private static final int HAZARD_SCORE = -4;
    private static final int HEAD_TO_HEAD_LOSE_SCORE = -5;
    private static final int COLLISION_SCORE = -10;

    private final Snake snake;
    private final GameState gameState;
    private final IPredictorInformant predictorInformant;

    private Coordinates coordinates;

    public MoveScoreMaker(Snake snake, GameState gameState, IPredictorInformant predictorInformant) {
        this.snake = snake;
        this.gameState = gameState;
        this.predictorInformant = predictorInformant;
    }

    public int scoreMove(Coordinates coordinates) {
        this.coordinates = coordinates;

        return scoreImpl();
    }

    private int scoreImpl() {
        if (isCollision()) {
            return COLLISION_SCORE;
        }

        int score = BASE_SCORE;

        score += getFoodScore();
        score += getHuntScore();
        score += getHazardScore();

        return score;
    }

    private boolean isCollision() {
        return !predictorInformant.isWalkable(coordinates);
    }

    private int getFoodScore() {
        for (Coordinates food : gameState.getBoard().getFood()) {
            if (food.equals(coordinates)) {
                return FOOD_SCORE;
            }
        }

        return 0;
    }

    private int getHuntScore() {
        int score = 0;

        int ownLength = snake.getLength();
        for (Snake someSnake : gameState.getBoard().getSnakes()) {
            if (isNotSelf(someSnake) && isScoreTarget(someSnake)) {
                int otherLength = someSnake.getLength();

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
        return isNextMove(otherSnake);
    }

    private boolean isNextMove(Snake otherSnake) {
        return otherSnake.getHead().manhattanDistance(coordinates) == 1;
    }

    private int getHazardScore() {
        for (Coordinates hazard : gameState.getBoard().getHazards()) {
            if (hazard.equals(coordinates)) {
                return HAZARD_SCORE;
            }
        }

        return 0;
    }
}

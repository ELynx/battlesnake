package ru.elynx.battlesnake.engine.strategy.alphabeta;

import lombok.experimental.UtilityClass;
import org.javatuples.Pair;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Snake;

@UtilityClass
public class GameStateScoreMaker {
    private static final Boolean TERMINATE = Boolean.TRUE;
    private static final Boolean CONTINUE = Boolean.FALSE;

    public static Pair<Boolean, Long> makeScore(Snake snake, GameState state0, GameState state1) {
        long scoreLimit = calculateScoreLimit(state0);

        if (state1.isEliminated(snake)) {
            return new Pair<>(TERMINATE, -scoreLimit);
        }

        if (oneSnakeLeft(state0, state1)) {
            return new Pair<>(TERMINATE, scoreLimit);
        }

        long score = stateScore(snake, state1);
        return new Pair<>(CONTINUE, score);
    }

    private static long calculateScoreLimit(GameState gameState) {
        return (long) gameState.getBoard().getDimensions().getArea() * Snake.getMaxHealth();
    }

    private static boolean oneSnakeLeft(GameState gameState0, GameState gameState1) {
        return gameState0.getBoard().getSnakes().size() > 1 && gameState1.getBoard().getSnakes().size() == 1;
    }

    private long stateScore(Snake snake, GameState gameState) {
        long score = 0;

        for (Snake someSnake : gameState.getBoard().getSnakes()) {
            long power = snakePower(someSnake, gameState);

            if (someSnake.getId().equals(snake.getId())) {
                score += power;
            } else {
                score -= power;
            }
        }

        return score;
    }

    private long snakePower(Snake snake, GameState gameState) {
        long score = snakeVitalityScore(snake);
        score += snakePositioningScore(snake, gameState);
        return score;
    }

    private long snakeVitalityScore(Snake snake) {
        return (long) snake.getLength() * snake.getHealth();
    }

    private long snakePositioningScore(Snake snake, GameState gameState) {
        Coordinates center = gameState.getBoard().getDimensions().getCenter();
        long score = 0L;
        for (Coordinates segment : snake.getBody()) {
            score -= center.getManhattanDistance(segment);
        }

        return score;
    }
}

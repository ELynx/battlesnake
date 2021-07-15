package ru.elynx.battlesnake.engine.strategy.alphabeta;

import lombok.experimental.UtilityClass;
import org.javatuples.Pair;
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
        return (long) gameState.getBoard().getDimensions().area() * Snake.getMaxHealth();
    }

    private static boolean oneSnakeLeft(GameState gameState0, GameState gameState1) {
        return gameState0.getBoard().getSnakes().size() > 1 && gameState1.getBoard().getSnakes().size() == 1;
    }

    private long stateScore(Snake snake, GameState gameState) {
        long score = 0;

        for (Snake someSnake : gameState.getBoard().getSnakes()) {
            long power = snakePower(someSnake);

            if (someSnake.getId().equals(snake.getId())) {
                score += power;
            } else {
                score -= power;
            }
        }

        return score;
    }

    private long snakePower(Snake snake) {
        return (long) snake.getLength() * snake.getHealth();
    }
}

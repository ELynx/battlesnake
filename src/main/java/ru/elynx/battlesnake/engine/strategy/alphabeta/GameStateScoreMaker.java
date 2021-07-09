package ru.elynx.battlesnake.engine.strategy.alphabeta;

import lombok.experimental.UtilityClass;
import org.javatuples.Pair;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Snake;

@UtilityClass
public class GameStateScoreMaker {
    private static final Boolean TERMINATE = Boolean.TRUE;
    private static final Boolean CONTINUE = Boolean.FALSE;

    private static final int LOSE_SCORE = -100;
    private static final int WIN_SCORE = 100;

    public static Pair<Boolean, Integer> makeYouScore(GameState state0, GameState state1) {
        return makeScore(state0.getYou(), state0, state1);
    }

    public static Pair<Boolean, Integer> makeScore(Snake snake, GameState state0, GameState state1) {
        if (state1.isEliminated(snake)) {
            return new Pair<>(TERMINATE, LOSE_SCORE);
        }

        if (oneSnakeLeft(state0, state1)) {
            return new Pair<>(TERMINATE, WIN_SCORE);
        }

        return new Pair<>(CONTINUE, 1);
    }

    private static boolean oneSnakeLeft(GameState gameState0, GameState gameState1) {
        return gameState0.getBoard().getSnakes().size() > 1 && gameState1.getBoard().getSnakes().size() == 1;
    }
}

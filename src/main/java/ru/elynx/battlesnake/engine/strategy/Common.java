package ru.elynx.battlesnake.engine.strategy;

import java.util.List;
import java.util.function.Consumer;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Snake;

public class Common {
    private Common() {
    }

    public static void forAllSnakeBodies(GameState gameState, Consumer<Coordinates> what) {
        for (Snake snake : gameState.getBoard().getSnakes()) {
            if (willSurvive(snake, gameState)) {
                forSnakeBody(snake, what);
            }
        }
    }

    private static boolean willSurvive(Snake snake, GameState gameState) {
        // TODO implement or reject idea
        return true;
    }

    private static void forSnakeBody(Snake snake, Consumer<Coordinates> what) {
        int tailMoveOffset = getTailMoveOffset(snake);

        List<Coordinates> body = snake.getBody();
        for (int i = 0; i < body.size() - tailMoveOffset; ++i) {
            Coordinates coordinates = body.get(i);
            what.accept(coordinates);
        }
    }

    private static int getTailMoveOffset(Snake snake) {
        if (snake.isGrowing()) {
            // tail will grow -> cell will remain occupied
            return 0;
        }

        // by default tail will go away
        return 1;
    }
}

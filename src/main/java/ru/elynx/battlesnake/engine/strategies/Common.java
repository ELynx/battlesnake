package ru.elynx.battlesnake.engine.strategies;

import java.util.List;
import java.util.function.Consumer;
import ru.elynx.battlesnake.api.CoordsDto;
import ru.elynx.battlesnake.api.SnakeDto;
import ru.elynx.battlesnake.engine.predictor.GameStatePredictor;

public class Common {
    private Common() {
    }

    public static void forSnakeBody(GameStatePredictor gameState, Consumer<CoordsDto> what) {
        for (SnakeDto snake : gameState.getBoard().getSnakes()) {
            final List<CoordsDto> body = snake.getBody();

            // by default tail will go away
            int tailMoveOffset = 1;

            // check if fed this turn
            if (gameState.isGrowing(snake)) {
                // tail will grow -> cell will remain occupied
                tailMoveOffset = 0;
            }

            for (int i = 0; i < body.size() - tailMoveOffset; ++i) {
                final CoordsDto coordsDto = body.get(i);
                what.accept(coordsDto);
            }
        }
    }
}

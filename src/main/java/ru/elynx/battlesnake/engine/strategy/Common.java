package ru.elynx.battlesnake.engine.strategy;

import java.util.List;
import java.util.function.Consumer;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Snake;

public class Common {
    private Common() {
    }

    public static void forSnakeBody(HazardPredictor hazardPredictor, Consumer<Coordinates> what) {
        for (Snake snake : hazardPredictor.getGameState().getBoard().getSnakes()) {
            List<Coordinates> body = snake.getBody();

            // by default tail will go away
            int tailMoveOffset = 1;

            // check if fed this turn
            if (hazardPredictor.getGameState().isSnakeGrowing(snake)) {
                // tail will grow -> cell will remain occupied
                tailMoveOffset = 0;
            }

            for (int i = 0; i < body.size() - tailMoveOffset; ++i) {
                Coordinates coordsDto = body.get(i);
                what.accept(coordsDto);
            }
        }
    }
}

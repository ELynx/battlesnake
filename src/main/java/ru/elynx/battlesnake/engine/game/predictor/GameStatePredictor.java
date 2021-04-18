package ru.elynx.battlesnake.engine.game.predictor;

import ru.elynx.battlesnake.engine.game.predictor.impl.IPredictorInformant;
import ru.elynx.battlesnake.engine.math.FreeSpaceMatrix;
import ru.elynx.battlesnake.protocol.CoordsDto;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

public class GameStatePredictor extends GameStateDto implements IPredictorInformant {
    private static final int INITIAL_LENGTH = 3;
    private static final int MAX_HEALTH = 100;

    private FreeSpaceMatrix freeSpaceMatrix = null;

    /**
     * Predict if snake will grow on it's tail this turn
     * Prediction is 100% if logic is not changed
     *
     * @param snake Snake to be checked
     * @return True if snake will not empty it's tail cell.
     */
    public boolean isGrowing(SnakeDto snake) {
        // initial expansion
        if (getTurn() < INITIAL_LENGTH) {
            return true;
        }

        // just ate food
        if (snake.getHealth().equals(MAX_HEALTH)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isWalkable(int x, int y) {
        if (freeSpaceMatrix == null) {
            freeSpaceMatrix = FreeSpaceMatrix.emptyFreeSpaceMatrix(getBoard().getWidth(), getBoard().getHeight());

            for (SnakeDto snake : getBoard().getSnakes()) {
                final int offset = isGrowing(snake) ? 0 : 1;

                for (int i = 0; i < snake.getLength() - offset; ++i) {
                    final CoordsDto coords = snake.getBody().get(i);
                    freeSpaceMatrix.setOccupied(coords.getX(), coords.getY());
                }
            }
        }

        return freeSpaceMatrix.getSpace(x, y) > 0;
    }
}

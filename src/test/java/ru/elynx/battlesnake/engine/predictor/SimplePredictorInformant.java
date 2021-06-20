package ru.elynx.battlesnake.engine.predictor;

import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Snake;

public class SimplePredictorInformant implements IPredictorInformant {
    private final GameState gameState;

    public SimplePredictorInformant(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public boolean isWalkable(Coordinates tested) {
        if (gameState.getBoard().getDimensions().outOfBounds(tested)) {
            return false;
        }

        for (Snake snake : gameState.getBoard().getSnakes()) {
            for (Coordinates body : snake.getBody()) {
                if (body.equals(tested)) {
                    return false;
                }
            }
        }

        return true;
    }
}

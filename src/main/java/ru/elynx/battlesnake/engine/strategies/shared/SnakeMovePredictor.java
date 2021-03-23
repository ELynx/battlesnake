package ru.elynx.battlesnake.engine.strategies.shared;

import java.util.Collections;
import java.util.List;
import javafx.util.Pair;
import ru.elynx.battlesnake.protocol.CoordsDto;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

public class SnakeMovePredictor {
    GameStateDto gameState;

    public SnakeMovePredictor() {
    }

    public void setGameState(GameStateDto gameState) {
        this.gameState = gameState;
    }

    public List<Pair<CoordsDto, Double>> predict(SnakeDto snake) {
        return Collections.emptyList();
    }
}

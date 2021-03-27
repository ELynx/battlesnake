package ru.elynx.battlesnake.engine.strategies.shared;

import java.util.Collections;
import java.util.List;
import org.javatuples.KeyValue;
import ru.elynx.battlesnake.engine.math.FreeSpaceMatrix;
import ru.elynx.battlesnake.protocol.CoordsDto;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

public class SnakeMovePredictor {
    GameStateDto gameState;
    FreeSpaceMatrix freeSpace;

    public SnakeMovePredictor() {
    }

    public void setGameState(GameStateDto gameState) {
        this.gameState = gameState;
    }

    public void setFreeSpace(FreeSpaceMatrix freeSpace) {
        this.freeSpace = freeSpace;
    }

    public List<KeyValue<CoordsDto, Double>> predict(SnakeDto snake) {
        return Collections.emptyList();
    }
}

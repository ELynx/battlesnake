package ru.elynx.battlesnake.engine.predictor;

import ru.elynx.battlesnake.protocol.CoordsDto;

public interface IPredictorInformant {
    // TODO leave only this
    default boolean isWalkable(CoordsDto coords) {
        return isWalkable(coords.getX(), coords.getY());
    }

    boolean isWalkable(int x, int y);
}

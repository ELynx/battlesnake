package ru.elynx.battlesnake.engine.predictor;

import ru.elynx.battlesnake.entity.Coordinates;

public interface IPredictorInformant {
    boolean isWalkable(Coordinates coordinates);
}

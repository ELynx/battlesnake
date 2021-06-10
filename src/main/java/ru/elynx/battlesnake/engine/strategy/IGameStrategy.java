package ru.elynx.battlesnake.engine.strategy;

import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;
import ru.elynx.battlesnake.entity.Move;

public interface IGameStrategy {
    BattlesnakeInfo getBattesnakeInfo();

    default void init(HazardPredictor hazardPredictor) {
    }

    Void processStart(HazardPredictor hazardPredictor);

    Move processMove(HazardPredictor hazardPredictor);

    Void processEnd(HazardPredictor hazardPredictor);

    default boolean isCombatant() {
        return true;
    }
}

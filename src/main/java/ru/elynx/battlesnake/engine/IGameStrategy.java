package ru.elynx.battlesnake.engine;

import ru.elynx.battlesnake.engine.predictor.GameStatePredictor;
import ru.elynx.battlesnake.protocol.BattlesnakeInfo;
import ru.elynx.battlesnake.protocol.Move;

public interface IGameStrategy {
    BattlesnakeInfo getBattesnakeInfo();

    default void init(GameStatePredictor gameState) {
    }

    Void processStart(GameStatePredictor gameState);

    Move processMove(GameStatePredictor gameState);

    Void processEnd(GameStatePredictor gameState);

    default boolean isCombatant() {
        return true;
    }
}

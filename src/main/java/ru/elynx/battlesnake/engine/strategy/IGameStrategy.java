package ru.elynx.battlesnake.engine.strategy;

import ru.elynx.battlesnake.entity.BattlesnakeInfo;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Move;

public interface IGameStrategy {
    BattlesnakeInfo getBattesnakeInfo();

    default void init(GameState gameState) {
    }

    Void processStart(GameState gameState);

    Move processMove(GameState gameState);

    Void processEnd(GameState gameState);

    default boolean isCombatant() {
        return true;
    }
}

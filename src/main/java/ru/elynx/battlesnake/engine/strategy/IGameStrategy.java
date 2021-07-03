package ru.elynx.battlesnake.engine.strategy;

import java.util.Optional;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.MoveCommand;

public interface IGameStrategy {
    BattlesnakeInfo getBattesnakeInfo();

    default void init(GameState gameState) {
    }

    Void processStart(GameState gameState);

    Optional<MoveCommand> processMove(GameState gameState);

    Void processEnd(GameState gameState);

    default boolean isCombatant() {
        return true;
    }
}

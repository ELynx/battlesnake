package ru.elynx.battlesnake.engine.strategy;

import java.util.Optional;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.MoveCommand;

public interface IGameStrategy {
    BattlesnakeInfo getBattesnakeInfo();

    default void init(GameState gameState) {
    }

    default Void processStart(GameState gameState) {
        return null;
    }

    Optional<MoveCommand> processMove(GameState gameState);

    default Void processEnd(GameState gameState) {
        return null;
    }
}

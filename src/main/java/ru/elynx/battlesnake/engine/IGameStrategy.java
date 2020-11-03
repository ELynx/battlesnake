package ru.elynx.battlesnake.engine;

import ru.elynx.battlesnake.protocol.BattlesnakeInfo;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.MoveDto;

public interface IGameStrategy {
    // TODO "#ffbf00", "smile", "regular";

    Void processStart(GameStateDto gameState);

    MoveDto processMove(GameStateDto gameState);

    Void processEnd(GameStateDto gameState);

    BattlesnakeInfo getBattesnakeInfo();
}

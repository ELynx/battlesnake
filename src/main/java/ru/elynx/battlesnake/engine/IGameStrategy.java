package ru.elynx.battlesnake.engine;

import ru.elynx.battlesnake.protocol.BattlesnakeInfo;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.Move;

public interface IGameStrategy {
    BattlesnakeInfo getBattesnakeInfo();

    Void processStart(GameStateDto gameState);

    Move processMove(GameStateDto gameState);

    Void processEnd(GameStateDto gameState);
}

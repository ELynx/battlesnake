package ru.elynx.battlesnake.testsnake;

import static ru.elynx.battlesnake.entity.MoveCommand.UP;

import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Move;

public class MySnake implements IGameStrategy {
    @Override
    public BattlesnakeInfo getBattesnakeInfo() {
        return new BattlesnakeInfo("Test Aut|hor", "#112233", "Test He|ad", "Test Ta|il", "Test Vers|ion");
    }

    @Override
    public Void processStart(GameState gameState) {
        return null;
    }

    @Override
    public Move processMove(GameState gameState) {
        return new Move(UP, "Test Sh|out");
    }

    @Override
    public Void processEnd(GameState gameState) {
        return null;
    }
}

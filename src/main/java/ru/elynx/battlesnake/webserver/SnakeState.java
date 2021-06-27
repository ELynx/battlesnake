package ru.elynx.battlesnake.webserver;

import static ru.elynx.battlesnake.entity.MoveCommand.REPEAT_LAST;
import static ru.elynx.battlesnake.entity.MoveCommand.UP;

import java.time.Instant;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Move;
import ru.elynx.battlesnake.entity.MoveCommand;

public class SnakeState {
    private final IGameStrategy gameStrategy;
    private boolean initialized;

    private MoveCommand lastMove;

    private Instant accessTime;

    SnakeState(IGameStrategy gameStrategy) {
        this.gameStrategy = gameStrategy;
        this.initialized = false;

        this.lastMove = UP;

        updateAccessTime();
    }

    private void updateAccessTime() {
        accessTime = Instant.now();
    }

    public boolean isLastAccessedBefore(Instant than) {
        return accessTime.isBefore(than);
    }

    public Void processStart(GameState gameState) {
        updateAccessTime();
        return initializeAndProcessStart(gameState);
    }

    private Void initializeAndProcessStart(GameState gameState) {
        initialize(gameState);
        return processStartImpl(gameState);
    }

    private void initialize(GameState gameState) {
        if (!initialized) {
            gameStrategy.init(gameState);
            initialized = true;
        }
    }

    private Void processStartImpl(GameState gameState) {
        return gameStrategy.processStart(gameState);
    }

    public Move processMove(GameState gameState) {
        updateAccessTime();
        Move move = initializeAndProcessMove(gameState);
        return handleRepeatLastMove(move);
    }

    private Move initializeAndProcessMove(GameState gameState) {
        initialize(gameState);
        return processMoveImpl(gameState);
    }

    private Move processMoveImpl(GameState gameState) {
        return gameStrategy.processMove(gameState);
    }

    private Move handleRepeatLastMove(Move move) {
        if (REPEAT_LAST.equals(move.getMoveCommand())) {
            return new Move(lastMove, move.getShout());
        }

        lastMove = move.getMoveCommand();

        return move;
    }

    public Void processEnd(GameState gameState) {
        updateAccessTime();

        if (!initialized) {
            return null;
        }

        return processEndImpl(gameState);
    }

    private Void processEndImpl(GameState gameState) {
        return gameStrategy.processEnd(gameState);
    }
}

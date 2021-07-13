package ru.elynx.battlesnake.webserver;

import static ru.elynx.battlesnake.entity.MoveCommand.UP;

import java.time.Instant;
import java.util.Optional;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.entity.*;

public class SnakeState {
    private final IGameStrategy gameStrategy;
    private boolean initialized;

    private Board lastBoard;
    private MoveCommand lastMove;

    private Instant accessTime;

    SnakeState(IGameStrategy gameStrategy) {
        this.gameStrategy = gameStrategy;
        this.initialized = false;

        this.lastBoard = null;
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
        gameState = addMetaInformation(gameState);
        return initializeAndProcessStart(gameState);
    }

    private GameState addMetaInformation(GameState gameState) {
        if (gameState.getRules().isRoyale()) {
            Board board = BoardWithActiveHazards.fromAdjacentTurns(lastBoard, gameState.getBoard());
            lastBoard = board;
            // recompose the game state only if there is need to do so
            if (board instanceof BoardWithActiveHazards) {
                return new GameState(gameState.getGameId(), gameState.getTurn(), gameState.getRules(), board,
                        gameState.getYou());
            }
        }

        return gameState;
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
        gameState = addMetaInformation(gameState);
        return initializeAndProcessMove(gameState);
    }

    private Move initializeAndProcessMove(GameState gameState) {
        initialize(gameState);
        return processMoveImpl(gameState);
    }

    private Move processMoveImpl(GameState gameState) {
        Optional<MoveCommand> currentMove = gameStrategy.processMove(gameState);
        currentMove.ifPresent(moveCommand -> lastMove = moveCommand);
        return new Move(lastMove);
    }

    public Void processEnd(GameState gameState) {
        updateAccessTime();

        if (!initialized) {
            return null;
        }

        gameState = addMetaInformation(gameState);
        return processEndImpl(gameState);
    }

    private Void processEndImpl(GameState gameState) {
        return gameStrategy.processEnd(gameState);
    }
}

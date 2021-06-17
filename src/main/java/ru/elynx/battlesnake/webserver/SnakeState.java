package ru.elynx.battlesnake.webserver;

import static ru.elynx.battlesnake.entity.MoveCommand.REPEAT_LAST;
import static ru.elynx.battlesnake.entity.MoveCommand.UP;

import java.time.Instant;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Move;
import ru.elynx.battlesnake.entity.MoveCommand;

public class SnakeState {
    private static final int DEFAULT_HAZARD_STEP = 25;

    private final IGameStrategy gameStrategy;
    private boolean initialized;

    private int hazardStep;
    private boolean hazardSeen;

    private MoveCommand lastMove;

    private Instant accessTime;

    SnakeState(IGameStrategy gameStrategy) {
        this.gameStrategy = gameStrategy;
        this.initialized = false;

        this.hazardStep = DEFAULT_HAZARD_STEP;
        this.hazardSeen = false;

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
        HazardPredictor hazardPredictor = makeHazardPredictor(gameState);
        return initializeAndProcessStart(hazardPredictor);
    }

    private HazardPredictor makeHazardPredictor(GameState gameState) {
        return new HazardPredictor(gameState, hazardStep);
    }

    private Void initializeAndProcessStart(HazardPredictor hazardPredictor) {
        initialize(hazardPredictor);
        return processStart(hazardPredictor);
    }

    private void initialize(HazardPredictor hazardPredictor) {
        if (!initialized) {
            gameStrategy.init(hazardPredictor);
            initialized = true;
        }
    }

    private Void processStart(HazardPredictor hazardPredictor) {
        return gameStrategy.processStart(hazardPredictor);
    }

    public Move processMove(GameState gameState) {
        updateAccessTime();
        trackHazard(gameState);
        HazardPredictor hazardPredictor = makeHazardPredictor(gameState);
        Move move = initializeAndProcessMove(hazardPredictor);
        return handleRepeatLastMove(move);
    }

    private void trackHazard(GameState gameState) {
        if (!hazardSeen && !gameState.getBoard().getHazards().isEmpty()) {
            hazardStep = gameState.getTurn();
            hazardSeen = true;
        }
    }

    private Move initializeAndProcessMove(HazardPredictor hazardPredictor) {
        initialize(hazardPredictor);
        return processMove(hazardPredictor);
    }

    private Move processMove(HazardPredictor hazardPredictor) {
        return gameStrategy.processMove(hazardPredictor);
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

        HazardPredictor hazardPredictor = makeHazardPredictor(gameState);
        return processEnd(hazardPredictor);
    }

    private Void processEnd(HazardPredictor hazardPredictor) {
        return gameStrategy.processEnd(hazardPredictor);
    }
}

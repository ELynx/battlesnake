package ru.elynx.battlesnake.webserver;

import static ru.elynx.battlesnake.entity.MoveCommand.REPEAT_LAST;
import static ru.elynx.battlesnake.entity.MoveCommand.UP;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.engine.strategy.IGameStrategyFactory;
import ru.elynx.battlesnake.engine.strategy.SnakeNotFoundException;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Move;
import ru.elynx.battlesnake.entity.MoveCommand;

@Service
public class SnakeManager {
    private static final long STALE_SNAKE_ROUTINE_DELAY = 60000; // milliseconds
    private static final long STALE_SNAKE_AGE = 5000; // milliseconds

    private final Logger logger = LoggerFactory.getLogger(SnakeManager.class);
    private final IGameStrategyFactory gameStrategyFactory;
    private final Map<String, SnakeState> activeSnakes = new ConcurrentHashMap<>();

    @Autowired
    public SnakeManager(IGameStrategyFactory gameStrategyFactory) {
        this.gameStrategyFactory = gameStrategyFactory;
    }

    private BattlesnakeInfo getSnakeInfo(String name) throws SnakeNotFoundException {
        IGameStrategy gameStrategy = gameStrategyFactory.getGameStrategy(name);
        return gameStrategy.getBattesnakeInfo();
    }

    private SnakeState computeSnake(String uid, String nameOnCreation) throws SnakeNotFoundException {
        return activeSnakes.compute(uid, (key, value) -> {
            if (value == null) {
                logger.debug("Creating new [{}] instance [{}]", nameOnCreation, uid);
                return new SnakeState(gameStrategyFactory.getGameStrategy(nameOnCreation));
            }

            logger.debug("Accessing existing snake instance [{}]", uid);
            value.accessTime = Instant.now();
            return value;
        });
    }

    private SnakeState removeSnake(String uid) {
        logger.debug("Releasing snake instance [{}]", uid);
        return activeSnakes.remove(uid);
    }

    @Scheduled(initialDelay = STALE_SNAKE_ROUTINE_DELAY, fixedDelay = STALE_SNAKE_ROUTINE_DELAY)
    private void cleanStaleSnakes() {
        if (activeSnakes.isEmpty()) {
            logger.debug("Cleaning stale snakes, nothing to clean");
            return;
        }

        int sizeBefore = activeSnakes.size();

        Instant staleSnakeTime = Instant.now().minusMillis(STALE_SNAKE_AGE);
        activeSnakes.values().removeIf(meta -> meta.accessTime.isBefore(staleSnakeTime));

        int sizeAfter = activeSnakes.size();

        if (sizeAfter == sizeBefore) {
            logger.debug("Cleaning stale snakes, no stale snakes");
            return;
        }

        int delta = sizeBefore - sizeAfter;
        logger.warn("Cleaning stale snakes, cleaned [{}] snakes older than [{}]", delta, staleSnakeTime);
    }

    public BattlesnakeInfo root(String name) throws SnakeNotFoundException {
        return getSnakeInfo(name);
    }

    public Void start(GameState gameState) throws SnakeNotFoundException {
        SnakeState snakeState = computeSnake(gameState.getYou().getId(), gameState.getYou().getName());
        HazardPredictor hazardPredictor = new HazardPredictor(gameState, snakeState.hazardStep);
        return snakeState.processStart(hazardPredictor);
    }

    public Move move(GameState gameState) throws SnakeNotFoundException {
        final SnakeState snakeState = computeSnake(gameState.getYou().getId(), gameState.getYou().getName());

        // track hazards
        if (!snakeState.hazardSeen && !gameState.getBoard().getHazards().isEmpty()) {
            snakeState.hazardStep = gameState.getTurn();
            snakeState.hazardSeen = true;
        }

        // provide hazards info
        HazardPredictor hazardPredictor = new HazardPredictor(gameState, snakeState.hazardStep);

        final Move move = snakeState.processMove(hazardPredictor);

        // provide last move on request
        if (REPEAT_LAST.equals(move.getMoveCommand())) {
            return new Move(snakeState.lastMove, move.getShout());
        }

        // track last move
        snakeState.lastMove = move.getMoveCommand();

        return move;
    }

    public Void end(GameState gameState) {
        final SnakeState snakeState = removeSnake(gameState.getYou().getId());
        if (snakeState == null) {
            throw new SnakeNotFoundException(gameState.getYou().getName());
        }

        HazardPredictor hazardPredictor = new HazardPredictor(gameState, snakeState.hazardStep);
        return snakeState.processEnd(hazardPredictor);
    }

    private static class SnakeState {
        final IGameStrategy gameStrategy;
        Instant accessTime;
        MoveCommand lastMove;
        int hazardStep;
        boolean hazardSeen;
        boolean initialized;

        SnakeState(IGameStrategy gameStrategy) {
            this.gameStrategy = gameStrategy;
            this.accessTime = Instant.now();
            this.lastMove = UP;
            this.hazardStep = 25; // by default
            this.hazardSeen = false;
            this.initialized = false;
        }

        public Void processStart(HazardPredictor hazardPredictor) {
            if (!initialized) {
                gameStrategy.init(hazardPredictor);
                initialized = true;
            }

            return gameStrategy.processStart(hazardPredictor);
        }

        public Move processMove(HazardPredictor hazardPredictor) {
            if (!initialized) {
                gameStrategy.init(hazardPredictor);
                initialized = true;
            }

            return gameStrategy.processMove(hazardPredictor);
        }

        public Void processEnd(HazardPredictor hazardPredictor) {
            if (!initialized) {
                return null;
            }

            return gameStrategy.processEnd(hazardPredictor);
        }
    }
}

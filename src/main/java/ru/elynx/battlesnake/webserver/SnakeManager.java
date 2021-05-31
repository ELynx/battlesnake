package ru.elynx.battlesnake.webserver;

import static ru.elynx.battlesnake.entity.Move.Moves.UP;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.IGameStrategyFactory;
import ru.elynx.battlesnake.engine.SnakeNotFoundException;
import ru.elynx.battlesnake.engine.predictor.GameStatePredictor;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;
import ru.elynx.battlesnake.entity.Move;

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
        final IGameStrategy tmp = gameStrategyFactory.getGameStrategy(name);
        return tmp.getBattesnakeInfo();
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

        final int sizeBefore = activeSnakes.size();

        final Instant staleSnakeTime = Instant.now().minusMillis(STALE_SNAKE_AGE);
        activeSnakes.values().removeIf(meta -> meta.accessTime.isBefore(staleSnakeTime));

        final int sizeAfter = activeSnakes.size();

        if (sizeAfter == sizeBefore) {
            logger.debug("Cleaning stale snakes, no stale snakes");
            return;
        }

        final int delta = sizeBefore - sizeAfter;
        logger.warn("Cleaning stale snakes, cleaned [{}] snakes older than [{}]", delta, staleSnakeTime);
    }

    public BattlesnakeInfo root(String name) throws SnakeNotFoundException {
        return getSnakeInfo(name);
    }

    public Void start(GameStatePredictor gameState) throws SnakeNotFoundException {
        return computeSnake(gameState.getYou().getId(), gameState.getYou().getName()).processStart(gameState);
    }

    public Move move(GameStatePredictor gameState) throws SnakeNotFoundException {
        final SnakeState snakeState = computeSnake(gameState.getYou().getId(), gameState.getYou().getName());

        // track hazards
        if (!snakeState.hazardSeen && !gameState.getBoard().getHazards().isEmpty()) {
            snakeState.hazardStep = gameState.getTurn();
            snakeState.hazardSeen = true;
        }

        // provide hazards info
        gameState.setHazardStep(snakeState.hazardStep);

        final Move move = snakeState.processMove(gameState);

        // provide last move on request
        if (Boolean.TRUE.equals(move.repeatLast())) {
            return new Move(snakeState.lastMove, move.getShout());
        }

        // track last move
        snakeState.lastMove = move.getMove();

        return move;
    }

    public Void end(GameStatePredictor gameState) {
        final SnakeState snakeState = removeSnake(gameState.getYou().getId());
        if (snakeState == null) {
            throw new SnakeNotFoundException(gameState.getYou().getName());
        }

        return snakeState.processEnd(gameState);
    }

    private static class SnakeState {
        final IGameStrategy gameStrategy;
        Instant accessTime;
        String lastMove;
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

        public Void processStart(GameStatePredictor gameState) {
            if (!initialized) {
                gameStrategy.init(gameState);
                initialized = true;
            }

            return gameStrategy.processStart(gameState);
        }

        public Move processMove(GameStatePredictor gameState) {
            if (!initialized) {
                gameStrategy.init(gameState);
                initialized = true;
            }

            return gameStrategy.processMove(gameState);
        }

        public Void processEnd(GameStatePredictor gameState) {
            if (!initialized) {
                return null;
            }

            return gameStrategy.processEnd(gameState);
        }
    }
}

package ru.elynx.battlesnake.webserver;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.elynx.battlesnake.engine.strategy.IGameStrategy;
import ru.elynx.battlesnake.engine.strategy.IGameStrategyFactory;
import ru.elynx.battlesnake.engine.strategy.SnakeNotFoundException;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Move;

@Service
public class SnakeManager {
    private static final long STALE_SNAKE_STATE_ROUTINE_INTERVAL = 60000; // milliseconds
    private static final long STALE_SNAKE_STATE_AGE = 5000; // milliseconds

    private final Logger logger = LoggerFactory.getLogger(SnakeManager.class);
    private final IGameStrategyFactory gameStrategyFactory;
    private final Map<String, SnakeState> activeSnakes = new ConcurrentHashMap<>();

    @Autowired
    public SnakeManager(IGameStrategyFactory gameStrategyFactory) {
        this.gameStrategyFactory = gameStrategyFactory;
    }

    @Scheduled(initialDelay = STALE_SNAKE_STATE_ROUTINE_INTERVAL, fixedDelay = STALE_SNAKE_STATE_ROUTINE_INTERVAL)
    private void cleanStaleSnakes() {
        cleanStaleSnakesImpl(Instant.now());
    }

    // visible for testing
    void cleanStaleSnakeTest(Instant referenceTime) {
        cleanStaleSnakesImpl(referenceTime);
    }

    private void cleanStaleSnakesImpl(Instant referenceTime) {
        if (activeSnakes.isEmpty()) {
            logger.debug("Cleaning stale snakes, nothing to clean");
            return;
        }

        int sizeBefore = activeSnakes.size();

        Instant staleSnakeTime = referenceTime.minusMillis(STALE_SNAKE_STATE_AGE);
        activeSnakes.entrySet().removeIf(x -> x.getValue().isLastAccessedBefore(staleSnakeTime));

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

    private BattlesnakeInfo getSnakeInfo(String name) throws SnakeNotFoundException {
        return getGameStrategy(name).getBattesnakeInfo();
    }

    private IGameStrategy getGameStrategy(String name) throws SnakeNotFoundException {
        return gameStrategyFactory.getGameStrategy(name);
    }

    public Void start(GameState gameState) throws SnakeNotFoundException {
        return getOrCreateSnakeState(gameState).processStart(gameState);
    }

    public Move move(GameState gameState) throws SnakeNotFoundException {
        return getOrCreateSnakeState(gameState).processMove(gameState);
    }

    private SnakeState getOrCreateSnakeState(GameState gameState) throws SnakeNotFoundException {
        return getOrCreateSnakeState(gameState.getYou().getId(), gameState.getYou().getName());
    }

    private SnakeState getOrCreateSnakeState(String snakeId, String snakeName) throws SnakeNotFoundException {
        return activeSnakes.compute(snakeId, (snakeId1, snakeState) -> {
            if (snakeState == null) {
                logger.debug("Creating new [{}] instance [{}]", snakeName, snakeId1);
                return new SnakeState(getGameStrategy(snakeName));
            }

            logger.debug("Accessing existing snake instance [{}]", snakeId1);
            return snakeState;
        });
    }

    public Void end(GameState gameState) throws SnakeNotFoundException {
        return releaseSnakeState(gameState).processEnd(gameState);
    }

    private SnakeState releaseSnakeState(GameState gameState) throws SnakeNotFoundException {
        SnakeState snakeState = releaseSnakeState(gameState.getYou().getId());
        if (snakeState == null) {
            throw new SnakeNotFoundException(gameState.getYou().getName());
        }

        return snakeState;
    }

    private SnakeState releaseSnakeState(String uid) {
        logger.debug("Releasing snake instance [{}]", uid);
        return activeSnakes.remove(uid);
    }
}

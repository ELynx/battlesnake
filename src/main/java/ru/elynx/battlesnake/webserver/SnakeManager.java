package ru.elynx.battlesnake.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.IGameStrategyFactory;
import ru.elynx.battlesnake.engine.SnakeNotFoundException;
import ru.elynx.battlesnake.protocol.BattlesnakeInfo;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.Move;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SnakeManager {
    private static final long STALE_SNAKE_ROUTINE_DELAY = 60000; // milliseconds
    private static final long STALE_SNAKE_AGE = 5000; // milliseconds
    private final Logger logger = LoggerFactory.getLogger(SnakeManager.class);
    private final IGameStrategyFactory gameStrategyFactory;
    private final Map<String, Snake> activeSnakes = new ConcurrentHashMap<>();

    @Autowired
    public SnakeManager(IGameStrategyFactory gameStrategyFactory) {
        this.gameStrategyFactory = gameStrategyFactory;
    }

    private BattlesnakeInfo getSnakeInfo(String name) throws SnakeNotFoundException {
        final IGameStrategy tmp = gameStrategyFactory.getGameStrategy(name);
        return tmp.getBattesnakeInfo();
    }

    private Snake computeSnake(String uid, String nameOnCreation) throws SnakeNotFoundException {
        return activeSnakes.compute(uid, (key, value) -> {
            if (value == null) {
                logger.debug("Creating new [{}] instance [{}]", nameOnCreation, uid);
                return new Snake(gameStrategyFactory.getGameStrategy(nameOnCreation));
            }

            logger.debug("Accessing existing snake instance [{}]", uid);
            value.accessTime = Instant.now();
            return value;
        });
    }

    private Snake removeSnake(String uid) {
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

    public Void start(GameStateDto gameState) throws SnakeNotFoundException {
        return computeSnake(gameState.getYou().getId(), gameState.getYou().getName()).gameStrategy
                .processStart(gameState);
    }

    public Move move(GameStateDto gameState) throws SnakeNotFoundException {
        return computeSnake(gameState.getYou().getId(), gameState.getYou().getName()).gameStrategy
                .processMove(gameState);
    }

    public Void end(GameStateDto gameState) {
        final Snake snake = removeSnake(gameState.getYou().getId());
        if (snake == null) {
            throw new SnakeNotFoundException(gameState.getYou().getName());
        }

        return snake.gameStrategy.processEnd(gameState);
    }

    private static class Snake {
        final IGameStrategy gameStrategy;
        Instant accessTime;

        Snake(IGameStrategy gameStrategy) {
            this.gameStrategy = gameStrategy;
            this.accessTime = Instant.now();
        }
    }
}

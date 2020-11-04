package ru.elynx.battlesnake.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.IGameStrategyFactory;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.MoveDto;
import ru.elynx.battlesnake.protocol.SnakeConfigDto;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SnakeManager {
    private final static long STALE_SNAKE_ROUTINE_DELAY = 60000; // milliseconds
    private final static long STALE_SNAKE_AGE = 5000; // milliseconds
    private final Logger logger = LoggerFactory.getLogger(SnakeManager.class);
    private final IGameStrategyFactory gameStrategyFactory;
    private final Map<String, Snake> activeSnakes = new ConcurrentHashMap<>();

    @Autowired
    public SnakeManager(IGameStrategyFactory gameStrategyFactory) {
        this.gameStrategyFactory = gameStrategyFactory;
    }

    private Snake getSnake(GameStateDto gameState) {
        final String gameId = gameState.getGame().getId();
        final String snakeId = gameState.getYou().getId();

        return activeSnakes.compute(snakeId, (key, value) -> {
            if (value == null) {
                logger.debug("Creating new snake instance [" + snakeId + "] for game [" + gameId + "]");
                System.out.println("count#snake.manager.new_game=1");
                return new Snake(gameStrategyFactory.alwaysGetGameStrategy(gameState));
            }

            logger.debug("Accessing existing snake instance [" + snakeId + "]");
            value.accessTime = Instant.now();
            return value;
        });
    }

    private Snake releaseSnake(GameStateDto gameState) {
        final String snakeId = gameState.getYou().getId();

        logger.debug("Releasing snake instance [" + snakeId + "]");
        System.out.println("count#snake.manager.end_game=1");
        return activeSnakes.remove(snakeId);
    }

    @Scheduled(initialDelay = STALE_SNAKE_ROUTINE_DELAY, fixedDelay = STALE_SNAKE_ROUTINE_DELAY)
    private void cleanStaleSnakes() {
        if (activeSnakes.isEmpty()) {
            logger.debug("Cleaning stale snakes, nothing to clean");
            System.out.println("count#snake.manager.stale=0");
            return;
        }

        final int sizeBefore = activeSnakes.size();

        final Instant staleSnakeTime = Instant.now().minusMillis(STALE_SNAKE_AGE);
        activeSnakes.values().removeIf(meta -> meta.accessTime.isBefore(staleSnakeTime));

        final int sizeAfter = activeSnakes.size();

        if (sizeAfter == sizeBefore) {
            logger.debug("Cleaning stale snakes, no stale snakes");
            System.out.println("count#snake.manager.stale=0");
            return;
        }

        final int delta = sizeBefore - sizeAfter;
        logger.debug("Cleaning stale snakes, cleaned [" + delta +
                "] snakes older than [" + staleSnakeTime.toString() + "]");
        System.out.println("count#snake.manager.stale=" + delta);
    }

    public SnakeConfigDto start(GameStateDto gameState) {
        return getSnake(gameState).gameStrategy.processStart(gameState);
    }

    public MoveDto move(GameStateDto gameState) {
        return getSnake(gameState).gameStrategy.processMove(gameState);
    }

    public Void end(GameStateDto gameState) {
        Snake snake = releaseSnake(gameState);
        if (snake != null) {
            snake.gameStrategy.processEnd(gameState);
        }

        return null;
    }

    private static class Snake {
        final IGameStrategy gameStrategy;
        final Instant startTime;
        Instant accessTime;

        Snake(IGameStrategy gameStrategy) {
            this.gameStrategy = gameStrategy;
            this.startTime = Instant.now();
            this.accessTime = this.startTime;
        }
    }
}

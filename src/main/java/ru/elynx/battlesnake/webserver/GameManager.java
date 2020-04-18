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
public class GameManager {
    private final static long STALE_GAME_ROUTINE_DELAY = 60000; // milliseconds
    private final static long STALE_GAME_AGE = 5000; // milliseconds
    private final Logger logger = LoggerFactory.getLogger(GameManager.class);
    private final IGameStrategyFactory gameStrategyFactory;
    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();

    @Autowired
    public GameManager(IGameStrategyFactory gameStrategyFactory) {
        this.gameStrategyFactory = gameStrategyFactory;
    }

    private Game getGame(GameStateDto gameState) {
        final String gameId = gameState.getGame().getId();

        return activeGames.compute(gameId, (key, value) -> {
            if (value == null) {
                logger.debug("Creating new game instance for game [" + key + "]");
                System.out.println("count#game.manager.new_game=1");
                return new Game(gameStrategyFactory.makeGameStrategy(gameState));
            }

            logger.debug("Accessing existing game instance for game [" + key + "]");
            value.accessTime = Instant.now();
            return value;
        });
    }

    private Game releaseGame(GameStateDto gameState) {
        final String gameId = gameState.getGame().getId();

        logger.debug("Releasing game instance for game [" + gameId + "]");
        System.out.println("count#game.manager.end_game=1");
        return activeGames.remove(gameId);
    }

    @Scheduled(initialDelay = STALE_GAME_ROUTINE_DELAY, fixedDelay = STALE_GAME_ROUTINE_DELAY)
    private void cleanStaleGames() {
        if (activeGames.isEmpty()) {
            logger.debug("Cleaning stale games, nothing to clean");
            System.out.println("count#game.manager.stale=0");
            return;
        }

        final int sizeBefore = activeGames.size();

        final Instant staleGameTime = Instant.now().minusMillis(STALE_GAME_AGE);
        activeGames.values().removeIf(meta -> meta.accessTime.isBefore(staleGameTime));

        final int sizeAfter = activeGames.size();

        if (sizeAfter == sizeBefore) {
            logger.debug("Cleaning stale games, no stale games");
            System.out.println("count#game.manager.stale=0");
            return;
        }

        final int delta = sizeBefore - sizeAfter;
        logger.debug("Cleaning stale games, cleaned [" + delta +
                "] games older than [" + staleGameTime.toString() + "]");
        System.out.println("count#game.manager.stale=" + delta);
    }

    public SnakeConfigDto start(GameStateDto gameState) {
        return getGame(gameState).gameStrategy.processStart(gameState);
    }

    public MoveDto move(GameStateDto gameState) {
        return getGame(gameState).gameStrategy.processMove(gameState);
    }

    public Void end(GameStateDto gameState) {
        Game game = releaseGame(gameState);
        if (game != null) {
            game.gameStrategy.processEnd(gameState);
        }

        return null;
    }

    private static class Game {
        final IGameStrategy gameStrategy;
        final Instant startTime;
        Instant accessTime;

        Game(IGameStrategy gameStrategy) {
            this.gameStrategy = gameStrategy;
            this.startTime = Instant.now();
            this.accessTime = this.startTime;
        }
    }
}

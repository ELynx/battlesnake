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
                System.out.println("count#game.controller.new_game=1");
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
        System.out.println("count#game.controller.end_game=1");
        return activeGames.remove(gameId);
    }

    @Scheduled(fixedDelay = STALE_GAME_ROUTINE_DELAY)
    private void cleanStaleGames() {
        logger.debug("Cleaning stale games");
        if (activeGames.isEmpty()) {
            logger.debug("Nothing to clean");
            System.out.println("count#game.controller.stale=0");
            return;
        }

        Instant staleGameTime = Instant.now().minusMillis(STALE_GAME_AGE);
        logger.debug("Cleaning games older than [" + staleGameTime.toString() + "]");
        logger.debug("Games before [" + activeGames.size() + "]");
        System.out.println("count#game.controller.stale=" + activeGames.size());
        activeGames.values().removeIf(meta -> meta.accessTime.isBefore(staleGameTime));
        logger.debug("Games after [" + activeGames.size() + "]");
        System.out.println("count#game.controller.stale=-" + activeGames.size());
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
        IGameStrategy gameStrategy;
        Instant startTime;
        Instant accessTime;

        Game(IGameStrategy gameStrategy) {
            this.gameStrategy = gameStrategy;
            this.startTime = Instant.now();
            this.accessTime = this.startTime;
        }
    }
}

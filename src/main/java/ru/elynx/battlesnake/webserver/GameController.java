package ru.elynx.battlesnake.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.IGameStrategyFactory;
import ru.elynx.battlesnake.protocol.GameState;
import ru.elynx.battlesnake.protocol.Move;
import ru.elynx.battlesnake.protocol.SnakeConfig;

import java.io.InvalidObjectException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class GameController {
    private final static long STALE_GAME_ROUTINE_DELAY = 60000; // milliseconds
    private final static long STALE_GAME_AGE = 5000; // milliseconds
    private Logger logger = LoggerFactory.getLogger(GameController.class);
    private IGameStrategyFactory gameStrategyFactory;
    private Map<String, Game> activeGames = new ConcurrentHashMap<>();

    @Autowired
    public GameController(IGameStrategyFactory gameStrategyFactory) {
        this.gameStrategyFactory = gameStrategyFactory;
    }

    private static void ValidateGameState(GameState gameState) throws InvalidObjectException {
        if (GameState.isInvalid(gameState))
            throw new InvalidObjectException("Provided GameState is not valid");
    }

    private Game getGame(GameState gameState) {
        String gameId = gameState.getGame().getId();

        return activeGames.compute(gameId, (key, value) -> {
            if (value == null) {
                logger.debug("Creating new game instance for game [" + key + "]");
                return new Game(gameStrategyFactory.makeGameStrategy(gameState));
            }

            logger.debug("Accessing existing game instance for game [" + key + "]");
            value.accessTime = Instant.now();
            return value;
        });
    }

    private Game releaseGame(GameState gameState) {
        String gameId = gameState.getGame().getId();

        logger.debug("Releasing game instance for game [" + gameId + "]");
        return activeGames.remove(gameId);
    }

    @Scheduled(fixedDelay = STALE_GAME_ROUTINE_DELAY)
    private void cleanStaleGames() {
        logger.debug("Cleaning stale games");
        if (activeGames.isEmpty()) {
            logger.debug("Nothing to clean");
            return;
        }

        Instant staleGameTime = Instant.now().minusMillis(STALE_GAME_AGE);
        logger.debug("Cleaning games older than [" + staleGameTime.toString() + "]");
        logger.debug("Games before [" + activeGames.size() + "]");
        activeGames.values().removeIf(meta -> meta.accessTime.isBefore(staleGameTime));
        logger.debug("Games after [" + activeGames.size() + "]");
    }

    @ExceptionHandler(InvalidObjectException.class)
    public ResponseEntity<String> handleException(InvalidObjectException e) {
        logger.error("Handling", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @PostMapping(path = "/start", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SnakeConfig> start(@RequestBody GameState gameState) throws InvalidObjectException {
        logger.info("Processing request game start");
        ValidateGameState(gameState);
        logger.debug("Game [" + gameState.getGame().getId() + "]");
        return ResponseEntity.ok(getGame(gameState).gameStrategy.processStart(gameState));
    }

    @PostMapping(path = "/move", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Move> move(@RequestBody GameState gameState) throws InvalidObjectException {
        logger.debug("Processing request game move");
        ValidateGameState(gameState);
        logger.debug("Game [" + gameState.getGame().getId() + "]");
        return ResponseEntity.ok(getGame(gameState).gameStrategy.processMove(gameState));
    }

    @PostMapping(path = "/end", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> end(@RequestBody GameState gameState) throws InvalidObjectException {
        logger.info("Processing request game end");
        ValidateGameState(gameState);
        logger.debug("Game [" + gameState.getGame().getId() + "]");

        Game value = releaseGame(gameState);
        if (value != null) {
            value.gameStrategy.processEnd(gameState);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/ping")
    public ResponseEntity<Void> ping() {
        logger.debug("Processing request web ping");
        return ResponseEntity.ok().build();
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

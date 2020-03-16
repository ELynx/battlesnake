package ru.elynx.battlesnake.webserver;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.elynx.battlesnake.engine.IGameEngine;
import ru.elynx.battlesnake.engine.IGameEngineFactory;
import ru.elynx.battlesnake.protocol.GameState;
import ru.elynx.battlesnake.protocol.Move;
import ru.elynx.battlesnake.protocol.SnakeConfig;

import java.io.InvalidObjectException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;

@RestController
public class GameController {
    private Logger logger = LoggerFactory.getLogger(GameController.class);

    private final static long STALE_GAME_ENGINE_ROUTINE_DELAY = 10000; // milliseconds
    private final static long STALE_GAME_ENGINE_AGE = 5000; // milliseconds
    private IGameEngineFactory gameEngineFactory;
    private Map<String, GameEngineWithMeta> gameEngines = new ConcurrentHashMap<>();

    @Autowired
    public GameController(IGameEngineFactory gameEngineFactory) {
        this.gameEngineFactory = gameEngineFactory;
    }

    private static void ValidateGameState(GameState gameState) throws InvalidObjectException {
        if (GameState.isInvalid(gameState))
            throw new InvalidObjectException("Provided GameState is not valid");
    }

    private GameEngineWithMeta getGameEngine(String gameId) {
        return gameEngines.compute(gameId, (key, value) -> {
            if (value == null) {
                logger.info("Creating new game engine instance for game [" + key + "]");
                return new GameEngineWithMeta(gameEngineFactory.makeGameEngine());
            }

            logger.debug("Accessing existing game engine instance for game [" + key + "]");
            value.accessTime = Instant.now();
            return value;
        });
    }

    private GameEngineWithMeta releaseGameEngine(String gameId) {
        logger.info("Releasing game engine instance for game [" + gameId + "]");
        return gameEngines.remove(gameId);
    }

    @Scheduled(fixedDelay = STALE_GAME_ENGINE_ROUTINE_DELAY)
    private void cleanStaleGameEngines() {
        logger.debug("Cleaning stale game engines");
        if (gameEngines.isEmpty()) {
            logger.debug("Nothing to clean");
            return;
        }

        Instant staleGameTime = Instant.now().minusMillis(STALE_GAME_ENGINE_AGE);
        logger.debug("Cleaning game engines older than [" + staleGameTime.toString() + "]");
        logger.debug("Game engines before [" + gameEngines.size() + "]");
        gameEngines.values().removeIf(meta -> meta.accessTime.isBefore(staleGameTime));
        logger.debug("Game engines after [" + gameEngines.size() + "]");
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
        return ResponseEntity.ok(getGameEngine(gameState.getGame().getId()).gameEngine.processStart(gameState));
    }

    @PostMapping(path = "/move", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Move> move(@RequestBody GameState gameState) throws InvalidObjectException {
        logger.debug("Processing request game move");
        ValidateGameState(gameState);
        logger.debug("Game [" + gameState.getGame().getId() + "]");
        return ResponseEntity.ok(getGameEngine(gameState.getGame().getId()).gameEngine.processMove(gameState));
    }

    @PostMapping(path = "/end", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> end(@RequestBody GameState gameState) throws InvalidObjectException {
        logger.info("Processing request game end");
        ValidateGameState(gameState);
        logger.debug("Game [" + gameState.getGame().getId() + "]");

        GameEngineWithMeta value = releaseGameEngine(gameState.getGame().getId());
        if (value != null) {
            value.gameEngine.processEnd(gameState);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/ping")
    public ResponseEntity<Void> ping() {
        logger.debug("Processing request web ping");
        return ResponseEntity.ok().build();
    }

    private static class GameEngineWithMeta {
        IGameEngine gameEngine;
        Instant startTime;
        Instant accessTime;

        GameEngineWithMeta(IGameEngine gameEngine) {
            this.gameEngine = gameEngine;
            this.startTime = Instant.now();
            this.accessTime = this.startTime;
        }
    }
}

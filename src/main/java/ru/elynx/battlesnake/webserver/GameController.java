package ru.elynx.battlesnake.webserver;

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

@RestController
public class GameController {
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
            if (value == null) return new GameEngineWithMeta(gameEngineFactory.makeGameEngine());

            value.accessTime = Instant.now();
            return value;
        });
    }

    private GameEngineWithMeta releaseGameEngine(String gameId) {
        return gameEngines.remove(gameId);
    }

    @Scheduled(fixedDelay = STALE_GAME_ENGINE_ROUTINE_DELAY)
    private void cleanStaleGameEngines() {
        if (gameEngines.isEmpty())
            return;

        Instant staleGameTime = Instant.now().minusMillis(STALE_GAME_ENGINE_AGE);
        gameEngines.values().removeIf(meta -> meta.accessTime.isBefore(staleGameTime));
    }

    @ExceptionHandler(InvalidObjectException.class)
    public ResponseEntity<String> handleException(InvalidObjectException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @PostMapping(path = "/start", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SnakeConfig> start(@RequestBody GameState gameState) throws InvalidObjectException {
        ValidateGameState(gameState);
        return ResponseEntity.ok(getGameEngine(gameState.getGame().getId()).gameEngine.processStart(gameState));
    }

    @PostMapping(path = "/move", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Move> move(@RequestBody GameState gameState) throws InvalidObjectException {
        ValidateGameState(gameState);
        return ResponseEntity.ok(getGameEngine(gameState.getGame().getId()).gameEngine.processMove(gameState));
    }

    @PostMapping(path = "/end", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> end(@RequestBody GameState gameState) throws InvalidObjectException {
        ValidateGameState(gameState);

        GameEngineWithMeta value = releaseGameEngine(gameState.getGame().getId());
        if (value != null) {
            value.gameEngine.processEnd(gameState);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/ping")
    public ResponseEntity<Void> ping() {
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

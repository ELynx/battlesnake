package ru.elynx.battlesnake.webserver;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.elynx.battlesnake.engine.GameEngine;
import ru.elynx.battlesnake.engine.GameEngineFactory;
import ru.elynx.battlesnake.engine.IGameEngine;
import ru.elynx.battlesnake.protocol.GameState;
import ru.elynx.battlesnake.protocol.Move;
import ru.elynx.battlesnake.protocol.SnakeConfig;

import java.io.InvalidObjectException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class GameController {
    private Map<String, IGameEngine> gameEngines = new ConcurrentHashMap<>();

    IGameEngine getGameEngine(String gameId) {
        return gameEngines.computeIfAbsent(gameId, key -> GameEngineFactory.makeGameEngine());
    }

    private static void ValidateGameState(GameState gameState) throws InvalidObjectException {
        if (GameState.isInvalid(gameState))
            throw new InvalidObjectException("Provided GameState is not valid");
    }

    @ExceptionHandler(InvalidObjectException.class)
    public ResponseEntity<String> handleException(InvalidObjectException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @PostMapping(path = "/start", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SnakeConfig> start(@RequestBody GameState gameState) throws InvalidObjectException {
        ValidateGameState(gameState);
        return ResponseEntity.ok(getGameEngine(gameState.getGame().getId()).processStart(gameState));
    }

    @PostMapping(path = "/move", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Move> move(@RequestBody GameState gameState) throws InvalidObjectException {
        ValidateGameState(gameState);
        return ResponseEntity.ok(getGameEngine(gameState.getGame().getId()).processMove(gameState));
    }

    @PostMapping(path = "/end", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> end(@RequestBody GameState gameState) throws InvalidObjectException {
        ValidateGameState(gameState);
        return ResponseEntity.ok(getGameEngine(gameState.getGame().getId()).processEnd(gameState));
    }

    @PostMapping("/ping")
    public ResponseEntity<Void> ping() {
        return ResponseEntity.ok().build();
    }
}

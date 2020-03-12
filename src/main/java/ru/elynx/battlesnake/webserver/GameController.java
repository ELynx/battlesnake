package ru.elynx.battlesnake.webserver;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.elynx.battlesnake.protocol.GameState;
import ru.elynx.battlesnake.protocol.Move;
import ru.elynx.battlesnake.protocol.SnakeConfig;

import java.io.InvalidObjectException;

@RestController
public class GameController {
    static private Move hardcode = new Move("up", "3% ready");

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
        return ResponseEntity.ok(SnakeConfig.DEFAULT_SNAKE_CONFIG);
    }

    @PostMapping(path = "/move", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Move> move(@RequestBody GameState gameState) throws InvalidObjectException {
        ValidateGameState(gameState);
        return ResponseEntity.ok(hardcode);
    }

    @PostMapping(path = "/end", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> end(@RequestBody GameState gameState) throws InvalidObjectException{
        ValidateGameState(gameState);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ping")
    public ResponseEntity<Void> ping() {
        return ResponseEntity.ok().build();
    }
}

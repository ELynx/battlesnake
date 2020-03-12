package ru.elynx.battlesnake.webserver;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.elynx.battlesnake.protocol.GameState;
import ru.elynx.battlesnake.protocol.Move;
import ru.elynx.battlesnake.protocol.SnakeConfig;

@RestController
public class GameController {
    static private Move hardcode = new Move("up", "2% ready");

    @PostMapping("/start")
    public ResponseEntity<SnakeConfig> start(GameState gameState) {
        if (GameState.isInvalid(gameState))
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(SnakeConfig.DEFAULT_SNAKE_CONFIG);
    }

    @PostMapping("/move")
    public ResponseEntity<Move> move(GameState gameState) {
        if (GameState.isInvalid(gameState))
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(hardcode);
    }

    @PostMapping("/end")
    public ResponseEntity<Void> end(GameState gameState) {
        if (GameState.isInvalid(gameState))
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok().build();
    }

    @PostMapping("/ping")
    public ResponseEntity<Void> ping() {
        return ResponseEntity.ok().build();
    }
}

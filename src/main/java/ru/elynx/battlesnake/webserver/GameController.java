package ru.elynx.battlesnake.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.MoveDto;
import ru.elynx.battlesnake.protocol.SnakeConfigDto;

@RestController
public class GameController {
    private final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final GameManager gameManager;
    private final StatisticsTracker statisticsTracker;

    @Autowired
    public GameController(GameManager gameManager, StatisticsTracker statisticsTracker) {
        this.gameManager = gameManager;
        this.statisticsTracker = statisticsTracker;
    }

    // TODO private and test as private
    protected static String terseIdentification(GameStateDto gameState) {
        return "ID [" + (gameState.getGame() == null ? "unknown" : gameState.getGame().getId()) +
                "] Turn [" + (gameState.getTurn() == null ? "unknown" : gameState.getTurn().intValue()) +
                "] Snake [" + (gameState.getYou() == null ? "unknown" : gameState.getYou().getName()) + ']';
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleException(IllegalArgumentException e) {
        logger.error("Handling", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @PostMapping(path = "/start", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SnakeConfigDto> start(@RequestBody GameStateDto gameState) throws IllegalArgumentException {
        logger.info("Processing request game start [" + terseIdentification(gameState) + "]");
        statisticsTracker.start(gameState);
        return ResponseEntity.ok(gameManager.start(gameState));
    }

    @PostMapping(path = "/move", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MoveDto> move(@RequestBody GameStateDto gameState) throws IllegalArgumentException {
        logger.debug("Processing request game move [" + terseIdentification(gameState) + "]");
        statisticsTracker.move(gameState);
        return ResponseEntity.ok(gameManager.move(gameState));
    }

    @PostMapping(path = "/end", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> end(@RequestBody GameStateDto gameState) throws IllegalArgumentException {
        logger.info("Processing request game end [" + terseIdentification(gameState) + "]");
        statisticsTracker.end(gameState);
        return ResponseEntity.ok(gameManager.end(gameState));
    }

    @PostMapping("/ping")
    public ResponseEntity<Void> ping() {
        logger.debug("Processing request web ping");
        statisticsTracker.ping();
        return ResponseEntity.ok().build();
    }
}

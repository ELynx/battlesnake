package ru.elynx.battlesnake.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.MoveDto;
import ru.elynx.battlesnake.protocol.SnakeConfigDto;

import javax.validation.Valid;

@RestController
public class GameController {
    private final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final SnakeManager snakeManager;
    private final StatisticsTracker statisticsTracker;

    @Autowired
    public GameController(SnakeManager snakeManager, StatisticsTracker statisticsTracker) {
        this.snakeManager = snakeManager;
        this.statisticsTracker = statisticsTracker;
    }

    private static String terseIdentification(GameStateDto gameState) {
        return "ID [" + gameState.getGame().getId() +
                "] Turn [" + gameState.getTurn() +
                "] Snake [" + gameState.getYou().getName() + ']';
    }

    @PostMapping(path = "/start", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SnakeConfigDto> start(@RequestBody @Valid GameStateDto gameState) {
        logger.info("Processing request game start " + terseIdentification(gameState));
        statisticsTracker.start(gameState);
        return ResponseEntity.ok(snakeManager.start(gameState));
    }

    @PostMapping(path = "/move", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MoveDto> move(@RequestBody @Valid GameStateDto gameState) {
        logger.debug("Processing request game move " + terseIdentification(gameState));
        statisticsTracker.move(gameState);
        return ResponseEntity.ok(snakeManager.move(gameState));
    }

    @PostMapping(path = "/end", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> end(@RequestBody @Valid GameStateDto gameState) {
        logger.info("Processing request game end " + terseIdentification(gameState));
        statisticsTracker.end(gameState);
        return ResponseEntity.ok(snakeManager.end(gameState));
    }

    @PostMapping("/ping")
    public ResponseEntity<Void> ping() {
        logger.debug("Processing request web ping");
        statisticsTracker.ping();
        return ResponseEntity.ok().build();
    }
}

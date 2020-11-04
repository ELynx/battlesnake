package ru.elynx.battlesnake.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.elynx.battlesnake.protocol.BattlesnakeInfoDto;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.MoveDto;

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

    // TODO consumes?
    @GetMapping(path = "/")
    public ResponseEntity<BattlesnakeInfoDto> root() {
        logger.info("Processing root meta call");
        statisticsTracker.root();
        final String name = "Snake 1"; // FIXME encode in path?
        return ResponseEntity.ok(new BattlesnakeInfoDto(snakeManager.meta(name)));
    }

    @PostMapping(path = "/start", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> start(@RequestBody @Valid GameStateDto gameState) {
        logger.info("Processing request game start " + terseIdentification(gameState));
        statisticsTracker.start(gameState);
        return ResponseEntity.ok(snakeManager.start(gameState));
    }

    @PostMapping(path = "/move", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MoveDto> move(@RequestBody @Valid GameStateDto gameState) {
        logger.debug("Processing request game move " + terseIdentification(gameState));
        statisticsTracker.move(gameState);
        return ResponseEntity.ok(new MoveDto(snakeManager.move(gameState)));
    }

    @PostMapping(path = "/end", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> end(@RequestBody @Valid GameStateDto gameState) {
        logger.info("Processing request game end " + terseIdentification(gameState));
        statisticsTracker.end(gameState);
        return ResponseEntity.ok(snakeManager.end(gameState));
    }
}

package ru.elynx.battlesnake.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.elynx.battlesnake.protocol.BattlesnakeInfoDto;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.MoveDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
    @GetMapping(path = "/battlesnake/api/v1/snake/{name}")
    public ResponseEntity<BattlesnakeInfoDto> root(
            @PathVariable(name = "name")
            @NotNull
            @Pattern(regexp = "[\\w ]+")
                    String name) {
        logger.info("Processing root meta call");
        statisticsTracker.root();
        return ResponseEntity.ok(new BattlesnakeInfoDto(snakeManager.root(name)));
    }

    @PostMapping(path = "/battlesnake/api/v1/snake/{name}/start", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> start(
            @PathVariable(name = "name")
            @NotNull
            @Pattern(regexp = "[\\w ]+")
                    String name,
            @RequestBody @Valid GameStateDto gameState) {
        logger.info("Processing request game start " + terseIdentification(gameState));
        statisticsTracker.start(gameState);
        return ResponseEntity.ok(snakeManager.start(name, gameState));
    }

    @PostMapping(path = "/battlesnake/api/v1/snake/{name}/move", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MoveDto> move(
            @PathVariable(name = "name")
            @NotNull
            @Pattern(regexp = "[\\w ]+")
                    String name,
            @RequestBody @Valid GameStateDto gameState) {
        logger.debug("Processing request game move " + terseIdentification(gameState));
        statisticsTracker.move(gameState);
        return ResponseEntity.ok(new MoveDto(snakeManager.move(name, gameState)));
    }

    @PostMapping(path = "/battlesnake/api/v1/snake/{name}/end", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> end(
            @PathVariable(name = "name")
            @NotNull
            @Pattern(regexp = "[\\w ]+")
                    String name,
            @RequestBody @Valid GameStateDto gameState) {
        logger.info("Processing request game end " + terseIdentification(gameState));
        statisticsTracker.end(gameState);
        return ResponseEntity.ok(snakeManager.end(gameState));
    }
}

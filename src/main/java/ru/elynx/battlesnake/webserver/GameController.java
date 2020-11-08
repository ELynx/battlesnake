package ru.elynx.battlesnake.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import ru.elynx.battlesnake.engine.SnakeNotFoundException;
import ru.elynx.battlesnake.protocol.BattlesnakeInfoDto;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.MoveDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping(value = "/battlesnake/api/v1")
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

    @ExceptionHandler(SnakeNotFoundException.class)
    public final ResponseEntity<Void> handleSnakeNotFoundException
            (SnakeNotFoundException e, WebRequest webRequest) {
        return ResponseEntity.notFound().build();
    }

    @GetMapping(path = "/snakes/{name}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BattlesnakeInfoDto> root(
            @PathVariable
            @NotNull
            @Pattern(regexp = "[\\w ]+")
                    String name) {
        logger.info("Processing root meta call");
        statisticsTracker.root();
        return ResponseEntity.ok(new BattlesnakeInfoDto(snakeManager.root(name)));
    }

    @PostMapping(path = "/snakes/{name}/start", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> start(
            @PathVariable
            @NotNull
            @Pattern(regexp = "[\\w ]+")
                    String name,
            @RequestBody @Valid GameStateDto gameState) {
        logger.info("Processing request game start " + terseIdentification(gameState));
        statisticsTracker.start(gameState);
        if (!name.equals(gameState.getYou().getName())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(snakeManager.start(gameState));
    }

    @PostMapping(path = "/snakes/{name}/move",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MoveDto> move(
            @PathVariable
            @NotNull
            @Pattern(regexp = "[\\w ]+")
                    String name,
            @RequestBody @Valid GameStateDto gameState) {
        logger.debug("Processing request game move " + terseIdentification(gameState));
        statisticsTracker.move(gameState);
        if (!name.equals(gameState.getYou().getName())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new MoveDto(snakeManager.move(gameState)));
    }

    @PostMapping(path = "/snakes/{name}/end", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> end(
            @PathVariable
            @NotNull
            @Pattern(regexp = "[\\w ]+")
                    String name,
            @RequestBody @Valid GameStateDto gameState) {
        logger.info("Processing request game end " + terseIdentification(gameState));
        statisticsTracker.end(gameState);
        if (!name.equals(gameState.getYou().getName())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(snakeManager.end(gameState));
    }
}

package ru.elynx.battlesnake.webserver;

import java.util.Random;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import ru.elynx.battlesnake.engine.SnakeNotFoundException;
import ru.elynx.battlesnake.engine.predictor.GameStatePredictor;
import ru.elynx.battlesnake.protocol.BattlesnakeInfoDto;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.Move;
import ru.elynx.battlesnake.protocol.MoveDto;

@RestController
@RequestMapping(value = "/battlesnake/api/v1")
public class GameController {
    private final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final SnakeManager snakeManager;
    private final StatisticsTracker statisticsTracker;

    private static final HttpStatus[] BAD_HTTP_STATUSES = new HttpStatus[]{HttpStatus.PAYMENT_REQUIRED,
            HttpStatus.NOT_ACCEPTABLE, HttpStatus.REQUEST_TIMEOUT, HttpStatus.GONE, HttpStatus.EXPECTATION_FAILED,
            HttpStatus.I_AM_A_TEAPOT};

    @Autowired
    public GameController(SnakeManager snakeManager, StatisticsTracker statisticsTracker) {
        this.snakeManager = snakeManager;
        this.statisticsTracker = statisticsTracker;
    }

    private static String terseIdentification(GameStateDto gameState) {
        return "ID [" + gameState.getGame().getId() + "] Turn [" + gameState.getTurn() + "] Snake ["
                + gameState.getYou().getName() + "] / [" + gameState.getYou().getId() + ']';
    }

    private static HttpStatus randomBadHttpStatus() {
        final int position = new Random().nextInt(BAD_HTTP_STATUSES.length);
        return BAD_HTTP_STATUSES[position];
    }

    @ExceptionHandler(SnakeNotFoundException.class)
    public final ResponseEntity<Void> handleSnakeNotFoundException(SnakeNotFoundException e, WebRequest webRequest) {
        logger.error("Exception handler for SnakeNotFound", e);
        return ResponseEntity.notFound().build();
    }

    @GetMapping(path = "/snakes/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BattlesnakeInfoDto> root(@PathVariable @NotNull @Pattern(regexp = "[\\w ]+") String name) {
        logger.info("Processing root meta call");
        statisticsTracker.root(name);

        return ResponseEntity.ok(new BattlesnakeInfoDto(snakeManager.root(name)));
    }

    @PostMapping(path = "/snakes/{name}/start", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> start(@PathVariable @NotNull @Pattern(regexp = "[\\w ]+") String name,
            @RequestBody @Valid GameStatePredictor gameState) {
        final String terseId = terseIdentification(gameState);
        logger.info("Processing request game start {}", terseId);
        statisticsTracker.start(gameState);

        if (!name.equals(gameState.getYou().getName())) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(snakeManager.start(gameState));
    }

    @PostMapping(path = "/snakes/{name}/move", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MoveDto> move(@PathVariable @NotNull @Pattern(regexp = "[\\w ]+") String name,
            @RequestBody @Valid GameStatePredictor gameState) {
        final String terseId = terseIdentification(gameState);
        logger.debug("Processing request game move {}", terseId);
        statisticsTracker.move(gameState);

        if (!name.equals(gameState.getYou().getName())) {
            return ResponseEntity.badRequest().build();
        }

        Move move = snakeManager.move(gameState);
        if (Boolean.FALSE.equals(move.getDropRequest())) {
            return ResponseEntity.ok(new MoveDto(move));
        }

        return ResponseEntity.status(randomBadHttpStatus()).build();
    }

    @PostMapping(path = "/snakes/{name}/end", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> end(@PathVariable @NotNull @Pattern(regexp = "[\\w ]+") String name,
            @RequestBody @Valid GameStatePredictor gameState) {
        final String terseId = terseIdentification(gameState);
        logger.info("Processing request game end {}", terseId);
        statisticsTracker.end(gameState);

        if (!name.equals(gameState.getYou().getName())) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(snakeManager.end(gameState));
    }
}

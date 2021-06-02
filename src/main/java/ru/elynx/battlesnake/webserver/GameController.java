package ru.elynx.battlesnake.webserver;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import ru.elynx.battlesnake.api.BattlesnakeInfoDto;
import ru.elynx.battlesnake.api.GameStateDto;
import ru.elynx.battlesnake.api.MoveDto;
import ru.elynx.battlesnake.engine.SnakeNotFoundException;
import ru.elynx.battlesnake.entity.BattlesnakeInfo;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Move;
import ru.elynx.battlesnake.entity.mapping.BattlesnakeInfoMapper;
import ru.elynx.battlesnake.entity.mapping.GameStateMapper;
import ru.elynx.battlesnake.entity.mapping.MoveMapper;

@RestController
@RequestMapping(value = "/battlesnake/api/v1")
public class GameController {
    private final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final SnakeManager snakeManager;
    private final StatisticsTracker statisticsTracker;
    private final BattlesnakeInfoMapper battlesnakeInfoMapper;
    private final GameStateMapper gameStateMapper;
    private final MoveMapper moveMapper;

    @Autowired
    public GameController(SnakeManager snakeManager, StatisticsTracker statisticsTracker,
            BattlesnakeInfoMapper battlesnakeInfoMapper, GameStateMapper gameStateMapper, MoveMapper moveMapper) {
        this.snakeManager = snakeManager;
        this.statisticsTracker = statisticsTracker;
        this.battlesnakeInfoMapper = battlesnakeInfoMapper;
        this.gameStateMapper = gameStateMapper;
        this.moveMapper = moveMapper;
    }

    private static String terseIdentification(GameStateDto gameStateDto) {
        return "ID [" + gameStateDto.getGame().getId() + "] Turn [" + gameStateDto.getTurn() + "] Snake ["
                + gameStateDto.getYou().getName() + "] / [" + gameStateDto.getYou().getId() + ']';
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

        BattlesnakeInfo battlesnakeInfo = snakeManager.root(name);
        return ResponseEntity.ok(battlesnakeInfoMapper.toDto(battlesnakeInfo));
    }

    @PostMapping(path = "/snakes/{name}/start", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> start(@PathVariable @NotNull @Pattern(regexp = "[\\w ]+") String name,
            @RequestBody @Valid GameStateDto gameStateDto) {
        String terseId = terseIdentification(gameStateDto);
        logger.info("Processing request game start {}", terseId);

        GameState gameState = gameStateMapper.toEntity(gameStateDto);

        statisticsTracker.start(gameState);
        if (!name.equals(gameState.getYou().getName())) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(snakeManager.start(gameState));
    }

    @PostMapping(path = "/snakes/{name}/move", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MoveDto> move(@PathVariable @NotNull @Pattern(regexp = "[\\w ]+") String name,
            @RequestBody @Valid GameStateDto gameStateDto) {
        String terseId = terseIdentification(gameStateDto);
        logger.debug("Processing request game move {}", terseId);

        GameState gameState = gameStateMapper.toEntity(gameStateDto);

        statisticsTracker.move(gameState);

        if (!name.equals(gameState.getYou().getName())) {
            return ResponseEntity.badRequest().build();
        }

        Move move = snakeManager.move(gameState);
        return ResponseEntity.ok(moveMapper.toDto(move));
    }

    @PostMapping(path = "/snakes/{name}/end", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> end(@PathVariable @NotNull @Pattern(regexp = "[\\w ]+") String name,
            @RequestBody @Valid GameStateDto gameStateDto) {
        String terseId = terseIdentification(gameStateDto);
        logger.info("Processing request game end {}", terseId);

        GameState gameState = gameStateMapper.toEntity(gameStateDto);

        statisticsTracker.end(gameState);

        if (!name.equals(gameState.getYou().getName())) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(snakeManager.end(gameState));
    }
}

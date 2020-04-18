package ru.elynx.battlesnake.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.elynx.battlesnake.engine.IGameStrategy;
import ru.elynx.battlesnake.engine.IGameStrategyFactory;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.MoveDto;
import ru.elynx.battlesnake.protocol.SnakeConfigDto;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class GameController {
    private final static long STALE_GAME_ROUTINE_DELAY = 60000; // milliseconds
    private final static long STALE_GAME_AGE = 5000; // milliseconds
    private final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final IGameStrategyFactory gameStrategyFactory;
    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();

    @Autowired
    public GameController(IGameStrategyFactory gameStrategyFactory) {
        this.gameStrategyFactory = gameStrategyFactory;
    }

    private static boolean IsGameStateInvalid(GameStateDto gameState) {
        if (gameState == null)
            return true;

        if (gameState.getGame() == null || gameState.getTurn() == null || gameState.getBoard() == null || gameState.getYou() == null)
            return true;

        if (gameState.getBoard().getFood() == null || gameState.getBoard().getSnakes() == null)
            return true;

        return gameState.getGame().getId() == null || gameState.getGame().getId().isEmpty();
    }

    private static void ValidateGameState(GameStateDto gameState) throws IllegalArgumentException {
        if (IsGameStateInvalid(gameState))
            throw new IllegalArgumentException("Provided GameState is not valid");
    }

    // TODO private and test as private
    protected static String terseIdentification(GameStateDto gameState) {
        return "ID [" + (gameState.getGame() == null ? "unknown" : gameState.getGame().getId()) +
                "] Turn [" + (gameState.getTurn() == null ? "unknown" : gameState.getTurn().intValue()) +
                "] Snake [" + (gameState.getYou() == null ? "unknown" : gameState.getYou().getName()) + ']';
    }

    private Game getGame(GameStateDto gameState) {
        final String gameId = gameState.getGame().getId();

        return activeGames.compute(gameId, (key, value) -> {
            if (value == null) {
                logger.debug("Creating new game instance for game [" + key + "]");
                System.out.println("count#game.controller.new_game=1");
                return new Game(gameStrategyFactory.makeGameStrategy(gameState));
            }

            logger.debug("Accessing existing game instance for game [" + key + "]");
            value.accessTime = Instant.now();
            return value;
        });
    }

    private Game releaseGame(GameStateDto gameState) {
        final String gameId = gameState.getGame().getId();

        logger.debug("Releasing game instance for game [" + gameId + "]");
        System.out.println("count#game.controller.end_game=1");
        return activeGames.remove(gameId);
    }

    @Scheduled(fixedDelay = STALE_GAME_ROUTINE_DELAY)
    private void cleanStaleGames() {
        logger.debug("Cleaning stale games");
        if (activeGames.isEmpty()) {
            logger.debug("Nothing to clean");
            System.out.println("count#game.controller.stale=0");
            return;
        }

        Instant staleGameTime = Instant.now().minusMillis(STALE_GAME_AGE);
        logger.debug("Cleaning games older than [" + staleGameTime.toString() + "]");
        logger.debug("Games before [" + activeGames.size() + "]");
        System.out.println("count#game.controller.stale=" + activeGames.size());
        activeGames.values().removeIf(meta -> meta.accessTime.isBefore(staleGameTime));
        logger.debug("Games after [" + activeGames.size() + "]");
        System.out.println("count#game.controller.stale=-" + activeGames.size());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleException(IllegalArgumentException e) {
        logger.error("Handling", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @PostMapping(path = "/start", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SnakeConfigDto> start(@RequestBody GameStateDto gameState) throws IllegalArgumentException {
        logger.info("Processing request game start");
        ValidateGameState(gameState);
        logger.info("Game [" + terseIdentification(gameState) + "]");
        System.out.println("count#game.controller.start=1");
        return ResponseEntity.ok(getGame(gameState).gameStrategy.processStart(gameState));
    }

    @PostMapping(path = "/move", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MoveDto> move(@RequestBody GameStateDto gameState) throws IllegalArgumentException {
        logger.debug("Processing request game move");
        ValidateGameState(gameState);
        logger.debug("Game [" + terseIdentification(gameState) + "]");
        System.out.println("count#game.controller.move=1");
        return ResponseEntity.ok(getGame(gameState).gameStrategy.processMove(gameState));
    }

    @PostMapping(path = "/end", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> end(@RequestBody GameStateDto gameState) throws IllegalArgumentException {
        logger.info("Processing request game end");
        ValidateGameState(gameState);
        logger.info("Game [" + terseIdentification(gameState) + "]");
        System.out.println("count#game.controller.end=1");

        Game game = releaseGame(gameState);
        if (game != null) {
            game.gameStrategy.processEnd(gameState);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/ping")
    public ResponseEntity<Void> ping() {
        logger.debug("Processing request web ping");
        System.out.println("count#game.controller.ping=1");
        return ResponseEntity.ok().build();
    }

    private static class Game {
        IGameStrategy gameStrategy;
        Instant startTime;
        Instant accessTime;

        Game(IGameStrategy gameStrategy) {
            this.gameStrategy = gameStrategy;
            this.startTime = Instant.now();
            this.accessTime = this.startTime;
        }
    }
}

package ru.elynx.battlesnake.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/utility")
public class UtilityController {
    private final Logger logger = LoggerFactory.getLogger(UtilityController.class);
    private final StatisticsTracker statisticsTracker;

    @Autowired
    public UtilityController(StatisticsTracker statisticsTracker) {
        this.statisticsTracker = statisticsTracker;
    }

    @GetMapping(path = "/status", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> status() {
        logger.info("Processing status call");
        statisticsTracker.ping();

        final long statusPings = statisticsTracker.getPings();
        final long snakePings = statisticsTracker.getRootCalls();
        final long snakeStarts = statisticsTracker.getStartCalls();
        final long snakeMoves = statisticsTracker.getMoveCalls();
        final long snakeEnds = statisticsTracker.getEndCalls();
        final long snakeVictories = statisticsTracker.getVictories();
        final long snakeDefeats = statisticsTracker.getDefeats();

        final long mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();

        final long freeMemory = runtime.freeMemory() / mb;
        final long totalMemory = runtime.totalMemory() / mb;

        String response = String.format(
                "Status pings %s%nSnake pings %s%nSnake start calls %s%nSnake move calls %s%nSnake end calls %s%nSnake victories %s%nSnake defeats %s%n Free memory, MB %s%n Total memory, MB %s",
                statusPings, snakePings, snakeStarts, snakeMoves, snakeEnds, snakeVictories, snakeDefeats, freeMemory,
                totalMemory);

        return ResponseEntity.ok(response);
    }
}

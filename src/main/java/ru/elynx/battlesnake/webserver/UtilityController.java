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

        String status = makeStatus();
        return ResponseEntity.ok(status);
    }

    private String makeStatus() {
        long statusPings = statisticsTracker.getPings();

        long mb = 1024L * 1024L;
        Runtime runtime = Runtime.getRuntime();

        long freeMemory = runtime.freeMemory() / mb;
        long totalMemory = runtime.totalMemory() / mb;

        return String.format("Status pings %s%nFree memory, MB %s%nTotal memory, MB %s", statusPings, freeMemory,
                totalMemory);
    }
}

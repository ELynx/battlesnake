package ru.elynx.battlesnake.webserver;

import com.newrelic.api.agent.NewRelic;
import java.util.Optional;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.Snake;

@Service
@Scope("singleton")
class StatisticsTracker {
    private static final String SNAKE_NAME_PARAMETER = "snakeName";
    private static final String SNAKE_HEALTH_PARAMETER = "snakeHealth";
    private static final String SNAKE_LENGTH_PARAMETER = "snakeLength";
    private static final String SNAKE_LATENCY = "snakeLatency";
    private static final String SNAKE_TIMED_OUT = "snakeTimedOut";
    private static final String RULESET_NAME_PARAMETER = "rulesetName";
    private static final String RULESET_VERSION_PARAMETER = "rulesetVersion";
    private static final String RULESET_TIMEOUT_PARAMETER = "rulesetTimeout";
    private static final String VICTORY_PARAMETER = "victory";
    private static final String TURNS_TO_END_PARAMETER = "turnsToEnd";
    private static final String PING_PARAMETER = "pings";

    private long pings = 0L;

    public void root(String name) {
        NewRelic.addCustomParameter(SNAKE_NAME_PARAMETER, name);
    }

    public void start(GameState gameState) {
        common(gameState);
    }

    private void common(GameState gameState) {
        snakeInfo(gameState);
        rulesetInfo(gameState);
    }

    private void snakeInfo(GameState gameState) {
        NewRelic.addCustomParameter(SNAKE_NAME_PARAMETER, gameState.getYou().getName());
        NewRelic.addCustomParameter(SNAKE_HEALTH_PARAMETER, gameState.getYou().getHealth());
        NewRelic.addCustomParameter(SNAKE_LENGTH_PARAMETER, gameState.getYou().getLength());
        NewRelic.addCustomParameter(SNAKE_LATENCY, Optional.ofNullable(gameState.getYou().getLatency()).orElse(0));
        NewRelic.addCustomParameter(SNAKE_TIMED_OUT, gameState.getYou().isTimedOut());
    }

    private void rulesetInfo(GameState gameState) {
        NewRelic.addCustomParameter(RULESET_NAME_PARAMETER, gameState.getRules().getName());
        NewRelic.addCustomParameter(RULESET_VERSION_PARAMETER, gameState.getRules().getVersion());
        NewRelic.addCustomParameter(RULESET_TIMEOUT_PARAMETER, gameState.getRules().getTimeout());
    }

    public void move(GameState gameState) {
        common(gameState);
    }

    public void end(GameState gameState) {
        common(gameState);
        victory(gameState);
        turnsToEnd(gameState);
    }

    private void victory(GameState gameState) {
        boolean victory = false;
        for (Snake someSnake : gameState.getBoard().getSnakes()) {
            if (someSnake.getId().equals(gameState.getYou().getId())) {
                victory = true;
                break;
            }
        }

        NewRelic.addCustomParameter(VICTORY_PARAMETER, victory);
    }

    private void turnsToEnd(GameState gameState) {
        NewRelic.addCustomParameter(TURNS_TO_END_PARAMETER, gameState.getTurn());
    }

    public void ping() {
        ++pings;
        NewRelic.addCustomParameter(PING_PARAMETER, pings);
    }

    public long getPings() {
        return pings;
    }
}

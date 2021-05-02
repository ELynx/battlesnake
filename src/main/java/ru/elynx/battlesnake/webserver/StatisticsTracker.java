package ru.elynx.battlesnake.webserver;

import com.newrelic.api.agent.NewRelic;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

@Service
@Scope("singleton")
class StatisticsTracker {
    protected static final String SNAKE_NAME_PARAMETER = "snakeName";
    protected static final String RULESET_NAME_PARAMETER = "rulesetName";
    protected static final String RULESET_VERSION_PARAMETER = "rulesetVersion";
    protected static final String RULESET_TIMEOUT_PARAMETER = "rulesetTimeout";
    protected static final String TIMEOUT_REPORTED_PARAMETER = "timeoutReported";
    protected static final String VICTORY_PARAMETER = "victory";
    protected static final String TURNS_TO_END_PARAMETER = "turnsToEnd";
    protected static final String PING_PARAMETER = "pings";

    private long pings = 0L;

    public void root(String name) {
        NewRelic.addCustomParameter(SNAKE_NAME_PARAMETER, name);
    }

    private void always(GameStateDto gameState) {
        NewRelic.addCustomParameter(SNAKE_NAME_PARAMETER, gameState.getYou().getName());

        NewRelic.addCustomParameter(RULESET_NAME_PARAMETER, gameState.getGame().getRuleset().getName());
        NewRelic.addCustomParameter(RULESET_VERSION_PARAMETER, gameState.getGame().getRuleset().getVersion());
        NewRelic.addCustomParameter(RULESET_TIMEOUT_PARAMETER, gameState.getGame().getTimeout());
    }

    public void start(GameStateDto gameState) {
        always(gameState);
    }

    public void move(GameStateDto gameState) {
        always(gameState);

        NewRelic.addCustomParameter(TIMEOUT_REPORTED_PARAMETER, gameState.getYou().isTimedOut());
    }

    public void end(GameStateDto gameState) {
        always(gameState);

        boolean victory = false;
        for (SnakeDto someSnake : gameState.getBoard().getSnakes()) {
            if (someSnake.getId().equals(gameState.getYou().getId())) {
                victory = true;
                break;
            }
        }

        NewRelic.addCustomParameter(VICTORY_PARAMETER, victory);
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

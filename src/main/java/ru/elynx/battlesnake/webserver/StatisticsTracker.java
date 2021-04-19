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

    private long pings = 0L;

    public void root(String name) {
        NewRelic.addCustomParameter(SNAKE_NAME_PARAMETER, name);
    }

    public void start(GameStateDto gameState) {
        NewRelic.addCustomParameter(SNAKE_NAME_PARAMETER, gameState.getYou().getName());
    }

    public void move(GameStateDto gameState) {
        NewRelic.addCustomParameter(SNAKE_NAME_PARAMETER, gameState.getYou().getName());
        NewRelic.addCustomParameter("timeoutReported", gameState.getYou().isTimedOut());
    }

    public void end(GameStateDto gameState) {
        boolean victory = false;
        for (SnakeDto someSnake : gameState.getBoard().getSnakes()) {
            if (someSnake.getId().equals(gameState.getYou().getId())) {
                victory = true;
                break;
            }
        }

        NewRelic.addCustomParameter(SNAKE_NAME_PARAMETER, gameState.getYou().getName());
        NewRelic.addCustomParameter("victory", victory);
    }

    public void ping() {
        ++pings;
    }

    public long getPings() {
        return pings;
    }
}

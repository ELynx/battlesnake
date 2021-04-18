package ru.elynx.battlesnake.webserver;

import com.newrelic.api.agent.NewRelic;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

@Service
@Scope("singleton")
class StatisticsTracker {
    protected final static String SnakeNameParameter = "snakeName";

    private long pings = 0L;

    public void root(String name) {
        NewRelic.addCustomParameter(SnakeNameParameter, name);
    }

    public void start(GameStateDto gameState) {
        NewRelic.addCustomParameter(SnakeNameParameter, gameState.getYou().getName());
    }

    public void move(GameStateDto gameState) {
        NewRelic.addCustomParameter(SnakeNameParameter, gameState.getYou().getName());
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

        NewRelic.addCustomParameter(SnakeNameParameter, gameState.getYou().getName());
        NewRelic.addCustomParameter("victory", victory);
    }

    public void ping() {
        ++pings;
    }

    public long getPings() {
        return pings;
    }
}

package ru.elynx.battlesnake.webserver;

import com.newrelic.api.agent.NewRelic;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

@Service
@Scope("singleton")
class StatisticsTracker {
    private long rootCalls = 0L;
    private long startCalls = 0L;
    private long moveCalls = 0L;
    private long endCalls = 0L;
    private long victories = 0L;
    private long defeats = 0L;
    private long pings = 0L;
    private long timeouts = 0L;

    public void root() {
        ++rootCalls;
    }

    public void start(GameStateDto gameState) {
        ++startCalls;
    }

    public void move(GameStateDto gameState) {
        ++moveCalls;
    }

    public void end(GameStateDto gameState) {
        boolean victory = false;
        for (SnakeDto someSnake : gameState.getBoard().getSnakes()) {
            if (someSnake.getId().equals(gameState.getYou().getId())) {
                victory = true;
                break;
            }
        }

        ++endCalls;

        if (victory)
            ++victories;
        else
            ++defeats;

        NewRelic.addCustomParameter("victory", victory);
    }

    public void ping() {
        ++pings;
    }

    public void timeout() {
        ++timeouts;
    }

    public long getRootCalls() {
        return rootCalls;
    }

    public long getStartCalls() {
        return startCalls;
    }

    public long getMoveCalls() {
        return moveCalls;
    }

    public long getEndCalls() {
        return endCalls;
    }

    public long getVictories() {
        return victories;
    }

    public long getDefeats() {
        return defeats;
    }

    public long getPings() {
        return pings;
    }

    public long getTimeouts() {
        return timeouts;
    }
}

package ru.elynx.battlesnake.webserver;

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
    private long wins = 0L;
    private long loses = 0L;
    private long pings = 0L;

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
        final String snakeName = gameState.getYou().getName().replace(' ', '_').trim();
        boolean victory = false;
        for (SnakeDto someSnake : gameState.getBoard().getSnakes()) {
            if (someSnake.getId().equals(gameState.getYou().getId())) {
                victory = true;
                break;
            }
        }
        final int turnsToEnd = gameState.getTurn();

        System.out.println("source=" + snakeName + " measure#" + (victory ? "win" : "lose") + "=" + turnsToEnd);

        ++endCalls;
        if (victory)
            ++wins;
        else
            ++loses;
    }

    public void ping() {
        ++pings;
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

    public long getWins() {
        return wins;
    }

    public long getLoses() {
        return loses;
    }

    public long getPings() {
        return pings;
    }
}

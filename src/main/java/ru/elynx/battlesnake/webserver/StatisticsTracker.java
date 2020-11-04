package ru.elynx.battlesnake.webserver;

import org.springframework.stereotype.Service;
import ru.elynx.battlesnake.protocol.GameStateDto;

@Service
class StatisticsTracker {
    public void root() {
    }

    public void start(GameStateDto gameState) {
    }

    public void move(GameStateDto gameState) {
    }

    public void end(GameStateDto gameState) {
        final String snakeName = gameState.getYou().getName().replace(' ', '_').trim();
        final boolean victory = gameState.getBoard().getSnakes().size() == 1 &&
                gameState.getBoard().getSnakes().get(0).getId() == gameState.getYou().getId();
        final int turnsToEnd = gameState.getTurn();

        System.out.println("source=" + snakeName + " measure#" + (victory ? "win" : "lose") + "=" + turnsToEnd);
    }
}

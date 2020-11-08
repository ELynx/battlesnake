package ru.elynx.battlesnake.webserver;

import org.springframework.stereotype.Service;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.SnakeDto;

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
        boolean victory = false;
        for (SnakeDto someSnake : gameState.getBoard().getSnakes()) {
            if (someSnake.getId().equals(gameState.getYou().getId())) {
                victory = true;
                break;
            }
        }
        final int turnsToEnd = gameState.getTurn();

        System.out.println("source=" + snakeName + " measure#" + (victory ? "win" : "lose") + "=" + turnsToEnd);
    }
}

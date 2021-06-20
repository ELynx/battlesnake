package ru.elynx.battlesnake.engine.advancer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.elynx.battlesnake.entity.CoordinatesWithDirection;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.MoveCommand;
import ru.elynx.battlesnake.entity.Snake;

public class GameStateAdvancerProbe {
    private GameStateAdvancerProbe() {
    }

    public static boolean probeGameStateChange(GameState gameState0, GameState gameState1) {
        Map<String, MoveCommand> determinedMoves = new HashMap<>();
        List<String> moveNotFound = new ArrayList<>();

        // make snake moves
        for (Snake snake0 : gameState0.getBoard().getSnakes()) {
            boolean found = false;
            for (Snake snake1 : gameState1.getBoard().getSnakes()) {
                if (snake0.getId().equals(snake1.getId())) {
                    for (CoordinatesWithDirection coordinates : snake0.getHead().sideNeighbours()) {
                        if (coordinates.equals(snake1.getHead())) {
                            determinedMoves.put(snake0.getId(), coordinates.getDirection());
                            found = true;
                        }
                    }

                    if (!found) {
                        return false; // if neighbours do not predict movement something is broken
                    }
                }
            }
            if (!found) {
                moveNotFound.add(snake0.getId());
            }
        }

        return true;
    }
}

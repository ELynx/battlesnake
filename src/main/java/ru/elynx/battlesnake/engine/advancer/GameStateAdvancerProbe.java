package ru.elynx.battlesnake.engine.advancer;

import ru.elynx.battlesnake.entity.GameState;

public class GameStateAdvancerProbe {
    private GameStateAdvancerProbe() {
    }

    public static boolean probeGameStateChange(GameState gameState0, GameState gameState1) {
        return true;
    }
}

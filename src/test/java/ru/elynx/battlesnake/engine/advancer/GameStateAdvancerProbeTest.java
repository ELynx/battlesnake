package ru.elynx.battlesnake.engine.advancer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.asciitest.AsciiToGameState;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.entity.GameState;

class GameStateAdvancerProbeTest {
    @Test
    void test_basic_valid() {
        HazardPredictor entity1 = new AsciiToGameState("" + //
                "yy_\n" + //
                "_Y_\n" + //
                "0__\n").setTurn(42).setHealth("Y", 25).build();
        GameState gameState0 = entity1.getGameState();

        HazardPredictor entity2 = new AsciiToGameState("" + //
                "_y_\n" + //
                "_yY\n" + //
                "0__\n").setTurn(43).setHealth("Y", 24).build();
        GameState gameState1 = entity2.getGameState();

        assertTrue(GameStateAdvancerProbe.probeGameStateChange(gameState0, gameState1));
    }

    @Test
    void test_basic_invalid() {
        HazardPredictor entity1 = new AsciiToGameState("" + //
                "yy_\n" + //
                "_Y_\n" + //
                "0__\n").setTurn(42).setHealth("Y", 25).build();
        GameState gameState0 = entity1.getGameState();

        HazardPredictor entity2 = new AsciiToGameState("" + //
                "_y_\n" + //
                "_yY\n" + //
                "___\n").setTurn(43).setLength("Y", 4).setHealth("Y", 100).build();
        GameState gameState1 = entity2.getGameState();

        assertFalse(GameStateAdvancerProbe.probeGameStateChange(gameState0, gameState1));
    }
}

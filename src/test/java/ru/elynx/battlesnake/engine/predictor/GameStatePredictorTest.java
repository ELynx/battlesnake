package ru.elynx.battlesnake.engine.predictor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.asciitest.AsciiToGameState;

class GameStatePredictorTest {
    private static final double fuzz = 0.0001d;

    @Test
    void test_is_growing() {
        AsciiToGameState generator = new AsciiToGameState("__Y__");
        GameStatePredictor tested;

        tested = (GameStatePredictor) generator.build();
        assertFalse(tested.isGrowing(tested.getYou()), "Not growing under normal conditions");

        for (int turn = 0; turn < 3; ++turn) {
            tested = (GameStatePredictor) generator.setTurn(turn).build();
            assertTrue(tested.isGrowing(tested.getYou()), "Grows on turn [" + turn + ']');
        }

        tested = (GameStatePredictor) generator.setTurn(123).setHealth("Y", 100).build();

        assertTrue(tested.isGrowing(tested.getYou()), "Grows on food");
    }

    @Test
    void test_predict_hazard_not_that_mode() {
        AsciiToGameState generator = new AsciiToGameState("_____\n_____\n__Y__\n_____\n_____");
        GameStatePredictor tested;

        // default state, no hazard step set
        tested = (GameStatePredictor) generator.build();
        assertEquals(0, tested.getPredictedHazards().size(), "No predicted hazards by default");

        // set mode, not set step
        tested = (GameStatePredictor) generator.setRulesetName("royale").build();
        assertEquals(0, tested.getPredictedHazards().size(), "No predicted hazards");

        // set step, not set mode
        tested = (GameStatePredictor) generator.setRulesetName("standard").build();
        tested.setHazardStep(25);
        assertEquals(0, tested.getPredictedHazards().size(), "No predicted hazards");
    }

    @Test
    void test_predict_hazard_already_full() {
        AsciiToGameState generator = new AsciiToGameState("_____\n_____\n__Y__\n_____\n_____").setRulesetName("royale")
                .setTurn(24);
        GameStatePredictor tested;

        tested = (GameStatePredictor) generator.setHazards("HHHHH\nHHHHH\nHH_HH\nHHHHH\nHHHHH").build();
        tested.setHazardStep(25);

        assertEquals(24, tested.getBoard().getHazards().size());
        assertEquals(4, tested.getPredictedHazards().size());

        for (int i = 0; i < 4; ++i) {
            assertEquals(2, tested.getPredictedHazards().get(i).getValue0());
            assertEquals(2, tested.getPredictedHazards().get(i).getValue1());
            assertThat(tested.getPredictedHazards().get(i).getValue2(), is(closeTo(0.25d, fuzz)));
        }

        tested = (GameStatePredictor) generator.setHazards("HHHHH\nHHHHH\nHHHHH\nHHHHH\nHHHHH").build();
        tested.setHazardStep(25);

        assertEquals(25, tested.getBoard().getHazards().size());
        assertEquals(0, tested.getPredictedHazards().size());
    }

    @Test
    void test_predict_hazard_not_that_turn() {
        AsciiToGameState generator = new AsciiToGameState("_____\n_____\n__Y__\n_____\n_____").setRulesetName("royale")
                .setTurn(23);
        GameStatePredictor tested;

        tested = (GameStatePredictor) generator.setHazards("HHHHH\nHHHHH\nHH_HH\nHHHHH\nHHHHH").build();
        tested.setHazardStep(25);

        assertEquals(24, tested.getBoard().getHazards().size());
        assertEquals(0, tested.getPredictedHazards().size());
    }

    @Test
    void test_predict_hazard() {
        AsciiToGameState generator = new AsciiToGameState("_____\n_____\n__Y__\n_____\n_____").setRulesetName("royale")
                .setTurn(24);
        GameStatePredictor tested;

        tested = (GameStatePredictor) generator.setHazards("HHHHH\nHHHHH\nHH_HH\nHHHHH\nHHHHH").build();
        tested.setHazardStep(25);

        assertEquals(24, tested.getBoard().getHazards().size());
        assertEquals(4, tested.getPredictedHazards().size());

        tested = (GameStatePredictor) generator.setHazards("HHHHH\n_____\n_____\n_____\n_____").build();
        tested.setHazardStep(25);

        assertEquals(5, tested.getBoard().getHazards().size());
        assertEquals(18, tested.getPredictedHazards().size());

        tested = (GameStatePredictor) generator.setHazards("HHHHH\nH___H\nH___H\nH___H\nHHHHH").build();
        tested.setHazardStep(25);

        assertEquals(16, tested.getBoard().getHazards().size());
        assertEquals(12, tested.getPredictedHazards().size());
    }
}

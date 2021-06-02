package ru.elynx.battlesnake.engine.predictor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.asciitest.AsciiToGameState;

@Tag("Internals")
class HazardPredictorTest {
    private static final String ROYALE_RULES_NAME = "royale";
    private static final double fuzz = 0.0001d;

    @Test
    void test_predict_hazard_not_that_mode() {
        AsciiToGameState generator = new AsciiToGameState("_____\n_____\n__Y__\n_____\n_____");
        HazardPredictor tested;

        // default state, no hazard step set
        tested = generator.build();
        assertEquals(0, tested.getPredictedHazards().size(), "No predicted hazards by default");

        // set mode, not set step
        tested = generator.setRulesetName(ROYALE_RULES_NAME).build();
        assertEquals(0, tested.getPredictedHazards().size(), "No predicted hazards");

        // set step, not set mode
        tested = generator.setRulesetName("standard").setHazardStep(25).build();
        assertEquals(0, tested.getPredictedHazards().size(), "No predicted hazards");
    }

    @Test
    void test_predict_hazard_already_full() {
        AsciiToGameState generator = new AsciiToGameState("_____\n_____\n__Y__\n_____\n_____")
                .setRulesetName(ROYALE_RULES_NAME).setTurn(24);
        HazardPredictor tested;

        tested = generator.setHazards("HHHHH\nHHHHH\nHH_HH\nHHHHH\nHHHHH").setHazardStep(25).build();

        assertEquals(24, tested.getGameState().getBoard().getHazards().size());
        assertEquals(1, tested.getPredictedHazards().size());

        assertEquals(2, tested.getPredictedHazards().get(0).getValue0());
        assertEquals(2, tested.getPredictedHazards().get(0).getValue1());
        assertThat(tested.getPredictedHazards().get(0).getValue2(), is(closeTo(1.0d, fuzz)));

        tested = generator.setHazards("HHHHH\nHHHHH\nHHHHH\nHHHHH\nHHHHH").setHazardStep(25).build();

        assertEquals(25, tested.getGameState().getBoard().getHazards().size());
        assertEquals(0, tested.getPredictedHazards().size());
    }

    @Test
    void test_predict_hazard_not_that_turn() {
        AsciiToGameState generator = new AsciiToGameState("_____\n_____\n__Y__\n_____\n_____")
                .setRulesetName(ROYALE_RULES_NAME).setTurn(23);
        HazardPredictor tested;

        tested = generator.setHazards("HHHHH\nHHHHH\nHH_HH\nHHHHH\nHHHHH").setHazardStep(25).build();

        assertEquals(24, tested.getGameState().getBoard().getHazards().size());
        assertEquals(0, tested.getPredictedHazards().size());
    }

    @Test
    void test_predict_hazard() {
        AsciiToGameState generator = new AsciiToGameState("_____\n_____\n__Y__\n_____\n_____")
                .setRulesetName(ROYALE_RULES_NAME).setTurn(24);
        HazardPredictor tested;

        tested = generator.setHazards("HHHHH\nHHHHH\nHH_HH\nHHHHH\nHHHHH").setHazardStep(25).build();

        assertEquals(24, tested.getGameState().getBoard().getHazards().size());
        assertEquals(1, tested.getPredictedHazards().size());
        assertEquals(2, tested.getPredictedHazards().get(0).getValue0());
        assertEquals(2, tested.getPredictedHazards().get(0).getValue1());
        assertThat(tested.getPredictedHazards().get(0).getValue2(), is(closeTo(1.0d, fuzz)));

        tested = generator.setHazards("HHHHH\n_____\n_____\n_____\n_____").setHazardStep(25).build();

        assertEquals(5, tested.getGameState().getBoard().getHazards().size());
        assertEquals(14, tested.getPredictedHazards().size());

        tested = generator.setHazards("HHHHH\nH___H\nH___H\nH___H\nHHHHH").setHazardStep(25).build();

        assertEquals(16, tested.getGameState().getBoard().getHazards().size());
        assertEquals(8, tested.getPredictedHazards().size());
    }
}

package ru.elynx.battlesnake.engine.predictor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static ru.elynx.battlesnake.api.RulesetDto.ROYALE_RULESET_NAME;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.asciitest.AsciiToGameState;

@Tag("Internals")
class HazardPredictorTest {
    private static final double fuzz = 0.0001d;

    @Test
    void test_is_growing() {
        AsciiToGameState generator = new AsciiToGameState("__Y__");
        HazardPredictor tested;

        tested = generator.build();
        assertFalse(tested.isGrowing(tested.getYou()), "Not growing under normal conditions");

        for (int turn = 0; turn < 3; ++turn) {
            tested = generator.setTurn(turn).build();
            assertTrue(tested.isGrowing(tested.getYou()), "Grows on turn [" + turn + ']');
        }

        tested = generator.setTurn(123).setHealth("Y", 100).build();

        assertTrue(tested.isGrowing(tested.getYou()), "Grows on food");
    }

    @Test
    void test_predict_hazard_not_that_mode() {
        AsciiToGameState generator = new AsciiToGameState("_____\n_____\n__Y__\n_____\n_____");
        HazardPredictor tested;

        // default state, no hazard step set
        tested = generator.build();
        assertEquals(0, tested.getPredictedHazards().size(), "No predicted hazards by default");

        // set mode, not set step
        tested = generator.setRulesetName(ROYALE_RULESET_NAME).build();
        assertEquals(0, tested.getPredictedHazards().size(), "No predicted hazards");

        // set step, not set mode
        tested = generator.setRulesetName("standard").build();
        tested.setHazardStep(25);
        assertEquals(0, tested.getPredictedHazards().size(), "No predicted hazards");
    }

    @Test
    void test_predict_hazard_already_full() {
        AsciiToGameState generator = new AsciiToGameState("_____\n_____\n__Y__\n_____\n_____")
                .setRulesetName(ROYALE_RULESET_NAME).setTurn(24);
        HazardPredictor tested;

        tested = generator.setHazards("HHHHH\nHHHHH\nHH_HH\nHHHHH\nHHHHH").build();
        tested.setHazardStep(25);

        assertEquals(24, tested.getBoard().getHazards().size());
        assertEquals(1, tested.getPredictedHazards().size());

        assertEquals(2, tested.getPredictedHazards().get(0).getValue0());
        assertEquals(2, tested.getPredictedHazards().get(0).getValue1());
        assertThat(tested.getPredictedHazards().get(0).getValue2(), is(closeTo(1.0d, fuzz)));

        tested = generator.setHazards("HHHHH\nHHHHH\nHHHHH\nHHHHH\nHHHHH").build();
        tested.setHazardStep(25);

        assertEquals(25, tested.getBoard().getHazards().size());
        assertEquals(0, tested.getPredictedHazards().size());
    }

    @Test
    void test_predict_hazard_not_that_turn() {
        AsciiToGameState generator = new AsciiToGameState("_____\n_____\n__Y__\n_____\n_____")
                .setRulesetName(ROYALE_RULESET_NAME).setTurn(23);
        HazardPredictor tested;

        tested = generator.setHazards("HHHHH\nHHHHH\nHH_HH\nHHHHH\nHHHHH").build();
        tested.setHazardStep(25);

        assertEquals(24, tested.getBoard().getHazards().size());
        assertEquals(0, tested.getPredictedHazards().size());
    }

    @Test
    void test_predict_hazard() {
        AsciiToGameState generator = new AsciiToGameState("_____\n_____\n__Y__\n_____\n_____")
                .setRulesetName(ROYALE_RULESET_NAME).setTurn(24);
        HazardPredictor tested;

        tested = generator.setHazards("HHHHH\nHHHHH\nHH_HH\nHHHHH\nHHHHH").build();
        tested.setHazardStep(25);

        assertEquals(24, tested.getBoard().getHazards().size());
        assertEquals(1, tested.getPredictedHazards().size());
        assertEquals(2, tested.getPredictedHazards().get(0).getValue0());
        assertEquals(2, tested.getPredictedHazards().get(0).getValue1());
        assertThat(tested.getPredictedHazards().get(0).getValue2(), is(closeTo(1.0d, fuzz)));

        tested = generator.setHazards("HHHHH\n_____\n_____\n_____\n_____").build();
        tested.setHazardStep(25);

        assertEquals(5, tested.getBoard().getHazards().size());
        assertEquals(14, tested.getPredictedHazards().size());

        tested = generator.setHazards("HHHHH\nH___H\nH___H\nH___H\nHHHHH").build();
        tested.setHazardStep(25);

        assertEquals(16, tested.getBoard().getHazards().size());
        assertEquals(8, tested.getPredictedHazards().size());
    }
}

package ru.elynx.battlesnake.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.testbuilder.CaseBuilder;

class BoardTest {
    @Test
    void test_base_class_does_not_predict() {
        HazardPredictor entity1 = CaseBuilder.eat_in_hazard();
        GameState gameState = entity1.getGameState();

        Board tested = gameState.getBoard();
        assumeFalse(tested.getHazards().size() == 0);
        assertThat(tested.getActiveHazards(), containsInAnyOrder(tested.getHazards().toArray()));
    }
}

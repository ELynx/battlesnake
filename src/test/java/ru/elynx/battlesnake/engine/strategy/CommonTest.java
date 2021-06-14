package ru.elynx.battlesnake.engine.strategy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.asciitest.AsciiToGameState;
import ru.elynx.battlesnake.engine.predictor.HazardPredictor;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.GameState;

class CommonTest {
    @Test
    void test_for_all_snake_bodies() {
        HazardPredictor entity1 = new AsciiToGameState("y___Y\nyyyyy").build();
        GameState entity = entity1.getGameState();

        List<Coordinates> visited = new ArrayList<>();
        Consumer<Coordinates> what = visited::add;

        Common.forAllSnakeBodies(entity, what);

        // tail will go away
        assertThat(visited, containsInAnyOrder(new Coordinates(0, 0), new Coordinates(1, 0), new Coordinates(2, 0),
                new Coordinates(3, 0), new Coordinates(4, 0), new Coordinates(4, 1)));
    }
}

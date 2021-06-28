package ru.elynx.battlesnake.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Test;
import ru.elynx.battlesnake.testbuilder.CaseBuilder;

class BoardWithActiveHazardsTest {
    @Test
    void test_equals_and_hash_code_same() {
        GameState gameState0 = CaseBuilder.eat_in_hazard();
        Board board0 = gameState0.getBoard();

        Board board0Same = BoardWithActiveHazards.fromAdjacentTurns(board0, board0);
        assertThat(board0Same, is(instanceOf(Board.class)));

        assertEquals(board0, board0Same);
        assertEquals(board0.hashCode(), board0Same.hashCode());
    }

    @Test
    void test_equals_and_hash_code_different() {
        GameState gameState0 = CaseBuilder.eat_in_hazard();
        Board board0 = gameState0.getBoard();

        GameState gameState1 = CaseBuilder.does_not_go_into_hazard_lake();
        Board board1 = gameState1.getBoard();

        assumeTrue(board0.getHazards().size() != board1.getHazards().size());

        Board board1Extra = BoardWithActiveHazards.fromAdjacentTurns(board0, board1);
        assertThat(board1Extra, is(instanceOf(BoardWithActiveHazards.class)));

        assertNotEquals(board0, board1Extra);
        assertNotEquals(board0.hashCode(), board1Extra.hashCode());

        assertEquals(board1, board1Extra);
        assertEquals(board1.hashCode(), board1Extra.hashCode());
    }

    @Test
    void test_null_create_board() {
        GameState gameState1 = CaseBuilder.eat_in_hazard();
        Board board1 = gameState1.getBoard();

        Board board1Same = BoardWithActiveHazards.fromAdjacentTurns(null, board1);

        assertThat(board1Same, is(instanceOf(Board.class)));
        assertEquals(board1, board1Same);
    }

    @Test
    void test_equal_create_board() {
        GameState gameState0 = CaseBuilder.eat_in_hazard();
        Board board0 = gameState0.getBoard();

        Board board0Same = BoardWithActiveHazards.fromAdjacentTurns(board0, board0);
        assertThat(board0Same, is(instanceOf(Board.class)));

        assertThat(board0Same.getHazards(), containsInAnyOrder(board0.getHazards().toArray()));
        assertThat(board0Same.getActiveHazards(), containsInAnyOrder(board0.getActiveHazards().toArray()));
        assertThat(board0Same.getActiveHazards(), containsInAnyOrder(board0Same.getHazards().toArray()));
    }

    @Test
    void test_not_equal_create_board_with_active_hazards() {
        GameState gameState0 = CaseBuilder.eat_in_hazard();
        Board board0 = gameState0.getBoard();

        GameState gameState1 = CaseBuilder.does_not_go_into_hazard_lake();
        Board board1 = gameState1.getBoard();

        assumeTrue(board0.getHazards().size() != board1.getHazards().size());

        Board board1Extra = BoardWithActiveHazards.fromAdjacentTurns(board0, board1);
        assertThat(board1Extra, is(instanceOf(BoardWithActiveHazards.class)));

        assertThat(board1Extra.getActiveHazards(), containsInAnyOrder(board0.getHazards().toArray()));
    }
}

package ru.elynx.battlesnake.entity;

import java.util.List;
import lombok.Getter;
import lombok.NonNull;

public class BoardWithActiveHazards extends Board {
    @Getter(onMethod_ = {@Override})
    private final List<Coordinates> activeHazards;

    private BoardWithActiveHazards(Board board, List<Coordinates> activeHazards) {
        super(board.getDimensions(), board.getFood(), board.getHazards(), board.getSnakes());
        this.activeHazards = activeHazards;
    }

    public static Board fromAdjacentTurns(Board board0, @NonNull Board board1) {
        // for first iteration, previous state can be null
        // then current state is only known state
        if (board0 == null) {
            return board1;
        }

        // if entities have equal number of hazards overall then active are the same
        if (board0.getHazards().size() == board1.getHazards().size()) {
            return board1;
        }

        // active items are items are all items from previous turn
        return new BoardWithActiveHazards(board1, board0.getHazards());
    }

    // visible for testing
    static BoardWithActiveHazards test_createWithNoChecks(Board board, List<Coordinates> activeHazards) {
        return new BoardWithActiveHazards(board, activeHazards);
    }

    @Override
    public boolean hasInactiveHazards() {
        return getHazards().size() != getActiveHazards().size();
    }

    @Override
    public boolean equals(Object to) {
        return super.equals(to);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

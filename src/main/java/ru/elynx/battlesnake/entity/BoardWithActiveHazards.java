package ru.elynx.battlesnake.entity;

import java.util.List;
import lombok.Getter;

public class BoardWithActiveHazards extends Board {
    @Getter(onMethod_ = {@Override})
    private final List<Coordinates> activeHazards;

    private BoardWithActiveHazards(Board board, List<Coordinates> activeHazards) {
        super(board.getDimensions(), board.getFood(), board.getHazards(), board.getSnakes());
        this.activeHazards = activeHazards;
    }

    public static Board fromAdjacentTurns(Board board0, Board board1) {
        // if entities have equal number of hazards overall active are the same
        if (board0.getHazards().size() == board1.getHazards().size()) {
            return board1;
        }

        // active items are items are all items from previous turn
        return new BoardWithActiveHazards(board1, board0.getHazards());
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

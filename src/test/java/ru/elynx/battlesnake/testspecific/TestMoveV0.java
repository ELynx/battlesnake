package ru.elynx.battlesnake.testspecific;

import ru.elynx.battlesnake.protocol.Move;

public class TestMoveV0 extends Move {
    public TestMoveV0(Move move) {
        this.setMove(move.getMove());
        this.setShout(move.getShout());
        this.setDropRequest(move.getDropRequest());

        if (getMove().equalsIgnoreCase("up")) {
            setMove("down");
        } else if (getMove().equalsIgnoreCase("down")) {
            setMove("up");
        }
    }
}

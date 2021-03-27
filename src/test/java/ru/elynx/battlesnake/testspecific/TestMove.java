package ru.elynx.battlesnake.testspecific;

import static ru.elynx.battlesnake.protocol.Move.Moves.DOWN;
import static ru.elynx.battlesnake.protocol.Move.Moves.UP;

import ru.elynx.battlesnake.protocol.Move;

public class TestMove extends Move {
    private final ToApiVersion toApiVersion;

    public TestMove(Move move, ToApiVersion toApiVersion) {
        this.toApiVersion = toApiVersion;

        this.setMove(move.getMove());
        this.setShout(move.getShout());
        this.setDropRequest(move.getDropRequest());
    }

    @Override
    public String getMove() {
        if (this.toApiVersion == ToApiVersion.V0) {
            if (super.getMove().equalsIgnoreCase(UP)) {
                return DOWN;
            } else if (super.getMove().equalsIgnoreCase(DOWN)) {
                return UP;
            }
        }

        return super.getMove();
    }

    @Override
    public String toString() {
        return "TestMove{" + super.toString() + ' ' + toApiVersion + ' ' + getMove() + '}';
    }
}

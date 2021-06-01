package ru.elynx.battlesnake.testspecific;

public class TestMove extends Move {
    private final ToApiVersion toApiVersion;

    public TestMove(Move move, ToApiVersion toApiVersion) {
        if (toApiVersion == ToApiVersion.V0) {
            throw new IllegalArgumentException("V0 APIs are not supported by TestMove");
        }

        this.toApiVersion = toApiVersion;

        super.setMove(move.getMove());
        super.setShout(move.getShout());
        super.setRepeatLast(move.repeatLast());
    }

    @Override
    public String getMove() {
        return super.getMove();
    }

    @Override
    public String toString() {
        return "TestMove{" + super.toString() + ' ' + toApiVersion + ' ' + getMove() + '}';
    }
}

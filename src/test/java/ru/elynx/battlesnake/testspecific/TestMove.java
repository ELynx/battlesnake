package ru.elynx.battlesnake.testspecific;

import ru.elynx.battlesnake.protocol.Move;

public class TestMove extends Move {
    private final ApiVersionTranslation apiVersionTranslation;

    public TestMove(Move move, ApiVersionTranslation apiVersionTranslation) {
        this.apiVersionTranslation = apiVersionTranslation;

        this.setMove(move.getMove());
        this.setShout(move.getShout());
        this.setDropRequest(move.getDropRequest());

        if (this.apiVersionTranslation == ApiVersionTranslation.V0_TO_V1) {
            if (getMove().equalsIgnoreCase("up")) {
                setMove("down");
            } else if (getMove().equalsIgnoreCase("down")) {
                setMove("up");
            }
        }
    }
}

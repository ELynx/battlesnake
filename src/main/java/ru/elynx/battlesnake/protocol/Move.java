package ru.elynx.battlesnake.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Move {
    private String move; // TODO enum
    private String shout;

    public Move() {
    }

    public Move(String move) {
        this.move = move;
    }

    public Move(String move, String shout) {
        this.move = move;
        this.shout = shout; // TODO check length
    }
}

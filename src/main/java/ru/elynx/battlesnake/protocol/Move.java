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

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public String getShout() {
        return shout;
    }

    public void setShout(String shout) {
        this.shout = shout;
    }
}

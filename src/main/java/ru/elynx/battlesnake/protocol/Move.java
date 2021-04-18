package ru.elynx.battlesnake.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Move {
    private String move;
    private String shout;
    private Boolean dropRequest = false;
    private Boolean repeatLast = false;

    public Move() {
        repeatLast = true;
    }

    public Move(String move) {
        this.move = move;
    }

    public Move(String move, String shout) {
        this.move = move;
        this.shout = shout;
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

    public Boolean getDropRequest() {
        return dropRequest;
    }

    public void setDropRequest(Boolean dropRequest) {
        this.dropRequest = dropRequest;
    }

    public Boolean getRepeatLast() {
        return repeatLast;
    }

    public void setRepeatLast(Boolean repeatLast) {
        this.repeatLast = repeatLast;
    }

    @Override
    public String toString() {
        return "Move{move='"
                + (Boolean.TRUE.equals(dropRequest) ? "drop" : Boolean.TRUE.equals(repeatLast) ? "repeat" : move)
                + "'}";
    }

    public static class Moves {
        private Moves() {
        }
        public static final String UP = "up";
        public static final String RIGHT = "right";
        public static final String DOWN = "down";
        public static final String LEFT = "left";
    }
}

package ru.elynx.battlesnake.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.Pattern;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Move {
    @Pattern(regexp = "up|down|left|right", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String move;
    // TODO constraint
    private String shout;
    private Boolean dropRequest = false;

    public Move() {
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
}

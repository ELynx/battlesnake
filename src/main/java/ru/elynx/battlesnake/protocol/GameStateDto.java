package ru.elynx.battlesnake.protocol;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public class GameStateDto {
    @NotNull
    private GameDto game;
    @NotNull
    @PositiveOrZero
    private Integer turn;
    @NotNull
    private BoardDto board;
    @NotNull
    private SnakeDto you;

    public GameStateDto() {
    }

    public GameDto getGame() {
        return game;
    }

    public void setGame(GameDto game) {
        this.game = game;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public BoardDto getBoard() {
        return board;
    }

    public void setBoard(BoardDto board) {
        this.board = board;
    }

    public SnakeDto getYou() {
        return you;
    }

    public void setYou(SnakeDto you) {
        this.you = you;
    }
}

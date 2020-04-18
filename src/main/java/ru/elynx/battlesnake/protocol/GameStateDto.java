package ru.elynx.battlesnake.protocol;

public class GameStateDto {
    private GameDto game;
    private Integer turn;
    private BoardDto board;
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

package ru.elynx.battlesnake.protocol;

public class GameState {
    private Game game;
    private Integer turn;
    private Board board;
    private Snake you;

    public GameState() {
    }

    public static boolean isInvalid(GameState gameState) {
        if (gameState == null)
            return true;

        if (gameState.getGame() == null || gameState.getTurn() == null || gameState.getBoard() == null || gameState.getYou() == null)
            return true;

        if (gameState.getBoard().getFood() == null || gameState.getBoard().getSnakes() == null)
            return true;

        if (gameState.getGame().getId() == null || gameState.getGame().getId().isEmpty())
            return true;

        return false;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Snake getYou() {
        return you;
    }

    public void setYou(Snake you) {
        this.you = you;
    }
}

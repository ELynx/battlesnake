package ru.elynx.battlesnake.engine;

import ru.elynx.battlesnake.protocol.Coords;
import ru.elynx.battlesnake.protocol.GameState;
import ru.elynx.battlesnake.protocol.Move;
import ru.elynx.battlesnake.protocol.SnakeConfig;

public class GameEngine implements IGameEngine {
    private static String UP = "up";
    private static String RIGHT = "right";
    private static String DOWN = "down";
    private static String LEFT = "left";

    @Override
    public SnakeConfig processStart(GameState gameState) {
        return getSnakeConfig();
    }

    @Override
    public Move processMove(GameState gameState) {
        int width = gameState.getBoard().getWidth();
        int height = gameState.getBoard().getHeight();

        Coords head = gameState.getYou().getBody().get(0);
        int x = head.getX();
        int y = head.getY();

        String move = UP;

        if (y == 0)
            move = RIGHT;

        if (x == width - 1)
            move = DOWN;

        if (y == height - 1)
            move = LEFT;

        if (x == 0) {
            if (y > 0) {
                move = UP;
            } else {
                // the only "edge case to stateless logic"
                move = RIGHT;
            }
        }

        return new Move(move, "4% ready");
    }

    @Override
    public Void processEnd(GameState gameState) {
        return null;
    }
}

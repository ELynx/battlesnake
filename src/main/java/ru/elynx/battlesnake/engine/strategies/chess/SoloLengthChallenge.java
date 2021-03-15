package ru.elynx.battlesnake.engine.strategies.chess;

import java.util.ArrayList;
import ru.elynx.battlesnake.protocol.GameStateDto;
import ru.elynx.battlesnake.protocol.Move;

class SoloLengthChallenge extends ChessStrategy {
    @Override
    protected int calculateStage(GameStateDto gameStateDto) {
        final int snakeToConquerThreshold = 2 * gameStateDto.getBoard().getWidth();

        // grow to double width
        if (gameStateDto.getYou().getLength() < snakeToConquerThreshold)
            return 0;

        final int areaLeft = gameStateDto.getBoard().getWidth() * gameStateDto.getBoard().getHeight()
                - snakeToConquerThreshold;

        // wait for food to fill in the space
        if (gameStateDto.getBoard().getFood().size() < areaLeft)
            return 0;

        // rush board
        return 1;
    }

    @Override
    protected StringField makeDirections(int stage, int width, int height) {
        return makeStage0(width, height);
    }

    /**
     * Stage zero Loop at the top of the board until head follows tail Loop at the
     * top of the board until whole board is food
     *
     * @param width
     *            Width of the board
     * @param height
     *            Height of the board
     * @return Instructions on how to run in circles
     */
    private StringField makeStage0(int width, int height) {
        ArrayList<String> directions = new ArrayList<>(width * height);

        // bottom rows just instruct to go to top rows
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height - 2; ++y) {
                directions.add(Move.Moves.UP);
            }
        }

        // pre-last row
        for (int x = 0; x < width - 1; ++x) {
            directions.add(Move.Moves.RIGHT);
        }
        directions.add(Move.Moves.UP);

        // last row
        directions.add(Move.Moves.DOWN);
        for (int x = 0; x < width - 1; ++x) {
            directions.add(Move.Moves.LEFT);
        }

        return StringField.of(width, height, directions.toArray(new String[0]));
    }
}

package ru.elynx.battlesnake.engine.strategies.chess;

import static ru.elynx.battlesnake.protocol.Move.Moves.*;

import java.util.ArrayList;
import ru.elynx.battlesnake.protocol.GameStateDto;

class SoloLengthChallenge extends ChessStrategy {
    @Override
    protected int calculateStage(GameStateDto gameStateDto) {
        // grow to double width minus two cells on the right for food spawn
        final int snakeToConquerThreshold = 2 * (gameStateDto.getBoard().getWidth() - 1);

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
        if (stage == 1)
            return makeStage1(width, height);

        // in any unknown situation, go loop
        return makeStage0(width, height);
    }

    /**
     * Stage zero. Loop at the bottom of the board until head follows tail. Loop at
     * the bottom of the board until whole board is food.
     *
     * @param width
     *            Width of the board
     * @param height
     *            Height of the board
     * @return Instructions on how to run in circles
     */
    private StringField makeStage0(int width, int height) {
        ArrayList<String> directions = new ArrayList<>(width * height);

        // first row, cut 1 cell short to spawn food out-of-head
        for (int x = 0; x < width - 2; ++x) {
            directions.add(RIGHT);
        }
        directions.add(UP);
        directions.add(UP); // kick into loop

        // second row
        directions.add(DOWN);
        for (int x = 1; x < width; ++x) {
            directions.add(LEFT);
        }

        for (int x = 0; x < width; ++x) {
            for (int y = 2; y < height; ++y) {
                directions.add(DOWN);
            }
        }

        return StringField.of(width, height, directions.toArray(new String[0]));
    }

    /**
     * Stage one. Eat every turn, fill the field
     *
     * @param width
     *            Width of the board
     * @param height
     *            Height of the board
     * @return Instructions on how to cover the field
     */
    private StringField makeStage1(int width, int height) {
        ArrayList<String> directions = new ArrayList<>(width * height);

        // first row, exit to remaining cells
        for (int x = 0; x < width - 1; ++x) {
            directions.add(RIGHT);
        }
        directions.add(UP);

        // second row, exit on remaining cell
        directions.add(DOWN);
        for (int x = 1; x < width - 1; ++x) {
            directions.add(LEFT);
        }
        directions.add(UP);

        for (int y = 0; y < height; ++y) {
            if (y % 2 == 1) {
                for (int x = 0; x < width - 1; ++x) {
                    directions.add(RIGHT);
                }
                directions.add(UP);
            } else {
                directions.add(UP);
                for (int x = 1; x < width; ++x) {
                    directions.add(LEFT);
                }
            }
        }

        int lastMoveIndex;
        if (height % 2 == 0) {
            lastMoveIndex = directions.size() - 1;
        } else {
            lastMoveIndex = directions.size() - width;
        }

        directions.set(lastMoveIndex, DOWN);

        return StringField.of(width, height, directions.toArray(new String[0]));
    }
}

package ru.elynx.battlesnake.engine.strategies.chess;

import static ru.elynx.battlesnake.protocol.Move.Moves.*;

import java.util.Arrays;
import java.util.stream.Stream;
import ru.elynx.battlesnake.protocol.GameStateDto;

class SoloLengthChallenge extends ChessStrategy {
    private boolean latch = false;
    
    @Override
    protected int calculateStage(GameStateDto gameStateDto) {
        if (latch)
            return 1;

        // wait for length
        if (gameStateDto.getYou().getLength() < 23)
            return 0;

        // wait for food to fill in the space
        if (gameStateDto.getBoard().getFood().size() < 24)
            return 0;

        // rush board
        latch = true;
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
     * Stage zero. Make trident.
     *
     * @param width
     *            7
     * @param height
     *            7
     * @return Instructions on how to run in circles.
     */
    private StringField makeStage0(int width, int height) {
        if (width != 7 || height != 7) {
            return null;
        }

        String[] map6 = {DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN};
        String[] map5 = map6;
        String[] map4 = map5;
        String[] map3 = {DOWN, LEFT, DOWN, LEFT, DOWN, LEFT, LEFT};
        String[] map2 = {DOWN, UP, DOWN, UP, DOWN, UP, UP};
        String[] map1 = {DOWN, UP, LEFT, UP, LEFT, UP, UP};
        String[] map0 = {RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, UP, UP};

        Stream<String> directions = Arrays.stream(map0);
        directions = Stream.concat(directions, Arrays.stream(map1));
        directions = Stream.concat(directions, Arrays.stream(map2));
        directions = Stream.concat(directions, Arrays.stream(map3));
        directions = Stream.concat(directions, Arrays.stream(map4));
        directions = Stream.concat(directions, Arrays.stream(map5));
        directions = Stream.concat(directions, Arrays.stream(map6));

        return StringField.of(width, height, directions.toArray(String[]::new));
    }

    /**
     * Stage one. Eat every turn, fill the field.
     *
     * @param width
     *            7
     * @param height
     *            7
     * @return Instructions on how to cover the field.
     */
    private StringField makeStage1(int width, int height) {
        if (width != 7 || height != 7) {
            return null;
        }

        String[] map6 = {UP, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT};
        String[] map5 = {RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, UP};
        String[] map4 = map6;
        String[] map3 = {DOWN, LEFT, DOWN, LEFT, DOWN, LEFT, UP};
        String[] map2 = {DOWN, UP, DOWN, UP, DOWN, RIGHT, UP};
        String[] map1 = {DOWN, UP, LEFT, UP, LEFT, UP, LEFT};
        String[] map0 = {RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, UP};

        Stream<String> directions = Arrays.stream(map0);
        directions = Stream.concat(directions, Arrays.stream(map1));
        directions = Stream.concat(directions, Arrays.stream(map2));
        directions = Stream.concat(directions, Arrays.stream(map3));
        directions = Stream.concat(directions, Arrays.stream(map4));
        directions = Stream.concat(directions, Arrays.stream(map5));
        directions = Stream.concat(directions, Arrays.stream(map6));

        return StringField.of(width, height, directions.toArray(String[]::new));
    }
}

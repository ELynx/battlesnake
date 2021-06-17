package ru.elynx.battlesnake.engine.strategy.solver;

import static ru.elynx.battlesnake.entity.MoveCommand.*;

import java.util.Arrays;
import java.util.stream.Stream;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Dimensions;
import ru.elynx.battlesnake.entity.GameState;
import ru.elynx.battlesnake.entity.MoveCommand;

public class SoloLengthChallengeStrategy extends SolverStrategy {
    private boolean latch1 = false;
    private boolean latch2 = false;

    @Override
    protected int calculateStage(GameState gameState) {
        if (latch2)
            return 2;

        Coordinates head = gameState.getYou().getHead();
        if (latch1 && head.getX() == 6 && head.getY() == 0) {
            latch2 = true;
            return 2;
        }

        if (latch1)
            return 1;

        // wait for length
        if (gameState.getYou().getLength() < 23)
            return 0;

        // wait for food to fill in the space
        if (gameState.getBoard().getFood().size() < 25)
            return 0;

        latch1 = true;
        return 1;
    }

    @Override
    protected MoveCommandField makeDirections(int stage, Dimensions dimensions) {
        if (stage == 1)
            return makeStage1(dimensions);

        if (stage == 2)
            return makeStage2(dimensions);

        // in any unknown situation, go loop
        return makeStage0(dimensions);
    }

    /**
     * Stage zero. Make trident.
     *
     * @param dimensions
     *            7x7
     * @return Instructions on how to run in circles.
     */
    private MoveCommandField makeStage0(Dimensions dimensions) {
        if (dimensions.area() != 49) {
            return null;
        }

        MoveCommand[] map6 = {DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN};
        MoveCommand[] map5 = {DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN};
        MoveCommand[] map4 = {DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN};
        MoveCommand[] map3 = {DOWN, LEFT, DOWN, LEFT, DOWN, LEFT, LEFT};
        MoveCommand[] map2 = {DOWN, UP, DOWN, UP, DOWN, UP, UP};
        MoveCommand[] map1 = {DOWN, UP, LEFT, UP, LEFT, UP, UP};
        MoveCommand[] map0 = {RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, UP, UP};

        Stream<MoveCommand> directions = Arrays.stream(map0);
        directions = Stream.concat(directions, Arrays.stream(map1));
        directions = Stream.concat(directions, Arrays.stream(map2));
        directions = Stream.concat(directions, Arrays.stream(map3));
        directions = Stream.concat(directions, Arrays.stream(map4));
        directions = Stream.concat(directions, Arrays.stream(map5));
        directions = Stream.concat(directions, Arrays.stream(map6));

        return MoveCommandField.of(dimensions, directions.toArray(MoveCommand[]::new));
    }

    /**
     * Stage one. Wait for escape.
     *
     * @param dimensions
     *            7x7
     * @return Instructions on how to cover the field.
     */
    private MoveCommandField makeStage1(Dimensions dimensions) {
        if (dimensions.area() != 49) {
            return null;
        }

        MoveCommand[] map6 = {DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN};
        MoveCommand[] map5 = {DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN};
        MoveCommand[] map4 = {DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN};
        MoveCommand[] map3 = {DOWN, LEFT, DOWN, LEFT, DOWN, LEFT, UP};
        MoveCommand[] map2 = {DOWN, UP, DOWN, UP, DOWN, RIGHT, UP};
        MoveCommand[] map1 = {DOWN, UP, LEFT, UP, LEFT, UP, LEFT};
        MoveCommand[] map0 = {RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, UP};

        Stream<MoveCommand> directions = Arrays.stream(map0);
        directions = Stream.concat(directions, Arrays.stream(map1));
        directions = Stream.concat(directions, Arrays.stream(map2));
        directions = Stream.concat(directions, Arrays.stream(map3));
        directions = Stream.concat(directions, Arrays.stream(map4));
        directions = Stream.concat(directions, Arrays.stream(map5));
        directions = Stream.concat(directions, Arrays.stream(map6));

        return MoveCommandField.of(dimensions, directions.toArray(MoveCommand[]::new));
    }

    /**
     * Stage two. Run.
     *
     * @param dimensions
     *            7x7
     * @return Instructions on how to cover the field.
     */
    private MoveCommandField makeStage2(Dimensions dimensions) {
        if (dimensions.area() != 49) {
            return null;
        }

        MoveCommand[] map6 = {DOWN, LEFT, LEFT, LEFT, LEFT, LEFT, LEFT};
        MoveCommand[] map5 = {DOWN, LEFT, LEFT, LEFT, LEFT, LEFT, UP};
        MoveCommand[] map4 = {RIGHT, RIGHT, RIGHT, RIGHT, DOWN, UP, UP};
        MoveCommand[] map3 = {DOWN, LEFT, DOWN, LEFT, RIGHT, UP, UP};
        MoveCommand[] map2 = {DOWN, UP, DOWN, UP, DOWN, RIGHT, UP};
        MoveCommand[] map1 = {DOWN, UP, LEFT, UP, LEFT, UP, LEFT};
        MoveCommand[] map0 = {RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, UP};

        Stream<MoveCommand> directions = Arrays.stream(map0);
        directions = Stream.concat(directions, Arrays.stream(map1));
        directions = Stream.concat(directions, Arrays.stream(map2));
        directions = Stream.concat(directions, Arrays.stream(map3));
        directions = Stream.concat(directions, Arrays.stream(map4));
        directions = Stream.concat(directions, Arrays.stream(map5));
        directions = Stream.concat(directions, Arrays.stream(map6));

        return MoveCommandField.of(dimensions, directions.toArray(MoveCommand[]::new));
    }
}

package ru.elynx.battlesnake.engine.strategies.solver;

import ru.elynx.battlesnake.engine.math.Matrix;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Dimensions;
import ru.elynx.battlesnake.entity.MoveCommand;

public class MoveCommandField extends Matrix {
    private final MoveCommand[] values;

    private MoveCommandField(Dimensions dimensions, MoveCommand[] values) {
        super(dimensions);

        this.values = values;
    }

    public static MoveCommandField of(Dimensions dimensions, MoveCommand[] values) throws IllegalArgumentException {
        if (dimensions.area() != values.length)
            throw new IllegalArgumentException("Dimensions do not match provided number of arguments");

        return new MoveCommandField(dimensions, values);
    }

    public MoveCommand getMoveCommand(Coordinates coordinates) {
        int boundIndex = calculateBoundIndex(coordinates);
        return getMoveCommandByBoundIndex(boundIndex);
    }

    private MoveCommand getMoveCommandByBoundIndex(int index) {
        if (index < 0)
            throw new IllegalArgumentException("Illegal element access for index [" + index + ']');

        return getMoveCommandByIndex(index);
    }

    private MoveCommand getMoveCommandByIndex(int index) {
        return values[index];
    }
}

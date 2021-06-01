package ru.elynx.battlesnake.engine.math;

import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Dimensions;

public class Matrix {
    private final Dimensions dimensions;

    protected Matrix(Dimensions dimensions) {
        this.dimensions = dimensions;
    }

    protected int calculateBoundIndex(Coordinates coordinates) {
        if (!coordinates.within(dimensions)) {
            return -1;
        }

        return calculateIndex(coordinates);
    }

    private int calculateIndex(Coordinates coordinates) {
        return coordinates.getX() + dimensions.getWidth() * coordinates.getY();
    }
}

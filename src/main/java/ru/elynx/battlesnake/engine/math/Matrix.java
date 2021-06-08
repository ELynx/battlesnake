package ru.elynx.battlesnake.engine.math;

import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Dimensions;

public class Matrix {
    private final Dimensions dimensions;

    protected Matrix(Dimensions dimensions) {
        this.dimensions = dimensions;
    }

    protected int calculateBoundIndex(Coordinates coordinates) {
        return calculateBoundIndex(coordinates.getX(), coordinates.getY());
    }

    protected int calculateBoundIndex(int x, int y) {
        if (outside(x, y)) {
            return -1;
        }

        return calculateIndex(x, y);
    }

    private boolean outside(int x, int y) {
        return x < 0 || y < 0 || x >= dimensions.getWidth() || y >= dimensions.getHeight();
    }

    private int calculateIndex(int x, int y) {
        return x + dimensions.getWidth() * y;
    }
}

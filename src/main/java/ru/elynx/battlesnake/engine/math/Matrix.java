package ru.elynx.battlesnake.engine.math;

public class Matrix {
    private final int width;
    private final int height;

    protected Matrix(int width, int height) {
        this.width = width;
        this.height = height;
    }

    protected int calculateBoundIndex(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return -1;

        return calculateIndex(x, y);
    }

    private int calculateIndex(int x, int y) {
        return x + width * y;
    }
}

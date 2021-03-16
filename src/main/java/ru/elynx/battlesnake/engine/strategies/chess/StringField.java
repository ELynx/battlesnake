package ru.elynx.battlesnake.engine.strategies.chess;

public class StringField {
    private final int width;

    private final String[] values;

    private StringField(int width, int height, String[] values) {
        this.width = width;

        this.values = values;
    }

    public static StringField of(int width, int height, String[] values) throws IllegalArgumentException {
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Dimensions must be positive");

        if (width * height != values.length)
            throw new IllegalArgumentException("Dimensions do not match provided number of arguments");

        return new StringField(width, height, values);
    }

    String getString(int x, int y) {
        final int index = x + width * y;
        return values[index];
    }
}

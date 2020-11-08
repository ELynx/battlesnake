package ru.elynx.battlesnake.engine.math;

public class FlagMatrix {
    private final int width;
    private final int height;
    private final int length;
    private final boolean[] values;
    private final boolean outsideValue;

    protected FlagMatrix(int width, int height, boolean outsideValue) {
        this.width = width;
        this.height = height;
        this.length = this.width * this.height;
        this.values = new boolean[this.length];
        this.outsideValue = outsideValue;
    }

    public static FlagMatrix falseMatrix(int width, int height, boolean outsideValue) {
        return new FlagMatrix(width, height, outsideValue);
    }

    /**
     * Reset to !outsideValue.
     */
    public void reset() {
        for (int i = 0; i < length; ++i) {
            values[i] = !outsideValue;
        }
    }

    public boolean getValue(int x, int y) {
        final int index = safeIndex(x, y);
        if (index < 0)
            return outsideValue;

        return unsafeGetValue(index);
    }

    public boolean setValue(int x, int y, boolean value) {
        final int index = safeIndex(x, y);
        if (index < 0)
            return false;

        unsafeSetValue(index, value);
        return true;
    }

    protected int unsafeIndex(int x, int y) {
        return x + width * y;
    }

    protected int safeIndex(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return -1;

        return unsafeIndex(x, y);
    }

    protected boolean unsafeGetValue(int index) {
        return values[index];
    }

    protected void unsafeSetValue(int index, boolean value) {
        values[index] = value;
    }
}

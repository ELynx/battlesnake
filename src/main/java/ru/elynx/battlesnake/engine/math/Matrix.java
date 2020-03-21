package ru.elynx.battlesnake.engine.math;

public class Matrix {
    private final static double DEFAULT_SPLASH = 2.0d;

    private final int width;
    private final int height;
    private final double[] values;
    private final double outsideValue;

    protected Matrix(int width, int height, double outsideValue) {
        this.width = width;
        this.height = height;
        this.values = new double[width * height];
        this.outsideValue = outsideValue;
    }

    public static Matrix zeroMatrix(int width, int height, double outsideValue) {
        return new Matrix(width, height, outsideValue);
    }

    public void zero() {
        for (int i = 0, l = width * height; i < l; ++i) {
            values[i] = 0.0d;
        }
    }

    public double getValue(int x, int y) {
        int index = safeIndex(x, y);
        if (index < 0)
            return outsideValue;

        return unsafeGetValue(index);
    }

    public void setValue(int x, int y, double value) {
        int index = safeIndex(x, y);
        if (index < 0)
            return;

        unsafeSetValue(index, value);
    }

    protected void addValue(int x, int y, double value) {
        int index = safeIndex(x, y);
        if (index < 0)
            return;

        unsafeAddValue(index, value);
    }

    public boolean splash1stOrder(int x, int y, double valueAtImpact) {
        return splash1stOrder(x, y, valueAtImpact, DEFAULT_SPLASH);
    }

    public boolean splash1stOrder(int x, int y, double valueAtImpact, double denominator) {
        // no impact - no setter
        if (valueAtImpact == 0.0d)
            return false;

        // TODO can be optimized

        // if impact is out of matrix ignore the setter
        int index = safeIndex(x, y);
        if (index < 0)
            return false;

        unsafeSetValue(index, valueAtImpact);

        valueAtImpact = valueAtImpact / denominator;

        addValue(x, y - 1, valueAtImpact);
        addValue(x - 1, y, valueAtImpact);
        addValue(x + 1, y, valueAtImpact);
        addValue(x, y + 1, valueAtImpact);

        return true;
    }

    public boolean splash2ndOrder(int x, int y, double valueAtImpact) {
        return splash2ndOrder(x, y, valueAtImpact, DEFAULT_SPLASH);
    }

    public boolean splash2ndOrder(int x, int y, double valueAtImpact, double denominator) {
        // TODO can be optimized af

        if (splash1stOrder(x, y, valueAtImpact, denominator)) {
            valueAtImpact = valueAtImpact / denominator / denominator;

            addValue(x - 1, y - 1, valueAtImpact);
            addValue(x + 1, y - 1, valueAtImpact);
            addValue(x - 1, y + 1, valueAtImpact);
            addValue(x + 1, y + 1, valueAtImpact);

            return true;
        }

        return false;
    }

    protected int unsafeIndex(int x, int y) {
        return x + width * y;
    }

    protected int safeIndex(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return -1;

        return unsafeIndex(x, y);
    }

    protected double unsafeGetValue(int index) {
        return values[index];
    }

    protected void unsafeSetValue(int index, double value) {
        values[index] = value;
    }

    protected void unsafeAddValue(int index, double value) {
        values[index] += value;
    }
}

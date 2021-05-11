package ru.elynx.battlesnake.engine.math;

public class DoubleMatrix {
    private static final double DEFAULT_SPLASH = 2.0d;

    private final int width;
    private final int height;
    private final int valuesLength;

    private final double[] values;
    private final double outsideValue;

    private DoubleMatrix(int width, int height, double outsideValue) {
        this.width = width;
        this.height = height;
        this.valuesLength = this.width * this.height;

        this.values = new double[this.valuesLength];
        this.outsideValue = outsideValue;
    }

    public static DoubleMatrix uninitializedMatrix(int width, int height, double outsideValue) {
        return new DoubleMatrix(width, height, outsideValue);
    }

    public static DoubleMatrix zeroMatrix(int width, int height, double outsideValue) {
        DoubleMatrix result = uninitializedMatrix(width, height, outsideValue);
        result.zero();
        return result;
    }

    public void zero() {
        for (int i = 0; i < valuesLength; ++i) {
            values[i] = 0.0d;
        }
    }

    public double getValue(int x, int y) {
        int boundIndex = calculateBoundIndex(x, y);
        return getValueByBoundIndex(boundIndex);
    }

    private int calculateBoundIndex(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return -1;

        return calculateIndex(x, y);
    }

    private int calculateIndex(int x, int y) {
        return x + width * y;
    }

    private double getValueByBoundIndex(int boundIndex) {
        if (boundIndex < 0)
            return outsideValue;

        return getValueByIndex(boundIndex);
    }

    private double getValueByIndex(int index) {
        return values[index];
    }

    public boolean addValue(int x, int y, double value) {
        int boundIndex = calculateBoundIndex(x, y);
        return addValueByBoundIndex(boundIndex, value);
    }

    private boolean addValueByBoundIndex(int boundIndex, double value) {
        if (boundIndex < 0)
            return false;

        addValueByIndex(boundIndex, value);
        return true;
    }

    private void addValueByIndex(int index, double value) {
        values[index] += value;
    }

    public boolean splash1stOrder(int x, int y, double valueAtImpact) {
        return splash1stOrder(x, y, valueAtImpact, DEFAULT_SPLASH);
    }

    public boolean splash1stOrder(int x, int y, double valueAtImpact, double denominator) {
        // no impact - no setter
        if (valueAtImpact == 0.0d)
            return false;

        // if impact is out of matrix ignore the setter
        if (addValue(x, y, valueAtImpact)) {
            valueAtImpact = valueAtImpact / denominator;

            addValue(x, y - 1, valueAtImpact);
            addValue(x - 1, y, valueAtImpact);
            addValue(x + 1, y, valueAtImpact);
            addValue(x, y + 1, valueAtImpact);

            return true;
        }

        return false;
    }

    public boolean splash2ndOrder(int x, int y, double valueAtImpact) {
        return splash2ndOrder(x, y, valueAtImpact, DEFAULT_SPLASH);
    }

    public boolean splash2ndOrder(int x, int y, double valueAtImpact, double denominator) {
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
}

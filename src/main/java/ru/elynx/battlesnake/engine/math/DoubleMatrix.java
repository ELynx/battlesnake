package ru.elynx.battlesnake.engine.math;

import java.util.Arrays;

public class DoubleMatrix extends Matrix {
    private static final double DEFAULT_SPLASH = 2.0d;

    private final double[] values;
    private final double outsideValue;

    private DoubleMatrix(int width, int height, double outsideValue) {
        super(width, height);

        this.values = new double[width * height];
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
        Arrays.fill(values, 0.0d);
    }

    public double getValue(int x, int y) {
        int boundIndex = calculateBoundIndex(x, y);
        return getValueByBoundIndex(boundIndex);
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

        // apply splash only if impact is applied
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
        // apply splash 2nd order only if splash 1st order is applied
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

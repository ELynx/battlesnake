package ru.elynx.battlesnake.engine.math;

import java.util.Arrays;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Dimensions;

public class DoubleMatrix extends Matrix {
    private static final double DEFAULT_SPLASH = 2.0d;

    private final double[] values;
    private final double outsideValue;

    private DoubleMatrix(Dimensions dimensions, double outsideValue) {
        super(dimensions);

        this.values = new double[dimensions.area()];
        this.outsideValue = outsideValue;
    }

    public static DoubleMatrix uninitializedMatrix(Dimensions dimensions, double outsideValue) {
        return new DoubleMatrix(dimensions, outsideValue);
    }

    public static DoubleMatrix zeroMatrix(Dimensions dimensions, double outsideValue) {
        DoubleMatrix result = uninitializedMatrix(dimensions, outsideValue);
        result.zero();
        return result;
    }

    public void zero() {
        Arrays.fill(values, 0.0d);
    }

    public double getValue(Coordinates coordinates) {
        int boundIndex = calculateBoundIndex(coordinates);
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

    public boolean addValue(Coordinates coordinates, double value) {
        int boundIndex = calculateBoundIndex(coordinates);
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

    public boolean splash1stOrder(Coordinates coordinates, double valueAtImpact) {
        return splash1stOrder(coordinates, valueAtImpact, DEFAULT_SPLASH);
    }

    public boolean splash1stOrder(Coordinates coordinates, double valueAtImpact, double denominator) {
        // no impact - no setter
        if (valueAtImpact == 0.0d)
            return false;

        // apply splash only if impact is applied
        if (addValue(coordinates, valueAtImpact)) {
            valueAtImpact = valueAtImpact / denominator;

            for (Coordinates neighbour : coordinates.sideNeighbours()) {
                addValue(neighbour, valueAtImpact);
            }

            return true;
        }

        return false;
    }

    public boolean splash2ndOrder(Coordinates coordinates, double valueAtImpact) {
        return splash2ndOrder(coordinates, valueAtImpact, DEFAULT_SPLASH);
    }

    public boolean splash2ndOrder(Coordinates coordinates, double valueAtImpact, double denominator) {
        // apply splash 2nd order only if splash 1st order is applied
        if (splash1stOrder(coordinates, valueAtImpact, denominator)) {
            valueAtImpact = valueAtImpact / denominator / denominator;

            for (Coordinates neighbour : coordinates.cornerNeighbours()) {
                addValue(neighbour, valueAtImpact);
            }

            return true;
        }

        return false;
    }
}

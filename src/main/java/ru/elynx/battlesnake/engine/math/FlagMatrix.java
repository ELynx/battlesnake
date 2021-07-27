package ru.elynx.battlesnake.engine.math;

import java.util.Arrays;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Dimensions;

public class FlagMatrix extends Matrix {
    private final boolean[] values;
    private final boolean outsideValue;

    private FlagMatrix(Dimensions dimensions, boolean outsideValue) {
        super(dimensions);

        this.values = new boolean[dimensions.getArea()];
        this.outsideValue = outsideValue;
    }

    public static FlagMatrix uninitializedMatrix(Dimensions dimensions, boolean outsideValue) {
        return new FlagMatrix(dimensions, outsideValue);
    }

    public static FlagMatrix unsetMatrix(Dimensions dimensions, boolean outsideValue) {
        FlagMatrix result = uninitializedMatrix(dimensions, outsideValue);
        result.unsetAll();
        return result;
    }

    public void unsetAll() {
        Arrays.fill(values, false);
    }

    public boolean set(Coordinates coordinates) {
        int boundIndex = calculateBoundIndex(coordinates);
        return setByBoundIndex(boundIndex);
    }

    public boolean set(int x, int y) {
        int boundIndex = calculateBoundIndex(x, y);
        return setByBoundIndex(boundIndex);
    }

    private boolean setByBoundIndex(int boundIndex) {
        if (boundIndex < 0) {
            return false;
        }

        setByIndex(boundIndex);
        return true;
    }

    private void setByIndex(int index) {
        values[index] = true;
    }

    public boolean isSet(Coordinates coordinates) {
        int boundIndex = calculateBoundIndex(coordinates);
        return isSetByBoundIndex(boundIndex);
    }

    public boolean isSet(int x, int y) {
        int boundIndex = calculateBoundIndex(x, y);
        return isSetByBoundIndex(boundIndex);
    }

    private boolean isSetByBoundIndex(int boundIndex) {
        if (boundIndex < 0) {
            return outsideValue;
        }

        return isSetByIndex(boundIndex);
    }

    private boolean isSetByIndex(int index) {
        return values[index];
    }
}

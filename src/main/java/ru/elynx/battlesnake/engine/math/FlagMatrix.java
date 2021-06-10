package ru.elynx.battlesnake.engine.math;

import java.util.Arrays;
import ru.elynx.battlesnake.entity.Dimensions;

public class FlagMatrix extends Matrix {
    private final boolean[] values;

    private FlagMatrix(Dimensions dimensions) {
        super(dimensions);

        this.values = new boolean[dimensions.area()];
    }

    public static FlagMatrix uninitializedMatrix(Dimensions dimensions) {
        return new FlagMatrix(dimensions);
    }

    public static FlagMatrix unsetMatrix(Dimensions dimensions) {
        FlagMatrix result = uninitializedMatrix(dimensions);
        result.unsetAll();
        return result;
    }

    public void unsetAll() {
        Arrays.fill(values, false);
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

    public boolean isSet(int x, int y) {
        int boundIndex = calculateBoundIndex(x, y);
        return isSetByBoundIndex(boundIndex);
    }

    private boolean isSetByBoundIndex(int boundIndex) {
        if (boundIndex < 0) {
            return false;
        }

        return isSetByIndex(boundIndex);
    }

    private boolean isSetByIndex(int index) {
        return values[index];
    }
}

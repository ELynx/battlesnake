package ru.elynx.battlesnake.engine.math;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Dimensions;

public class FreeSpaceMatrix extends Matrix {
    private static final int OCCUPIED_VALUE = 0;
    private static final int UNSET_VALUE = -1;
    private static final int FLOOD_FILL_VALUE = -2;

    private final int[] spaceValues;
    private final Deque<Coordinates> floodFillStack;

    private FreeSpaceMatrix(Dimensions dimensions) {
        super(dimensions);

        this.spaceValues = new int[dimensions.area()];
        this.floodFillStack = new ArrayDeque<>();
    }

    public static FreeSpaceMatrix uninitializedMatrix(Dimensions dimensions) {
        return new FreeSpaceMatrix(dimensions);
    }

    public static FreeSpaceMatrix emptyMatrix(Dimensions dimensions) {
        FreeSpaceMatrix result = uninitializedMatrix(dimensions);
        result.empty();
        return result;
    }

    public void empty() {
        Arrays.fill(spaceValues, UNSET_VALUE);
    }

    public boolean setOccupied(Coordinates coordinates) {
        int boundIndex = calculateBoundIndex(coordinates);
        return setOccupiedByBoundIndex(boundIndex);
    }

    private boolean setOccupiedByBoundIndex(int boundIndex) {
        if (boundIndex < 0)
            return false;

        setValueByIndex(boundIndex, OCCUPIED_VALUE);
        return true;
    }

    private void setValueByIndex(int index, int value) {
        spaceValues[index] = value;
    }

    /**
     * Test if cell is free, without actual space calculation. Use in cases of
     * preliminary getters, and true/false testing.
     *
     * @param coordinates
     *            coordinates to test
     * @return True if cell was not set as occupied.
     */
    public boolean isFree(Coordinates coordinates) {
        int value = getValueByCoordinates(coordinates);
        // it does not matter if cell has free space calculated or not to be free
        return value != OCCUPIED_VALUE;
    }

    private int getValueByCoordinates(Coordinates coordinates) {
        int boundIndex = calculateBoundIndex(coordinates);
        return getValueByBoundIndex(boundIndex);
    }

    private int getValueByXY(int x, int y) {
        int boundIndex = calculateBoundIndex(x, y);
        return getValueByBoundIndex(boundIndex);
    }

    private int getValueByBoundIndex(int boundIndex) {
        if (boundIndex < 0)
            return OCCUPIED_VALUE;

        return getValueByIndex(boundIndex);
    }

    private int getValueByIndex(int index) {
        return spaceValues[index];
    }

    public int getFreeSpace(Coordinates coordinates) {
        // if cell is set as occupied, return it
        // if flood fill already calculated free space, return it
        int value = getValueByCoordinates(coordinates);
        if (value > UNSET_VALUE)
            return value;

        return getFreeSpaceByFloodFill(coordinates);
    }

    private int getFreeSpaceByFloodFill(Coordinates coordinates) {
        floodFillStack.add(coordinates);

        while (!floodFillStack.isEmpty()) {
            Coordinates checked = floodFillStack.removeLast();
            int checkedY = checked.getY();

            int leftX = checked.getX();
            while (fillIfUnset(leftX - 1, checkedY)) {
                leftX -= 1;
            }

            int rightX = checked.getX();
            while (fillIfUnset(rightX, checkedY)) {
                rightX += 1;
            }

            scanAndQueue(leftX, rightX - 1, checkedY + 1);
            scanAndQueue(leftX, rightX - 1, checkedY - 1);
        }

        return countAndFill();
    }

    private boolean fillIfUnset(int x, int y) {
        int boundIndex = calculateBoundIndex(x, y);

        if (getValueByBoundIndex(boundIndex) == UNSET_VALUE) {
            // index is tested to be bound by operation above
            setValueByIndex(boundIndex, FLOOD_FILL_VALUE);
            return true;
        }

        return false;
    }

    private void scanAndQueue(int leftX, int rightX, int y) {
        boolean queued = false;
        for (int x = leftX; x <= rightX; ++x) {
            if (isSet(x, y)) {
                queued = false;
            } else if (!queued) {
                floodFillStack.add(new Coordinates(x, y));
                queued = true;
            }
        }
    }

    private boolean isSet(int x, int y) {
        int value = getValueByXY(x, y);
        return value != UNSET_VALUE;
    }

    private int countAndFill() {
        int count = 0;
        for (int index = 0; index < spaceValues.length; ++index) {
            if (getValueByIndex(index) == FLOOD_FILL_VALUE)
                ++count;
        }

        if (count > 0) {
            for (int index = 0; index < spaceValues.length; ++index) {
                if (getValueByIndex(index) == FLOOD_FILL_VALUE)
                    setValueByIndex(index, count);
            }
        }

        return count;
    }
}

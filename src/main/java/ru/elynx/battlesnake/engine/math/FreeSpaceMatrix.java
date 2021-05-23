package ru.elynx.battlesnake.engine.math;

import java.util.Arrays;

public class FreeSpaceMatrix {
    private static final int OCCUPIED_VALUE = 0;
    private static final int UNSET_VALUE = -1;
    private static final int FILL_VALUE = -2;

    private final int width;
    private final int height;

    private final int[] spaceValues;
    private final int[] floodFillStack;

    private FreeSpaceMatrix(int width, int height) {
        this.width = width;
        this.height = height;

        this.spaceValues = new int[this.width * this.height];
        this.floodFillStack = new int[this.spaceValues.length * 2]; // potentially stack each xy
    }

    public static FreeSpaceMatrix uninitializedMatrix(int width, int height) {
        return new FreeSpaceMatrix(width, height);
    }

    public static FreeSpaceMatrix emptyMatrix(int width, int height) {
        FreeSpaceMatrix result = uninitializedMatrix(width, height);
        result.empty();
        return result;
    }

    public void empty() {
        Arrays.fill(spaceValues, UNSET_VALUE);
    }

    public boolean setOccupied(int x, int y) {
        int boundIndex = calculateBoundIndex(x, y);
        return setOccupiedByBoundIndex(boundIndex);
    }

    private int calculateBoundIndex(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return -1;

        return calculateIndex(x, y);
    }

    private int calculateIndex(int x, int y) {
        return x + width * y;
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
     * @param x
     *            coordinate
     * @param y
     *            coordinate
     * @return True if cell was not set as occupied.
     */
    public boolean isFree(int x, int y) {
        int value = getValueByXY(x, y);
        // it does not matter if cell has free space calculated or not to be free
        return value != OCCUPIED_VALUE;
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

    public int getFreeSpace(int x, int y) {
        // if cell is set as occupied, return it
        // if flood fill already calculated free space, return it
        int value = getValueByXY(x, y);
        if (value > UNSET_VALUE)
            return value;

        return getFreeSpaceByFloodFill(x, y);
    }

    private int getFreeSpaceByFloodFill(int startX, int startY) {
        floodFillStack[0] = startX;
        floodFillStack[1] = startY;
        int stackPosition = 2;

        while (stackPosition > 0) {
            int checkedX = floodFillStack[stackPosition - 2];
            int checkedY = floodFillStack[stackPosition - 1];
            stackPosition -= 2;

            int leftX = checkedX;
            while (fillIfUnset(leftX - 1, checkedY)) {
                leftX -= 1;
            }

            int rightX = checkedX;
            while (fillIfUnset(rightX, checkedY)) {
                rightX += 1;
            }

            stackPosition = scanAndQueue(leftX, rightX - 1, checkedY + 1, stackPosition);
            stackPosition = scanAndQueue(leftX, rightX - 1, checkedY - 1, stackPosition);
        }

        return countAndFill();
    }

    private boolean fillIfUnset(int x, int y) {
        int boundIndex = calculateBoundIndex(x, y);

        if (getValueByBoundIndex(boundIndex) == UNSET_VALUE) {
            // index is tested to be bound by operation above
            setValueByIndex(boundIndex, FILL_VALUE);
            return true;
        }
        return false;
    }

    private int scanAndQueue(int leftX, int rightX, int y, int stackPos) {
        boolean queued = false;
        for (int x = leftX; x <= rightX; ++x) {
            if (isSet(x, y)) {
                queued = false;
            } else if (!queued) {
                floodFillStack[stackPos] = x;
                floodFillStack[stackPos + 1] = y;
                stackPos += 2;
                queued = true;
            }
        }

        return stackPos;
    }

    private boolean isSet(int x, int y) {
        int value = getValueByXY(x, y);
        return value != UNSET_VALUE;
    }

    private int countAndFill() {
        int count = 0;
        for (int index = 0; index < spaceValues.length; ++index) {
            if (getValueByIndex(index) == FILL_VALUE)
                ++count;
        }

        if (count > 0) {
            for (int index = 0; index < spaceValues.length; ++index) {
                if (getValueByIndex(index) == FILL_VALUE)
                    setValueByIndex(index, count);
            }
        }

        return count;
    }
}

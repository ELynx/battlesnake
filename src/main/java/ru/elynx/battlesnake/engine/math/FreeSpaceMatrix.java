package ru.elynx.battlesnake.engine.math;

import java.util.Arrays;
import ru.elynx.battlesnake.api.CoordsDto;

public class FreeSpaceMatrix extends Matrix {
    private static final int OCCUPIED_VALUE = 0;
    private static final int UNSET_VALUE = -1;
    private static final int FILL_VALUE = -2;

    private static final int X_STACK_POSITION = 0;
    private static final int Y_STACK_POSITION = 1;
    private static final int STACK_SIZE_PER_ITEM = Y_STACK_POSITION + 1;

    private final int[] spaceValues;
    private final int[] floodFillStack;

    private FreeSpaceMatrix(int width, int height) {
        super(width, height);

        this.spaceValues = new int[width * height];
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

    // TODO leave only one
    public boolean setOccupied(CoordsDto coords) {
        return setOccupied(coords.getX(), coords.getY());
    }

    public boolean setOccupied(int x, int y) {
        int boundIndex = calculateBoundIndex(x, y);
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

    // TODO leave only one
    public boolean isFree(CoordsDto coords) {
        return isFree(coords.getX(), coords.getY());
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

    // TODO leave only one
    public int geFreeSpace(CoordsDto coords) {
        return getFreeSpace(coords.getX(), coords.getY());
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
        floodFillStack[X_STACK_POSITION] = startX;
        floodFillStack[Y_STACK_POSITION] = startY;
        int stackPosition = STACK_SIZE_PER_ITEM;

        while (stackPosition > 0) {
            int checkedX = floodFillStack[stackPosition + X_STACK_POSITION - STACK_SIZE_PER_ITEM];
            int checkedY = floodFillStack[stackPosition + Y_STACK_POSITION - STACK_SIZE_PER_ITEM];
            stackPosition -= STACK_SIZE_PER_ITEM;

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
                floodFillStack[stackPos + X_STACK_POSITION] = x;
                floodFillStack[stackPos + Y_STACK_POSITION] = y;
                stackPos += STACK_SIZE_PER_ITEM;
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

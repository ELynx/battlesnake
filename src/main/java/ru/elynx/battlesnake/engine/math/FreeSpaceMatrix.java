package ru.elynx.battlesnake.engine.math;

import java.util.Arrays;
import ru.elynx.battlesnake.entity.Coordinates;
import ru.elynx.battlesnake.entity.Dimensions;

public class FreeSpaceMatrix extends Matrix {
    private static final int OCCUPIED_VALUE = 0;
    private static final int UNSET_VALUE = -1;
    private static final int FLOOD_FILL_VALUE = -2;

    private static final int X_STACK_POSITION = 0;
    private static final int Y_STACK_POSITION = 1;
    private static final int STACK_SIZE_PER_ITEM = Y_STACK_POSITION + 1;

    private final int[] spaceValues;

    private final int[] floodFillStack;
    private int floodFillStackPosition;

    private FreeSpaceMatrix(Dimensions dimensions) {
        super(dimensions);

        this.spaceValues = new int[dimensions.getArea()];
        this.floodFillStack = new int[this.spaceValues.length * 2];
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
        initiateFloodFillAt(coordinates);
        preformFloodFill();
        return tallyFloodFilledCells();
    }

    private void initiateFloodFillAt(Coordinates coordinates) {
        floodFillStack[X_STACK_POSITION] = coordinates.getX();
        floodFillStack[Y_STACK_POSITION] = coordinates.getY();
        floodFillStackPosition = STACK_SIZE_PER_ITEM;
    }

    private void preformFloodFill() {
        while (hasFloodFillStackItems()) {
            floodFillByOneStackItem();
        }
    }

    private boolean hasFloodFillStackItems() {
        return floodFillStackPosition > 0;
    }

    private void floodFillByOneStackItem() {
        int currentX = floodFillStack[floodFillStackPosition + X_STACK_POSITION - STACK_SIZE_PER_ITEM];
        int currentY = floodFillStack[floodFillStackPosition + Y_STACK_POSITION - STACK_SIZE_PER_ITEM];
        floodFillStackPosition -= STACK_SIZE_PER_ITEM;

        int leftX = currentX;
        while (fillIfUnset(leftX - 1, currentY)) {
            leftX -= 1;
        }

        int rightX = currentX;
        while (fillIfUnset(rightX, currentY)) {
            rightX += 1;
        }

        scanAndQueue(leftX, rightX, currentY + 1);
        scanAndQueue(leftX, rightX, currentY - 1);
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
        for (int x = leftX; x < rightX; ++x) {
            if (isSet(x, y)) {
                queued = false;
            } else if (!queued) {
                floodFillStack[floodFillStackPosition + X_STACK_POSITION] = x;
                floodFillStack[floodFillStackPosition + Y_STACK_POSITION] = y;
                floodFillStackPosition += STACK_SIZE_PER_ITEM;
                queued = true;
            }
        }
    }

    private boolean isSet(int x, int y) {
        int value = getValueByXY(x, y);
        return value != UNSET_VALUE;
    }

    private int getValueByXY(int x, int y) {
        int boundIndex = calculateBoundIndex(x, y);
        return getValueByBoundIndex(boundIndex);
    }

    private int tallyFloodFilledCells() {
        int count = 0;
        for (int index = 0; index < boundIndexLimit(); ++index) {
            if (getValueByIndex(index) == FLOOD_FILL_VALUE)
                ++count;
        }

        if (count > 0) {
            for (int index = 0; index < boundIndexLimit(); ++index) {
                if (getValueByIndex(index) == FLOOD_FILL_VALUE)
                    setValueByIndex(index, count);
            }
        }

        return count;
    }

    private int boundIndexLimit() {
        return spaceValues.length;
    }
}

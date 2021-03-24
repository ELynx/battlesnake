package ru.elynx.battlesnake.engine.math;

public class FreeSpaceMatrix {
    private final int width;
    private final int height;
    private final int length;

    private final int sizeValue[];
    private final int sizeIndex[];

    protected FreeSpaceMatrix(int width, int height) {
        this.width = width;
        this.height = height;
        this.length = this.width * this.height;

        this.sizeValue = new int[this.length];
        this.sizeIndex = new int[this.length];
    }

    public static FreeSpaceMatrix uninitializedMatrix(int width, int height) {
        return new FreeSpaceMatrix(width, height);
    }

    public static FreeSpaceMatrix emptyFreeSpaceMatrix(int width, int height) {
        FreeSpaceMatrix result = uninitializedMatrix(width, height);
        result.empty();
        return result;
    }

    public void empty() {
        for (int i = 0; i < length; ++i) {
            // by default all cells are empty, and their space is kept in zero node
            sizeValue[i] = length;
            sizeIndex[i] = 0;
        }
    }

    public int getSpace(int x, int y) {
        final int index = safeIndex(x, y);
        if (index < 0)
            return 0; // outside has zero free space

        return unsafeGetSpace(index);
    }

    public boolean setOccupied(int x, int y) {
        final int index = safeIndex(x, y);
        if (index < 0)
            return false;

        unsafeSetOccupied(index);
        return true;
    }

    protected int unsafeIndex(int x, int y) {
        return x + width * y;
    }

    protected int safeIndex(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return -1;

        return unsafeIndex(x, y);
    }

    protected int unsafeGetSpace(int index) {
        return sizeValue[index];
    }

    protected void unsafeSetOccupied(int index) {
    }
}

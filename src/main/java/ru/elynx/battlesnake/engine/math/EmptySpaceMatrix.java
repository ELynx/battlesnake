package ru.elynx.battlesnake.engine.math;

public class EmptySpaceMatrix {
    private static final double DEFAULT_SPLASH = 2.0d;

    private final int width;
    private final int height;
    private final int length;

    protected EmptySpaceMatrix(int width, int height) {
        this.width = width;
        this.height = height;
        this.length = this.width * this.height;
    }

    public static EmptySpaceMatrix uninitializedMatrix(int width, int height) {
        return new EmptySpaceMatrix(width, height);
    }

    public static EmptySpaceMatrix emptySpaceMatrix(int width, int height) {
        EmptySpaceMatrix result = uninitializedMatrix(width, height);
        result.empty();
        return result;
    }

    public void empty() {
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
        return 0;
    }

    protected void unsafeSetOccupied(int index) {
    }
}

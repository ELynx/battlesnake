package ru.elynx.battlesnake.engine.math;

public class FreeSpaceMatrix {
    private final static int UNSET_VALUE = -1;
    private final static int FILL_VALUE = -2;

    private final int width;
    private final int height;
    private final int length;

    private final int[] spaceValues;
    private final int[] stack;

    protected FreeSpaceMatrix(int width, int height) {
        this.width = width;
        this.height = height;
        this.length = this.width * this.height;

        this.spaceValues = new int[this.length];
        this.stack = new int[1024];
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
            spaceValues[i] = UNSET_VALUE;
        }
    }

    private boolean inside(int x, int y) {
        final int insideIndex = safeIndex(x, y);
        if (insideIndex < 0) {
            return false;
        }
        return spaceValues[insideIndex] == UNSET_VALUE;
    }

    private boolean insideAndSet(int x, int y) {
        final int insideIndex = safeIndex(x, y);
        if (insideIndex < 0) {
            return false;
        }
        if (spaceValues[insideIndex] == UNSET_VALUE) {
            spaceValues[insideIndex] = FILL_VALUE;
            return true;
        }
        return false;
    }

    private int post() {
        int filled = 0;
        for (int i = 0; i < length; ++i) {
            if (spaceValues[i] == FILL_VALUE)
                ++filled;
        }

        if (filled > 0) {
            for (int i = 0; i < length; ++i) {
                if (spaceValues[i] == FILL_VALUE)
                    spaceValues[i] = filled;
            }
        }

        return filled;
    }

    public int getSpaceImpl(int xIn, int yIn) {
        stack[0] = xIn;
        stack[1] = xIn;
        stack[2] = yIn;
        stack[3] = 1;

        stack[4] = xIn;
        stack[5] = xIn;
        stack[6] = yIn - 1;
        stack[7] = -1;

        int stackPos = 8;
        while (stackPos > 0) {
            int x1 = stack[stackPos - 4];
            int x2 = stack[stackPos - 3];
            int y = stack[stackPos - 2];
            int dy = stack[stackPos - 1];
            stackPos -= 4;

            int x = x1;

            if (inside(x, y)) {
                while (insideAndSet(x - 1, y)) {
                    x -= 1;
                }
            }

            if (x < x1) {
                stack[stackPos] = x;
                stack[stackPos + 1] = x1 - 1;
                stack[stackPos + 2] = y - dy;
                stack[stackPos + 3] = -dy;
                stackPos += 4;
            }

            while (x1 < x2) {
                while (insideAndSet(x1, y)) {
                    x1 += 1;
                }

                stack[stackPos] = x;
                stack[stackPos + 1] = x1 - 1;
                stack[stackPos + 2] = y + dy;
                stack[stackPos + 3] = dy;
                stackPos += 4;

                if (x1 - 1 > x2) {
                    stack[stackPos] = x2 + 1;
                    stack[stackPos + 1] = x1 - 1;
                    stack[stackPos + 2] = y - dy;
                    stack[stackPos + 3] = -dy;
                    stackPos += 4;
                }

                while (x1 < x2 && !inside(x1, y)) {
                    x1 += 1;
                }

                x = x1;
            }
        }

        return post();
    }

    public int getSpace(int x, int y) {
        final int index = safeIndex(x, y);
        if (index < 0)
            return 0; // outside has zero free space

        final int current = spaceValues[index];
        if (current >= 0) {
            return current;
        }

        return getSpaceImpl(x, y);
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

    protected void unsafeSetOccupied(int index) {
        spaceValues[index] = 0;
    }
}

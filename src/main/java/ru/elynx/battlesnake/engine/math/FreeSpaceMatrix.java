package ru.elynx.battlesnake.engine.math;

import java.util.function.Consumer;
import java.util.function.Predicate;
import org.javatuples.Pair;

public class FreeSpaceMatrix {
    private final int width;
    private final int height;
    private final int length;

    private final int[] spaceValues;

    protected FreeSpaceMatrix(int width, int height) {
        this.width = width;
        this.height = height;
        this.length = this.width * this.height;

        this.spaceValues = new int[this.length];
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
            // by default all cells are uninitialized
            spaceValues[i] = -1;
        }
    }

    public int getSpace(int x, int y) {
        final int index = safeIndex(x, y);
        if (index < 0)
            return 0; // outside has zero free space

        final int current = spaceValues[index];
        if (current >= 0) {
            return current;
        }

        // inside by being -1
        Predicate<Pair<Integer, Integer>> inside = pair -> {
            final int insideIndex = safeIndex(pair.getValue0(), pair.getValue1());
            if (insideIndex < 0) {
                return false;
            }
            return spaceValues[insideIndex] == -1;
        };

        // set to -2 as marked
        Consumer<Pair<Integer, Integer>> set = pair -> {
            final int setIndex = unsafeIndex(pair.getValue0(), pair.getValue1());
            spaceValues[setIndex] = -2;
        };

        // TODO algorithm

        // count all -2s
        int tagged = 0;
        for (int i = 0; i < length; ++i) {
            if (spaceValues[i] == -2)
                ++tagged;
        }

        // write found space
        for (int i = 0; i < length; ++i) {
            if (spaceValues[i] == -2)
                spaceValues[i] = tagged;
        }

        return tagged;
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

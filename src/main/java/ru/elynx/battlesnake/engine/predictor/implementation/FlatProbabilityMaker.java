package ru.elynx.battlesnake.engine.predictor.implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.javatuples.Triplet;

public class FlatProbabilityMaker {
    private int[] stack;
    private int stackPos;

    public FlatProbabilityMaker() {
        stack = new int[8];
        stackPos = 0;
    }

    public void reset() {
        stackPos = 0;
    }

    public void add(int x, int y) {
        stack[stackPos] = x;
        stack[stackPos + 1] = y;
        stackPos += 2;
    }

    public List<Triplet<Integer, Integer, Double>> make() {
        if (stackPos == 0)
            return Collections.emptyList();

        List<Triplet<Integer, Integer, Double>> result = new ArrayList<>(stackPos / 2);

        // 1 / (size / 2) -> 2 / size
        double p = 2.0 / (double) stackPos;
        for (int i = 0; i < stackPos; i += 2) {
            result.add(new Triplet<>(stack[i], stack[i + 1], p));
        }

        return result;
    }
}
